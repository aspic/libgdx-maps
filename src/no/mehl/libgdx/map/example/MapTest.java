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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import no.mehl.libgdx.map.info.MapQuestTileFactoryInfo;
import no.mehl.libgdx.map.info.MapManager;

public class MapTest implements ApplicationListener, InputProcessor {

    private Logger logger = new Logger(MapTest.class, Logger.INFO);

    OrthographicCamera camera;
    private MapManager mapManager;

    private Texture pin;
    private SpriteBatch batch;
    private Vector3 pinPos = new Vector3();

    private float pinWidth = 0.1f;
    private float pinHeight = 0.1f;

    @Override
    public void create() {
        mapManager = new MapManager(new MapQuestTileFactoryInfo());

        Gdx.input.setInputProcessor(this);

        // Tile maps
        // updatePin();

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
		mapManager.render();
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
        if(keycode == Input.Keys.ESCAPE) {
            mapManager.zoom(0, 0, -1);
            return false;
        } else if(keycode == Input.Keys.LEFT) {
            camera.position.x -= 1;
        } else if(keycode == Input.Keys.RIGHT) camera.position.x += 1;
        else if(keycode == Input.Keys.UP) camera.position.y -= 1;
        else if(keycode == Input.Keys.DOWN) camera.position.y += 1;

		mapManager.updateTiles();

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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 vector3 = new Vector3(screenX, screenY, 0);
        camera.unproject(vector3);
        // camera.position.set(vector3.x, vector3.y, 0);

        logger.info("clicked: %s", vector3.toString());
        mapManager.zoom(2 * vector3.x, 2 * vector3.y, 1);
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /** Sets some pin that we have
    private void updatePin() {
        Dimension dim = mapManager.getMapDimension(zoom);
        int mapWidth = dim.getWidth() * mapManager.getTileSize();
        int mapHeight = dim.getHeight() * mapManager.getTileSize();
        Vector2 point = mapManager.geoToPixel(new GeoPosition(59.9267740, 15.7161670), zoom);

        pinPos.set((float) ((point.x / mapWidth) * Math.pow(2, zoom)), (float) ((point.y / mapHeight) * Math.pow(2,
				zoom)), 0);
    }
	 */
}
