package no.mehl.libgdx.map.example;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import no.mehl.libgdx.map.info.MapQuestTileFactoryInfo;
import no.mehl.libgdx.map.info.MapManager;
import no.mehl.libgdx.map.util.GeoPosition;

public class MapTest implements ApplicationListener, InputProcessor {

    private Logger logger = new Logger(MapTest.class.getSimpleName(), Logger.INFO);

    OrthographicCamera camera;
    private MapManager mapManager;

    private Texture pin;
    private SpriteBatch batch;
    private Vector3 pinPos = new Vector3();

    private float pinWidth = 0.1f;
    private float pinHeight = 0.1f;
	private Vector2 downPos;

    @Override
    public void create() {
        mapManager = new MapManager(new MapQuestTileFactoryInfo());

        Gdx.input.setInputProcessor(this);

        // Tile maps
        updatePin();
		pin = new Texture(Gdx.files.external("dev/assets/sprites_ui/pin.png"));

        batch = new SpriteBatch();
        batch.setColor(Color.GREEN);
		// mapManager.setCamera(camera);
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
		mapManager.render(Gdx.graphics.getDeltaTime());

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(pin, pinPos.x - pinWidth * 0.5f, pinPos.y, pinWidth, -pinHeight);
		batch.end();
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

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE) mapManager.zoomCamera(1);
        else if(keycode == Input.Keys.LEFT) mapManager.panCamera(-0.5f, 0);
        else if(keycode == Input.Keys.RIGHT) mapManager.panCamera(0.5f, 0);
        else if(keycode == Input.Keys.UP) mapManager.panCamera(0, -0.5f);
        else if(keycode == Input.Keys.DOWN) mapManager.panCamera(0, 0.5f);

		mapManager.updateTiles();
		updatePin();

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyTyped(char character) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

	private boolean down;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 vector3 = new Vector3(screenX, screenY, 0);
        camera.unproject(vector3);
        // camera.position.set(vector3.x, vector3.y, 0);

       	mapManager.zoom(2 * vector3.x, 2 * vector3.y, 1);
		updatePin();

		downPos = new Vector2(screenX, screenY);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int amount) {
		mapManager.zoomCamera(0.05f * amount);
		return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /** Sets some pin that we have */
    private void updatePin() {
		Vector2 pos = mapManager.geoInPixels(new GeoPosition(59.9267740, 15.7161670));
        pinPos.set(pos.x, pos.y, 0);
    }
}
