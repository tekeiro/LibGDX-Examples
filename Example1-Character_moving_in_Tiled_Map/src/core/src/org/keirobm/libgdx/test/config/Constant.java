package org.keirobm.libgdx.test.config;

import com.badlogic.gdx.math.Vector2;

public class Constant {
	/******  Constants for Viewport  **********/
	// Viewport width in world units
	public static final float VIRTUAL_WIDTH = 800.0f;
	
	// Viewport height in world units
	public static final float VIRTUAL_HEIGHT = 600.0f;
	
	// Max width size of supported screen
	public static final float MAX_SCENE_WIDTH = 1920.0f;
	
	// Max height size of supported screen
	public static final float MAX_SCENE_HEIGHT = 1080.0f;
	
	/******  Constants for Camera ********/	
	// Movement's speed of camera
	public static final float CAMERA_SPEED = 100.0f;
	
	// Camera zoom speed
	public static final float CAMERA_ZOOM_SPEED = 5.0f;
	
	// Min level of zoom
	public static final float CAMERA_ZOOM_MIN = 0.01f;
	
	// Max level of zoom
	public static final float CAMERA_ZOOM_MAX = 1.5f;	
	
	/***** Constants for Gesture Detector  ***********/
	public static final float HALF_TAP_SQUARE_SIZE = 20.0f;
	
	public static final float TAP_COUNT_INTERVAL = 0.4f;
	
	public static final float LONG_PRESS_DURATION = 1.1f;
	
	public static final float MAX_FLING_DELAY = 0.15f;
	
	/**** Constants for Character Sprite and Animation ********/
	public static final int CHAR_SPRITE_WIDTH = 32;
	public static final int CHAR_SPRITE_HEIGHT = 32;
	
	public static final float CHAR_MOVE_FRAME_DURATION = 0.2f;
	
	public static final Vector2 CHAR_SPEED = new Vector2(100.0f, 100.0f);
	
	public static final String CHAR_MOVE_UP = "up";
	public static final String CHAR_MOVE_DOWN = "down";
	public static final String CHAR_MOVE_LEFT = "left";
	public static final String CHAR_MOVE_RIGHT = "right";
	
	/*****  Constants for Widgets  ************/
	public static final float TOUCHPAD_DEADZONE_RADIUS = 10.0f;
}
