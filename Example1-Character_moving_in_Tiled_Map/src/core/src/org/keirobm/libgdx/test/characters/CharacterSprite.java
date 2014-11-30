package org.keirobm.libgdx.test.characters;

import org.keirobm.libgdx.test.config.Assets;
import org.keirobm.libgdx.test.config.Constant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.Viewport;


public class CharacterSprite {
	public static interface ChangeAnimationKey {
		public String changeAnimation(CharacterSprite character);
	}
	
	//------  FIELDS  --------------
	private Vector2 position;
	private Vector2 direction;
	private Vector2 origin;
	private Vector2 scale;
	private float rotation;
	
	private ArrayMap<String, Animation> animations;
	private float time;
	private String currentAnimationKey;
	ChangeAnimationKey changeAnimationHandler = null;
	
	//------  CONSTRUCTOR  ---------
	public CharacterSprite(ArrayMap<String, Animation> animations, String firstAnimationKey) {
		position = new Vector2();
		direction = new Vector2();
		
		time = 0.0f;
		this.animations = animations;		
		currentAnimationKey = firstAnimationKey;
		
		TextureRegion firstFrame = getCurrentFrame();
		origin = new Vector2(
			firstFrame.getRegionWidth() * 0.5f,
			firstFrame.getRegionHeight() * 0.5f
		);
		scale = new Vector2(1.0f, 1.0f);
		rotation = 0.0f;
	}
	
	//------  PROPERTIES  --------
	public Vector2 getPosition() {
		return position.cpy();
	}
	public float getPositionX() { return position.x; }
	public float getPositionY() { return position.y; }
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public Vector2 getDirection() {
		return direction.cpy();
	}
	public float getDirectionX() { return direction.x; }
	public float getDirectionY() { return direction.y; }
	public void setDirection(float directionX, float directionY) {
		direction.set(directionX, directionY);
	}
	
	public Vector2 getOrigin() { return origin.cpy(); }
	public float getOriginX() { return origin.x; }
	public float getOriginY() { return origin.y; }
	public void setOrigin(float originX, float originY) {
		origin.set(originX, originY);
	}
	
	public Vector2 getScale() { return scale.cpy(); }
	public float getScaleX() { return scale.x; }
	public float getScaleY() { return scale.y; }
	public void setScale(float scaleX, float scaleY) {
		scale.set(scaleX, scaleY);
	}
	
	public float getRotation() { return rotation; }
	public void setRotation(float rotation) {
		this.rotation = MathUtils.clamp(rotation, 0.0f, 360.0f); 
	}
	
	public void setChangeAnimationKeyHandler(ChangeAnimationKey changeAnimationKeyHandler) {
		this.changeAnimationHandler = changeAnimationKeyHandler;
	}
	
	public Animation getCurrentAnimation() {
		return animations.get(currentAnimationKey);
	}
	public float getCurrentTime() {
		return time;
	}
	
	public TextureRegion getCurrentFrame() {
		return getCurrentAnimation().getKeyFrame(time);
	}
	
	//------  METHODS  ----------
	public void move(float amountX, float amountY) {
//		direction.x = (amountX >= 0) ? ((amountX == 0) ? 0 : 1) : -1;
//		direction.y = (amountY >= 0) ? ((amountY == 0) ? 0 : 1) : -1;
		direction.set(amountX, amountY);
		
		position.x += amountX;
		position.y += amountY;
	}
	
	public Vector2 calculateMovementPosition(float amountX, float amountY) {
		Vector2 pos = position.cpy();
		return pos.add(amountX, amountY);
	}
	
	public void update() {
		String new_direction = null;
		if (changeAnimationHandler != null)
			new_direction = changeAnimationHandler.changeAnimation(this);	
		
		if (new_direction != null) {
			if (new_direction == currentAnimationKey) {				
				time += Gdx.graphics.getDeltaTime();
			}
			else {
				currentAnimationKey = new_direction;
				time = 0.0f;
			}
		}
		
	}
	
	public void render(SpriteBatch batch) {
		TextureRegion frame = getCurrentFrame();
		batch.draw(
			frame,
			position.x - origin.x, position.y - origin.y,
			origin.x, origin.y,
			frame.getRegionWidth(), frame.getRegionHeight(),
			scale.x, scale.y,
			rotation
		);
	}
}
