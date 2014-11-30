package org.keirobm.libgdx.test;

import org.keirobm.libgdx.test.config.Constant;
import org.keirobm.libgdx.test.renderer.HUDRenderer;
import org.keirobm.libgdx.test.renderer.WorldRenderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LibGDXTest extends ApplicationAdapter {
	public static final String TAG = LibGDXTest.class.getName();
	
	private SpriteBatch batch;
	private OrthographicCamera camera;	
	private Viewport viewport;
	
	private OrthographicCamera cameraHUD;
	private Viewport viewportHUD;
	
	private WorldRenderer worldRenderer;
	private HUDRenderer hudRenderer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();		
		viewport = new StretchViewport(
				Constant.VIRTUAL_WIDTH, Constant.VIRTUAL_HEIGHT,
//				Constant.MAX_SCENE_WIDTH, Constant.MAX_SCENE_HEIGHT,
				camera);
		
		cameraHUD = new OrthographicCamera();
		viewportHUD = new StretchViewport(
				Constant.VIRTUAL_WIDTH, Constant.VIRTUAL_HEIGHT,
//				Constant.MAX_SCENE_WIDTH, Constant.MAX_SCENE_HEIGHT,
				cameraHUD);
		
		worldRenderer = new WorldRenderer(camera, viewport, batch);
		worldRenderer.create();
		
		hudRenderer = new HUDRenderer(batch, cameraHUD, viewportHUD,
				worldRenderer.getMap(), worldRenderer.getCharacter());
		hudRenderer.create();
		hudRenderer.setTouchpadMoveListener(
			worldRenderer.getTouchpadMoveListener()
		);		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		worldRenderer.render();
		hudRenderer.render();
		
		Gdx.app.log(TAG, "Camera Position: " + camera.position.toString());
		Gdx.app.log(TAG, "CameraHUD Position: " + cameraHUD.position.toString());
	}
	
	@Override
	public void dispose() {
		worldRenderer.dispose();
		hudRenderer.dispose();
	}
	
	public void resize(int width, int height) {	
		viewport.update(width, height);
		viewportHUD.update(width, height);
	}
}
