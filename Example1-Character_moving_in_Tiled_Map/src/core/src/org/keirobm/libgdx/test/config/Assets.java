package org.keirobm.libgdx.test.config;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {
	//----- FILE PATH -----
	// Path to map
	public static final String MAP_PATH = "data/maps/ejemplo2.tmx";
	
	// Path to font
	public static final String FONT_PATH = "data/fonts/play.fnt";
	
	public static final String CHARACTER_TEXTURE_PATH = "data/sprites/character_spritesheet.png";
	
	public static final String TOUCHPAD_BACKGROUND_PATH = "data/widgets/touchpad_background.png";
	public static final String TOUCHPAD_KNOB_PATH = "data/widgets/touchpad_knob.png";
	
	//----  SINGLETON INSTANCE ---
	public static final Assets instance = new Assets();
	
	//----  FIELDS  -----
	private AssetManager assetManager;
	
	//---  ASSET ---
	public final TiledMap Map;
	public final BitmapFont Font;
	public final Texture Character_Spritesheet;
	public final CharacterRegions CharacterRegions;
	public final Texture Touchpad_Background;
	public final Texture Touchpad_Knob;
	
	//--- SUBCLASSES ---
	public static class CharacterRegions {
		private CharacterRegions(Texture texture) {
			Up = new Array<TextureRegion>();
			Down = new Array<TextureRegion>();
			Left = new Array<TextureRegion>();
			Right = new Array<TextureRegion>();
			
			int x=0, y=0;
			int spriteW = Constant.CHAR_SPRITE_WIDTH, spriteH=Constant.CHAR_SPRITE_HEIGHT;
			for (int i=0; i<3; i++) {
				Down.add(new TextureRegion(texture, x, y, spriteW, spriteH));
				x += spriteW;
			}
			x = 0;
			y += spriteH;
			for (int i=0; i<3; i++) {
				Left.add(new TextureRegion(texture, x, y, spriteW, spriteH));
				x += spriteW;
			}
			x = 0;
			y += spriteH;
			for (int i=0; i<3; i++) {
				Right.add(new TextureRegion(texture, x, y, spriteW, spriteH));
				x += spriteW;
			}
			x = 0;
			y += spriteH;
			for (int i=0; i<3; i++) {
				Up.add(new TextureRegion(texture, x, y, spriteW, spriteH));
				x += spriteW;
			}
		}
		
		public final Array<TextureRegion> Up;
		public final Array<TextureRegion> Down;
		public final Array<TextureRegion> Left;
		public final Array<TextureRegion> Right;
	}
	
	//---  CONSTRUCTOR ---
	private Assets() {
		assetManager = new AssetManager();
		
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load(MAP_PATH, TiledMap.class);
		assetManager.load(FONT_PATH, BitmapFont.class);
		assetManager.load(CHARACTER_TEXTURE_PATH, Texture.class);
		assetManager.load(TOUCHPAD_BACKGROUND_PATH, Texture.class);
		assetManager.load(TOUCHPAD_KNOB_PATH, Texture.class);
		assetManager.finishLoading();
		
		Map = assetManager.get(MAP_PATH, TiledMap.class);
		Font = assetManager.get(FONT_PATH, BitmapFont.class);
		Character_Spritesheet = assetManager.get(CHARACTER_TEXTURE_PATH, Texture.class);
		CharacterRegions = new CharacterRegions(Character_Spritesheet);
		Touchpad_Background = assetManager.get(TOUCHPAD_BACKGROUND_PATH, Texture.class);
		Touchpad_Knob = assetManager.get(TOUCHPAD_KNOB_PATH, Texture.class);
	}
	
	//----  METHODS  -----
	@Override
	public void dispose() {
		assetManager.clear();
		assetManager.dispose();
	}
}
