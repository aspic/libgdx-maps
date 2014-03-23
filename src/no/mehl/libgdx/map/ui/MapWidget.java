package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.mehl.libgdx.map.info.MapManager;

public class MapWidget extends Table {

	private MapManager manager;

	public MapWidget(MapManager manager) {
		addListener(new MapListener());
		setBackground(new TextureRegionDrawable(manager.getMapTexture()));
		setTouchable(Touchable.enabled);
		this.manager = manager;
	}

	private class MapListener extends ActorGestureListener {

		public MapListener() {
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
			manager.zoomCamera(1f);
		}
	}
}
