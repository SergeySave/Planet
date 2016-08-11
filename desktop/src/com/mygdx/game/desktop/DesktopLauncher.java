package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1200;
		config.height = 900;
		config.useHDPI = true;
		config.vSyncEnabled = true;
		//config.addIcon("images/iconTestBMP.bmp", FileType.Internal);
		//config.addIcon("images/iconTestBMP1.bmp", FileType.Internal);
		//config.addIcon("images/iconTest.png", FileType.Internal);
		//config.addIcon("images/iconTest3.png", FileType.Internal);
		config.title = "3D Test";
		
		new LwjglApplication(new MyGdxGame(), config);
	}
}
