package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import no.mehl.libgdx.map.info.MapManager;

/**
 * Listener which is supposed to be used both for Scene2D events, and regular touch events.
 */
public class MapListener extends ActorGestureListener {

	private MapManager manager;
	private static final int ZOOM_LIMIT = 50;
	private int zoom = ZOOM_LIMIT;

	public MapListener(MapManager manager) {
		this.manager = manager;
	}

	public void touchDown (InputEvent event, float x, float y, int pointer, int button) {
	}

	@Override
	public void tap(InputEvent event, float x, float y, int count, int button) {
		int zoom = button == Input.Buttons.LEFT ? 1 : -1;
		if(event != null && !event.isCancelled()) manager.zoom(zoom);
		else if (event == null) manager.zoom(zoom);
	}

	@Override
	public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
		manager.moveCamera(-deltaX, deltaY);
	}

	@Override
	public void fling(InputEvent event, float velocityX, float velocityY, int button) {
	}

	@Override
	public void zoom(InputEvent event, float initialDistance, float distance) {
		manager.zoomCamera(-1f);
	}

	@Override
	public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
	}
}
