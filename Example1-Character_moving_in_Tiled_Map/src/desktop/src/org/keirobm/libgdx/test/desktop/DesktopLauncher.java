package org.keirobm.libgdx.test.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.keirobm.libgdx.test.LibGDXTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Example of Character Moving";
		new LwjglApplication(new LibGDXTest(), config);
	}
}
