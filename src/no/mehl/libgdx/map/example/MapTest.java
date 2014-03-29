package no.mehl.libgdx.map.example;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import no.mehl.libgdx.map.cache.MemoryCache;
import no.mehl.libgdx.map.info.CloudmadeTileFactoryInfo;
import no.mehl.libgdx.map.info.MapQuestTileFactoryInfo;
import no.mehl.libgdx.map.info.MapManager;
import no.mehl.libgdx.map.ui.MapListener;
import no.mehl.libgdx.map.ui.MapWidget;
import no.mehl.libgdx.map.util.GeoPosition;

public class MapTest implements ApplicationListener, GestureDetector.GestureListener {

    private Logger logger = new Logger(MapTest.class.getSimpleName(), Logger.INFO);

    OrthographicCamera camera;
    private MapManager mapManager;

    private Texture pin;
    private SpriteBatch batch;
    private Vector3 pinPos = new Vector3();

    private float pinWidth = 0.1f;
    private float pinHeight = 0.1f;

	private MapListener listener;
	private float delta;

    @Override
    public void create() {
        mapManager = new MapManager(new CloudmadeTileFactoryInfo(), null, new MemoryCache(), 512, 512, 0, 0);
		listener = new MapListener(mapManager);
		Gdx.input.setInputProcessor(new InputMultiplexer(new GestureDetector(this)));


        // Tile maps
        updatePin();
		pin = new Texture(Gdx.files.external("dev/assets/sprites_ui/pin.png"));

        batch = new SpriteBatch();
		camera = mapManager.getCamera();
    }

    @Override
    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.7f, 1.0f);

		mapManager.update();

		TextureRegion map = mapManager.getMapTexture();
		batch.begin();
		batch.draw(map, Gdx.graphics.getWidth() * 0.5f - map.getRegionWidth() * 0.5f, Gdx.graphics.getHeight()*0.5f - map.getRegionHeight() * 0.5f);
		batch.end();

		Matrix4 mat = batch.getProjectionMatrix().cpy();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(pin, pinPos.x - pinWidth * 0.5f, pinPos.y, pinWidth, -pinHeight);
		batch.end();
		batch.setProjectionMatrix(mat);


		delta += Gdx.graphics.getDeltaTime();
		if(delta > 1f) {
			logger.info("Current FPS: " + Gdx.graphics.getFramesPerSecond());
			delta = 0;
		}
    }

    @Override
    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // public boolean scrolled(int amount) {
	// 	mapManager.zoomCamera(0.05f * amount);
	//	return false;  //To change body of implemented methods use File | Settings | File Templates.
    // }

    /** Sets some pin that we have */
    private void updatePin() {
		Vector2 pos = mapManager.geoInPixels(new GeoPosition(59.9267740, 15.7161670));
        pinPos.set(pos.x, pos.y, 0);
    }

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		listener.touchDown(null, x, y, pointer, button);
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		listener.tap(null, x, y, count, button);
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		listener.longPress(null, x, y);
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		listener.fling(null, velocityX, velocityY, button);
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		listener.pan(null, x, y, deltaX, -deltaY);
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		listener.zoom(null, initialDistance, distance);
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		listener.pinch(null, initialPointer1, initialPointer2, pointer1, pointer2);
		return false;
	}
}
