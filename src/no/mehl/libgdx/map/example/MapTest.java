package no.mehl.libgdx.map.example;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MapTest  {

	public static void main(String[] args) {

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "uniquegameidentifaction";
		cfg.width = 568;
		cfg.height = 1080;


		new LwjglApplication(new MapView(), cfg);
	}
}
