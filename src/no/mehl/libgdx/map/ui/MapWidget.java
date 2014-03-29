package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
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
			// manager.zoomCamera(1f);
		}

		@Override
		public void zoom(InputEvent event, float initialDistance, float distance) {
			manager.zoomCamera(1f);
		}

		@Override
		public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			manager.zoomCamera(-1f);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		manager.update();
		batch.begin();
	}
}
