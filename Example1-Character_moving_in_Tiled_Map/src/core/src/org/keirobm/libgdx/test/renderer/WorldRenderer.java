package org.keirobm.libgdx.test.renderer;

import org.keirobm.libgdx.test.characters.CharacterSprite;
import org.keirobm.libgdx.test.config.Assets;
import org.keirobm.libgdx.test.config.Constant;
import org.keirobm.libgdx.test.input.TouchpadMoveListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldRenderer implements Disposable {
	public static final String TAG = WorldRenderer.class.getName();
	
	//-------  FIELDS  --------
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Vector2 direction;
	
	private GestureDetector gestureDetector;
	
	private CharacterSprite character;	
	
	//------ CONSTRUCTOR  -------
	public WorldRenderer(OrthographicCamera camera, Viewport viewport, SpriteBatch batch) {
		this.camera = camera;
		this.viewport = viewport;
		this.batch = batch;
	}
	
	//-------  PROPERTIES  -----
	public TiledMap getMap() { return map; }
	public CharacterSprite getCharacter() { return character; }
	
	public Vector2 posToTile(float x, float y, boolean flipY) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		int tileX = (int) (x / layer.getTileWidth());
		int tileY = (int) (y / layer.getTileHeight());
		if (flipY)
			tileY = (layer.getHeight()-1) - tileY;
		return new Vector2(tileX, tileY);
	}
	
	public Vector2 tileToPos(int tileX, int tileY, boolean flipY) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		float x = tileX * layer.getTileWidth();
		float y;
		if (flipY)
			y = (layer.getHeight() - tileY) * layer.getTileHeight();
		else
			y = tileY * layer.getTileHeight();
		return new Vector2(x, y);
	}
	
	public TouchpadMoveListener getTouchpadMoveListener() {
		return this.touchpadMoveListener;
	}
	
	//------  LIFE CYCLE METHODS -----
	public void create() {
		map = Assets.instance.Map;
		renderer = new OrthogonalTiledMapRenderer(map);
		direction = new Vector2();
		
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		camera.position.y = layer.getHeight() * layer.getTileHeight() - viewport.getWorldHeight() * 0.5f;
		camera.update();
		
		gestureDetector = new GestureDetector(
				Constant.HALF_TAP_SQUARE_SIZE,
				Constant.TAP_COUNT_INTERVAL,
				Constant.LONG_PRESS_DURATION,
				Constant.MAX_FLING_DELAY,
				new WorldRendererGestureHandler()
		);
		Gdx.input.setInputProcessor(gestureDetector);
		
		createCharacterSprite();
	}
	
	public void render() {
		updateCharacter();
		//updateCamera();
		
		renderer.setView(camera);
		renderer.render();
		
		character.update();
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		character.render(batch);
		batch.end();
	}
	
	public void dispose() {
		renderer.dispose();
	}
	//------  CAMERA METHODS  --------
	private void adjustCameraPosition() {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		
		float cameraMinX = viewport.getWorldWidth() * 0.5f;
		float cameraMinY = viewport.getWorldHeight() * 0.5f;
		float cameraMaxX = layer.getWidth() * layer.getTileWidth() - 
				cameraMinX * camera.zoom;
		float cameraMaxY = layer.getHeight() * layer.getTileHeight() -
				cameraMinY * camera.zoom;
		
		camera.position.x = MathUtils.clamp(camera.position.x, 
				cameraMinX * camera.zoom, cameraMaxX);
		camera.position.y = MathUtils.clamp(camera.position.y,
				cameraMinY * camera.zoom, cameraMaxY);
	}
	
	private void adjustCameraZoom() {
		camera.zoom = MathUtils.clamp(
			camera.zoom,
			Constant.CAMERA_ZOOM_MIN,
			Constant.CAMERA_ZOOM_MAX
		);
	}
	
	private TouchpadMoveListener touchpadMoveListener = new TouchpadMoveListener() {
		@Override
		public void touchpadMoved(float knobX, float knobY, float knobPercentX,
				float knobPercentY) {
			float deltaTime = Gdx.graphics.getDeltaTime();
			knobPercentX *= Constant.CHAR_SPEED.x * deltaTime;
			knobPercentY *= Constant.CHAR_SPEED.y * deltaTime;
			
			if (canMove(knobPercentX, knobPercentY)) {
				character.move(knobPercentX, knobPercentY);
				camera.position.set(character.getPosition(), 0.0f);			
				adjustCharacterPosition();
			}
			
			adjustCameraPosition();
			camera.update();
			character.update();
		}
	};
	
	public void updateCamera() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		direction.set(0.0f, 0.0f);
		
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		// Check camera movement
		if (Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.input.isTouched() 
				&& mouseX < width * 0.25f)) {
			direction.x = -1;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.input.isTouched()
				&& mouseX > width * 0.75f)) {
			direction.x = 1;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP) || (Gdx.input.isTouched()
				&& mouseY < height * 0.25f)) {
			direction.y = 1;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN) || (Gdx.input.isTouched()
				&& mouseY > height * 0.75f)) {
			direction.y = -1;
		}
		
		// Check camera zoom
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom += Constant.CAMERA_ZOOM_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom -= Constant.CAMERA_ZOOM_SPEED * deltaTime;
		}
		adjustCameraZoom();
		
		direction.nor().scl(Constant.CAMERA_SPEED * deltaTime);
		camera.position.x += direction.x;
		camera.position.y += direction.y;
		
		// We do not want user to be able to scroll past the map limits
		adjustCameraPosition();		
		
		camera.update();		
	}
	
	//------  CHARACTER & MAP METHODS  -------
	private void adjustCharacterPosition() {
		Vector2 pos = character.getPosition();
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		Vector2 threshold = new Vector2(Constant.CHAR_SPRITE_WIDTH * 0.5f, Constant.CHAR_SPRITE_HEIGHT * 0.5f);
		
		if (pos.x <= 0 + threshold.x)
			pos.x = 0 + threshold.x;
		if (pos.x >= (layer.getWidth() * layer.getTileWidth()) - threshold.x)
			pos.x = (layer.getWidth() * layer.getTileWidth()) - threshold.x;
		if (pos.y <= 0 + threshold.y)
			pos.y = 0 + threshold.y;
		if (pos.y >= (layer.getHeight() * layer.getTileHeight()) - threshold.y)
			pos.y = (layer.getHeight() * layer.getTileHeight()) - threshold.y;
		
		character.setPosition(pos.x, pos.y);
	}
	
	private boolean canMove(float amountX, float amountY) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		Vector2 position = character.calculateMovementPosition(amountX, amountY);
		Vector2 tile = posToTile(position.x, position.y, false);
		
		for (int l=0; l<map.getLayers().getCount(); l++) {
			TiledMapTileLayer currentLayer = (TiledMapTileLayer)map.getLayers().get(l);
			TiledMapTileLayer.Cell cell = currentLayer.getCell((int)tile.x, (int)tile.y);
			if (cell != null) {
				if (cell.getTile().getProperties().containsKey("traspasable"))
					continue;
				else
					return false;
			}
		}
		return true;
	}
	
	public void updateCharacter() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		float speedIncrement = 1.0f;
		
		direction.set(0.0f, 0.0f);
		
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
			speedIncrement = 3.0f;
		}
		
		// Check character movement
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			direction.x = -1;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			direction.x = 1;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			direction.y = 1;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			direction.y = -1;
		}
		
		// Check camera zoom
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom += Constant.CAMERA_ZOOM_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom -= Constant.CAMERA_ZOOM_SPEED * deltaTime;
		}
		adjustCameraZoom();
		
		direction.nor();
		direction.x *= Constant.CHAR_SPEED.x * speedIncrement * deltaTime;
		direction.y *= Constant.CHAR_SPEED.y * speedIncrement * deltaTime;
		if (canMove(direction.x, direction.y)) {
			character.move(direction.x, direction.y);
			camera.position.set(character.getPosition(), 0.0f);			
			adjustCharacterPosition();
		}
		
		adjustCameraPosition();
		camera.update();
	}
	
	//------  PRIVATE METHODS  ---------
	private void createCharacterSprite() {
		ArrayMap<String, Animation> animations = new ArrayMap<String, Animation>();
		int x=0, y=0;
		int width=Constant.CHAR_SPRITE_WIDTH, height=Constant.CHAR_SPRITE_HEIGHT;
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		
		animations = new ArrayMap<String, Animation>();
		animations.put(
			Constant.CHAR_MOVE_UP, 
			new Animation(
				Constant.CHAR_MOVE_FRAME_DURATION,
				Assets.instance.CharacterRegions.Up,
				PlayMode.LOOP
			)
		);
		animations.put(
			Constant.CHAR_MOVE_DOWN, 
			new Animation(
				Constant.CHAR_MOVE_FRAME_DURATION,
				Assets.instance.CharacterRegions.Down,
				PlayMode.LOOP
			)
		);
		animations.put(
			Constant.CHAR_MOVE_LEFT, 
			new Animation(
				Constant.CHAR_MOVE_FRAME_DURATION,
				Assets.instance.CharacterRegions.Left,
				PlayMode.LOOP
			)
		);
		animations.put(
			Constant.CHAR_MOVE_RIGHT, 
			new Animation(
				Constant.CHAR_MOVE_FRAME_DURATION,
				Assets.instance.CharacterRegions.Right,
				PlayMode.LOOP
			)
		);
		
		int tileOriginX = Integer.parseInt((String)map.getProperties().get("tileOriginX"));
		int tileOriginY = Integer.parseInt((String)map.getProperties().get("tileOriginY"));
		Vector2 pos = tileToPos(tileOriginX, tileOriginY, true);
		
		character = new CharacterSprite(animations, Constant.CHAR_MOVE_DOWN);
		character.setPosition(
			pos.x,
			pos.y
		);		
		character.setChangeAnimationKeyHandler(new CharacterSprite.ChangeAnimationKey() {			
			@Override
			public String changeAnimation(CharacterSprite character) {
				Vector2 direction = character.getDirection();
				float threshold = 0.4f;
				if (direction.x > threshold)
					return Constant.CHAR_MOVE_RIGHT;
				else if (direction.x < -threshold)
					return Constant.CHAR_MOVE_LEFT;
				if (direction.y > threshold)
					return Constant.CHAR_MOVE_UP;
				else if (direction.y < -threshold)
					return Constant.CHAR_MOVE_DOWN;
				return null;
			}
		});
		camera.position.set(character.getPositionX(), character.getPositionY(), 0.0f);
		camera.update();
	}
	
	//------  INPUT METHODS  -----------	
	private class WorldRendererGestureHandler extends GestureAdapter {

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			Gdx.app.log(TAG, String.format("touchDown -> x:%f y:%f pointer:%d button:%d",
					x, y, pointer, button));
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			Gdx.app.log(TAG, String.format("tap -> x:%f y:%f count:%d button:%d",
					x, y, count, button));
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			Gdx.app.log(TAG, String.format("longPress -> x:%f y:%f",
					x, y));
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			Gdx.app.log(TAG, String.format("fling -> velX:%f velY:%f button:%d",
					velocityX, velocityY, button));			
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			Gdx.app.log(TAG, String.format("pan -> x:%f y:%f deltaX:%f deltaY:%f",
					x, y, deltaX, deltaY));
//			float scale = 0.8f;
//			camera.position.x += deltaX * scale ;
//			camera.position.y += -deltaY * scale ;
//			adjustCameraPosition();
//			camera.update();
			
//			Vector2 direction = new Vector2(deltaX * scale, -deltaY * scale);
//			if (canMove(direction.x, direction.y)) {
//				character.move(direction.x, direction.y);
//				camera.position.set(character.getPosition(), 0.0f);			
//				adjustCharacterPosition();
//			}
//			
//			adjustCameraPosition();
//			camera.update();
//			character.update();
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			Gdx.app.log(TAG, String.format("panStop -> x:%f y:%f pointer:%d button:%d",
					x, y, pointer, button));
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			Gdx.app.log(TAG, String.format("zoom -> initialDistance:%f distance:%f",
					initialDistance, distance));
			float diff = initialDistance - distance;
			float scale = 0.005f;
			
			camera.zoom += diff * scale * Gdx.graphics.getDeltaTime();
			adjustCameraZoom();
			camera.update();
			
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
				Vector2 pointer1, Vector2 pointer2) {
			Gdx.app.log(TAG, String.format("pinch -> initialPointer1:%s initialPointer2:%s pointer1:%s pointer2:%s",
					initialPointer1.toString(),
					initialPointer2.toString(),
					pointer1.toString(),
					pointer2.toString()
					)
			);
			return false;
		}
		
	}
}
