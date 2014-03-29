package no.mehl.libgdx.map.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import no.mehl.libgdx.map.info.MapManager;

public class MapWidget extends Table {

	private MapManager manager;

	public MapWidget(MapManager manager) {
		addListener(new MapListener(manager));
		setBackground(new TextureRegionDrawable(manager.getMapTexture()));
		setTouchable(Touchable.enabled);
		this.manager = manager;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		manager.update();
		batch.begin();
	}
}
