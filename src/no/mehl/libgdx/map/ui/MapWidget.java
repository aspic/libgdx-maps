package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.mehl.libgdx.map.info.MapManager;

public class MapWidget extends Table {

	private MapManager manager;
	private MapWidgetStyle style;

	public MapWidget(MapManager manager) {
		this(manager, null);
	}

	public MapWidget(final MapManager manager, MapWidgetStyle style) {
		setBackground(new TextureRegionDrawable(manager.getMapTexture()));
		setTouchable(Touchable.enabled);
		this.manager = manager;

		if(style != null) {
			final Image zoomIn = new Image(style.zoomIn);
			final Image zoomOut = new Image(style.zoomOut);

			ClickListener listener = new ClickListener() {
				public void clicked (InputEvent event, float x, float y) {
					if(event.getTarget().equals(zoomIn)) {
						manager.zoomCamera(1f);
					} else if(event.getTarget().equals(zoomOut)) {
						manager.zoomCamera(-1f);
					}
					event.cancel();
					System.out.println("zoom listener");
				}
			};

			zoomIn.addListener(listener);
			zoomOut.addListener(listener);

			add(zoomIn).right().bottom().expandX().row();
			add(zoomOut).right().bottom().expandX();
		}
		addListener(new MapListener(manager));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		manager.update();
		batch.begin();
	}

	public static class MapWidgetStyle {

		public Drawable zoomIn;
		public Drawable zoomOut;

		public MapWidgetStyle(Drawable zoomIn, Drawable zoomOut) {
			this.zoomIn = zoomIn;
			this.zoomOut = zoomOut;
		}

	}
}
