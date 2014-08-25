package uk.josephmansfield.cwtf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.josephmansfield.cwtf.CwtfGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 1200;
        config.height = 600;
		new LwjglApplication(new CwtfGame(), config);
	}
}
