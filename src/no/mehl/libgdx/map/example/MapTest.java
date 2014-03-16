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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import no.mehl.libgdx.map.util.TextureTile;
import no.mehl.libgdx.map.info.MapQuestTileFactoryInfo;
import no.mehl.libgdx.map.info.TileFactory;
import no.mehl.libgdx.map.info.TileListener;
import no.mehl.libgdx.map.util.*;
import no.mehl.libgdx.map.util.Dimension;

import java.util.Stack;

public class MapTest implements ApplicationListener, InputProcessor, TileListener {

    private Logger logger = new Logger(MapTest.class, Logger.INFO);

    OrthographicCamera camera;
    private Texture texture;
    private TileFactory tileFactory;
    private int zoom = 1;
    private int lastZoom = zoom;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private int tilesH = 2, tilesW = 2;

    private Stack<Vector3> zoomLevels = new Stack<Vector3>();
    private Vector2 lastPos = new Vector2();
    private Texture pin;
    private SpriteBatch batch;
    private Vector3 pinPos = new Vector3();

    private float pinWidth = 0.1f;
    private float pinHeight = 0.1f;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(true, (w / h) * 4, 4);
        camera.update();


        tileFactory = new TileFactory(new MapQuestTileFactoryInfo(), this);

        Gdx.input.setInputProcessor(this);

        // Tile maps
        tiledMap = new TiledMap();

        loadMaps(zoom);
        updatePin();


        camera.position.set(1, 1, 0);
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1/256f);
        renderer.setView(camera);


        pin = new Texture(Gdx.files.external("dev/assets/sprites_ui/pin.png"));
        batch = new SpriteBatch();
        batch.setColor(Color.GREEN);
    }

    @Override
    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(pin, pinPos.x - pinWidth * 0.5f, pinPos.y, pinWidth, -pinHeight);
        batch.end();
    }

    @Override
    public void loaded(TextureTile tile) {
        TiledMapTileLayer layer = getLayer(zoom);

        texture = tile.getImage();
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(new TextureRegion(texture)));
        cell.setFlipVertically(true);
        int x = tile.getX();
        int y = tile.getY();

        logger.info("x:%d, y:%d", x, y);

        layer.setCell(x, y, cell);
    }

    private void loadMaps(int zoom) {

        float h2 = camera.viewportHeight * 0.5f;
        float w2 = camera.viewportWidth * 0.5f;

        float x = camera.position.x;
        float y = camera.position.y;

        for(int i = (int)Math.floor(x - w2); i <= Math.floor(x + w2); i++) {
            for (int j = (int)Math.floor(y - h2); j <= Math.floor(y + h2); j++) {
                if(i < 0 || j < 0) continue;
                tileFactory.getTile(i, j, zoom);
            }
        }

        lastPos.set(camera.position.x, camera.position.y);
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
            zoom(-1);
            return false;
        } else if(keycode == Input.Keys.LEFT) {
            camera.position.x -= 1;
        } else if(keycode == Input.Keys.RIGHT) camera.position.x += 1;
        else if(keycode == Input.Keys.UP) camera.position.y -= 1;
        else if(keycode == Input.Keys.DOWN) camera.position.y += 1;

        loadMaps(zoom);

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
        int x = MathUtils.floor(vector3.x);
        int y = MathUtils.floor(vector3.y);
        zoom(+1);
        camera.position.set(2*vector3.x, 2*vector3.y, 0);
        loadMaps(zoom);

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

    private TiledMapTileLayer getLayer(int index) {
        TiledMapTileLayer layer = null;

        try {
            layer = (TiledMapTileLayer) tiledMap.getLayers().get(zoom);
        } catch(IndexOutOfBoundsException e) {
            logger.info("Layer did not exist for zoom %d", zoom);
        }

        if(layer == null) {
            layer = new TiledMapTileLayer((int) Math.pow(2, zoom), (int) Math.pow(2, zoom), 256, 256);
            tiledMap.getLayers().add(layer);
        }
        return layer;
    }

    private void zoom(int dZoom) {
        if(dZoom == 1) {
            zoomLevels.add(new Vector3(camera.position.x, camera.position.y, zoom));
            getLayer(zoom).setVisible(false);
            zoom += dZoom;
            getLayer(zoom).setVisible(true);
        } else if(dZoom == -1) {
            Vector3 pos = zoomLevels.pop();

            getLayer(zoom).setVisible(false);
            zoom = (int)pos.z;
            getLayer(zoom).setVisible(true);
            camera.position.set(pos.x, pos.y, 0);
        }
        updatePin();
    }

    /** Sets some pin that we have */
    private void updatePin() {
        Dimension dim = tileFactory.getMapSize(zoom);
        int mapWidth = dim.getWidth() * tileFactory.getTileSize(zoom);
        int mapHeight = dim.getHeight() * tileFactory.getTileSize(zoom);
        Vector2 point = tileFactory.geoToPixel(new GeoPosition(59.9267740, 15.7161670), zoom);

        pinPos.set((float) ((point.x / mapWidth)*Math.pow(2, zoom)), (float) ((point.y / mapHeight)*Math.pow(2,
                zoom)), 0);
    }
}
