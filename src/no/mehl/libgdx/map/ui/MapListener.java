package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import no.mehl.libgdx.map.info.MapManager;

/**
* Created with IntelliJ IDEA.
* User: aspic
* Date: 3/29/14
* Time: 10:02 PM
* To change this template use File | Settings | File Templates.
*/
public class MapListener extends ActorGestureListener {

	private MapManager manager;
	private static final int ZOOM_LIMIT = 50;
	private int zoom = ZOOM_LIMIT;

	public MapListener(MapManager manager) {
		this.manager = manager;
	}

	public void touchDown (InputEvent event, float x, float y, int pointer, int button) {
		this.zoom = ZOOM_LIMIT;
	}

	@Override
	public void tap(InputEvent event, float x, float y, int count, int button) {
		manager.click(x, y);
	}

	@Override
	public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
		manager.moveCamera(-deltaX, deltaY);
	}

	@Override
	public void fling(InputEvent event, float velocityX, float velocityY, int button) {
		// manager.zoomCamera(1f);
	}

	@Override
	public void zoom(InputEvent event, float initialDistance, float distance) {
		System.out.println("Dist: " + distance);
		// manager.zoomCamera(-1f);
	}

	@Override
	public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
	}
}
