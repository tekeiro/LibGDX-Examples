package org.keirobm.libgdx.test.renderer;

import org.keirobm.libgdx.test.characters.CharacterSprite;
import org.keirobm.libgdx.test.config.Assets;
import org.keirobm.libgdx.test.config.Constant;
import org.keirobm.libgdx.test.input.TouchpadMoveListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;


public class HUDRenderer implements Disposable {
	//------  FIELDS  ----------
	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Stage stage;
	private Label labelFPS;
	private Label labelTile;
	private Label lbTouchpad;
	private Touchpad touchpad;
	
	private Table tableBottom;
	private Table tableTop;
	
	private TouchpadMoveListener touchpadListener = null;
	
	private TiledMap map;
	private CharacterSprite character;
	
	private float pos_x;
	private float pos_y;
	
	//----  CONSTRUCTOR  ------
	public HUDRenderer(SpriteBatch batch, OrthographicCamera camera, 
			Viewport viewport, TiledMap map, CharacterSprite character) {
		this.spriteBatch = batch;
		this.camera = camera;
		this.viewport = viewport;
		this.map = map;
		this.character = character;
	}
	
	//------  PROPERTIES  ------
	public void setTouchpadMoveListener(TouchpadMoveListener touchpadListener) {
		this.touchpadListener = touchpadListener;
	}
	
	//---  METHODS  ---
	public void create() {
		initializeUI();
	}
	
	private Vector2 characterTilePosition(boolean flipY) {		
		Vector2 pos = character.getPosition();
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		
		int tileX = (int) (pos.x / layer.getTileWidth());
		int tileY = (int) (pos.y / layer.getTileHeight());
		if (flipY)
			tileY = (layer.getHeight()-1) - tileY;
		
		return new Vector2(tileX, tileY);
	}
	
	private String analyzeTiles(Vector2 tile) {
		String res = "";
		
		for (int l=0; l<map.getLayers().getCount(); l++) {
			TiledMapTileLayer.Cell cell = ((TiledMapTileLayer)map.getLayers().get(l)).getCell((int)tile.x, (int)tile.y);
			if (cell != null)
				res += String.valueOf(cell.getTile().getId()) + ",";
		}
		
		return res;
	}
	
	private void initializeUI() {
		tableTop = new Table();
		tableTop.top();
		
		Label.LabelStyle lbStyle = new Label.LabelStyle(Assets.instance.Font, Color.WHITE);
		labelFPS = new Label("FPS: ", lbStyle);
		labelFPS.setFontScale(0.5f);
		tableTop.add(labelFPS).left().top().expandX();
		
		labelTile = new Label("Tile: ", lbStyle);
		labelTile.setFontScale(0.5f);
		tableTop.add(labelTile).right().top().expandX();
		
		tableBottom = new Table();
		tableBottom.bottom();
		
		Touchpad.TouchpadStyle tchStyle = new Touchpad.TouchpadStyle();
		tchStyle.background = new TextureRegionDrawable(new TextureRegion(Assets.instance.Touchpad_Background));
		tchStyle.knob = new TextureRegionDrawable(new TextureRegion(Assets.instance.Touchpad_Knob));
		touchpad = new Touchpad(Constant.TOUCHPAD_DEADZONE_RADIUS, tchStyle);
		Color color = touchpad.getColor();
		touchpad.setColor(color.r, color.g, color.b, 0.5f);
		tableBottom.add(touchpad).left().bottom().expandX();
		
		lbTouchpad = new Label("Touchpad: ", lbStyle);
		lbTouchpad.setFontScale(0.5f);
		tableBottom.add(lbTouchpad).right().bottom().expandX();	
		
		stage = new Stage(viewport);
		stage.setDebugAll(false);
		Gdx.input.setInputProcessor(stage);
		
		stage.addActor(tableTop);
		stage.addActor(tableBottom);
	}
	
	public void render() {
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		
		int fps = Gdx.graphics.getFramesPerSecond();
		Vector2 charTile = characterTilePosition(true);
		
		String fps_str = String.format("FPS: %d", fps);	
		String tile_str = String.format("Tile: %s", charTile.toString());	
		
		float width = stage.getWidth();
		float height = stage.getHeight();
		pos_x = 0f;
		pos_y = height - labelFPS.getHeight();
		labelFPS.setText(fps_str);
//		labelFPS.setPosition(pos_x, pos_y );
		
		labelTile.setText(tile_str);
		pos_x = width - labelTile.getWidth();
		pos_y = 0f;
//		labelTile.setPosition(pos_x, pos_y);
		
		pos_x = 0f;
		pos_y = 0f;
//		touchpad.setPosition(pos_x, pos_y);
		
		float knobX = touchpad.getKnobPercentX(), knobY = touchpad.getKnobPercentY();
		String knob_str = String.format("X: %f, Y: %f", knobX, knobY);
		lbTouchpad.setText(knob_str);
		pos_x = width - lbTouchpad.getWidth();
		pos_y = height - lbTouchpad.getHeight();
//		lbTouchpad.setPosition(pos_x, pos_y);
		
		tableBottom.setPosition(0, 0);
		tableBottom.setFillParent(true);
		tableBottom.pack();
		
		tableTop.setPosition(0f, height - tableTop.getHeight());
		tableTop.setFillParent(true);
		tableTop.pack();
		
		if (touchpadListener != null) {
			touchpadListener.touchpadMoved(
				touchpad.getKnobX(),
				touchpad.getKnobY(),
				touchpad.getKnobPercentX(),
				touchpad.getKnobPercentY()
			);
		}
		
		stage.draw();
		
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
}
