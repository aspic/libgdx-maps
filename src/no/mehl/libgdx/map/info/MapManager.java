package no.mehl.libgdx.map.info;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import no.mehl.libgdx.map.cache.DiskCache;
import no.mehl.libgdx.map.util.*;
import no.mehl.libgdx.map.cache.ByteCache;

import java.lang.ref.SoftReference;
import java.util.Stack;

/**
 *  A map manager which loads tiles from the back end and adds them to
 *  a {@link com.badlogic.gdx.maps.tiled.TiledMap}.
 */
public class MapManager {

	private Logger logger = new Logger(MapManager.class, Logger.INFO);

    private AbstractTileInfo info;
    private ObjectMap<String, TextureTile> tileCache = new ObjectMap<String, TextureTile>(); // TODO: Rely on tiled maps instead.
    private ByteCache cache;
    private TileListener listener;
    private int loadTile = 0;
	private int zoom = 1;
	private Stack<Vector3> zoomLevels = new Stack<Vector3>();

	// Tile map
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;

    public MapManager(AbstractTileInfo info) {
        this(info, null, new DiskCache("libgdx-maps/"));
    }

    public MapManager(AbstractTileInfo info, TileListener listener, ByteCache cache) {
        this.info = info;
        this.listener = listener;
        this.cache = cache;

		tiledMap = new TiledMap();
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1f/info.getTileSize());

		camera = new OrthographicCamera();
		camera.setToOrtho(true, (Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight()) * 2, 2);
		camera.position.set(1, 1, 0);

		// Initialize map
		updateTiles();
    }

	public void render() {
		camera.update();
		renderer.setView(camera);
		renderer.render();
	}

	/**
	 * Load all tiles on this zoom level within the camera bounds.
	 * Could potentially be an expensive operation.
	 */
	public void updateTiles() {

		float h2 = camera.viewportHeight * 0.5f;
		float w2 = camera.viewportWidth * 0.5f;

		float x = camera.position.x;
		float y = camera.position.y;

		for(int i = (int)Math.floor(x - w2); i <= Math.floor(x + w2); i++) {
			for (int j = (int)Math.floor(y - h2); j <= Math.floor(y + h2); j++) {
				if(i < 0 || j < 0) continue;
				getTile(i, j, zoom);
			}
		}
	}

	/** Query cache or back end for a tile. A tile is returned, but potentially not loaded. */
    public TextureTile getTile(int tpx, int typ, int zoom) {
        return getTile(tpx, typ, zoom, false);
    }

    private TextureTile getTile(int tpx, int tpy, int zoom, boolean eagerLoad) {
        // wrap the tiles horizontally --> mod the X with the max width
        // and use that
        int tileX = tpx;// tilePoint.getX();
        int numTilesWide = getMapDimension(zoom).getWidth();
        if (tileX < 0)
        {
            tileX = numTilesWide - (Math.abs(tileX) % numTilesWide);
        }

        tileX = tileX % numTilesWide;
        int tileY = tpy;
        // TilePoint tilePoint = new TilePoint(tileX, tpy);
        String url = info.getTileUrl(tileX, tileY, zoom);// tilePoint);

        TextureTile.Priority pri = TextureTile.Priority.High;
        if (!eagerLoad)
        {
            pri = TextureTile.Priority.Low;
        }
        TextureTile tile = null;
        // System.out.println("testing for validity: " + tilePoint + " zoom = " + zoom);
        if (!tileCache.containsKey(url))
        {
            if (!GeoUtil.isValidTile(tileX, tileY, zoom, info)) {
                tile = new TextureTile(tileX, tileY, zoom);
            }
            else
            {
                tile = new TextureTile(tileX, tileY, zoom, url, pri, this);
                startLoading(tile);
            }
            tileCache.put(url, tile);
        }
        else {
            tile = tileCache.get(url);
        }

		/*
		 * if (eagerLoad && doEagerLoading) { for (int i = 0; i<1; i++) { for (int j = 0; j<1; j++) { // preload the 4
		 * tiles under the current one if(zoom > 0) { eagerlyLoad(tilePoint.getX()*2, tilePoint.getY()*2, zoom-1);
		 * eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2, zoom-1); eagerlyLoad(tilePoint.getX()*2,
		 * tilePoint.getY()*2+1, zoom-1); eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2+1, zoom-1); } } } }
		 */

        return tile;
    }

    public Dimension getMapDimension(int zoom) {
        return GeoUtil.getMapSize(zoom, info);
    }

    public int getTileSize() {
        return info.getTileSize();
    }

    /**
     * Convert a GeoPosition to a pixel position in the world bitmap a the specified zoom level.
     * @param c a GeoPosition
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     * @return the pixel point
     */
    public Vector2 geoToPixel(GeoPosition c, int zoomLevel) {
        return GeoUtil.getBitmapCoordinate(c, zoomLevel, info);
    }

    private void startLoading(final TextureTile tile) {

		// Early exit if cache hit
		byte[] bytes = cache.get(tile.getURL());
        if(bytes != null && loadTile(tile, bytes)) {
			addToTiledMap(tile);
            return;
        }

		// Request tile from back end
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(tile.getURL());
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                if(httpResponse.getStatus().getStatusCode() != HttpStatus.SC_OK) return;
                final byte[] content = httpResponse.getResult();

                Gdx.app.postRunnable(
                    new Runnable() {
                        @Override
                        public void run() {
							if(loadTile(tile, content)) {
								addToTiledMap(tile);
							}
                        }
                    }
                );
            }

            @Override
            public void failed(Throwable t) {
				logger.error("Could not load from server", t);
            }
        };
        Gdx.net.sendHttpRequest(httpRequest, listener);
    }

	/** Ensures that the texture we got is sane */
	private boolean loadTile(TextureTile tile, byte[] content) {
		try {
			Pixmap pixmap = new Pixmap(content, 0, content.length);
			Texture texture = new Texture(pixmap);
			cache.put(tile.getURL(), content);
			tile.setReference(new SoftReference<Texture>(texture));
			tile.setLoaded(true);
		} catch(Exception e) {
			logger.error("Could not generate texture from image data", e);
			return false;
		}
		return true;
	}

	/** Appends this tile to our map, will be rendered for the appropriate layer */
	private void addToTiledMap(TextureTile tile) {
		TiledMapTileLayer layer = getLayer(zoom);
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(tile.getImage())));
		cell.setFlipVertically(true);
		int x = tile.getX();
		int y = tile.getY();

		logger.info("Adds tile: x=%d, y=%d", x, y);
		layer.setCell(x, y, cell);
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
		renderer.setView(camera);
	}

	/** Retrieve or create a new layer with the given index */
	private TiledMapTileLayer getLayer(int index) {
		TiledMapTileLayer layer = null;

		try {
			layer = (TiledMapTileLayer) tiledMap.getLayers().get(index);
		} catch(IndexOutOfBoundsException e) {
			logger.info("Layer did not exist for zoom %d", index);
		}

		if(layer == null) {
			layer = new TiledMapTileLayer((int) Math.pow(2, index), (int) Math.pow(2, index), info.getTileSize(), info.getTileSize());
			tiledMap.getLayers().add(layer);
		}
		return layer;
	}

	/** Zoom to some level in the tiled map */
	public void zoom(float centerX, float centerY, int dZoom) {
		if(dZoom == 1) {
			zoomLevels.add(new Vector3(camera.position.x, camera.position.y, zoom));
			getLayer(zoom).setVisible(false);
			zoom += dZoom;
			getLayer(zoom).setVisible(true);
			camera.position.set(centerX, centerY, 0);
		} else if(dZoom == -1) {
			Vector3 pos = zoomLevels.pop();
			getLayer(zoom).setVisible(false);
			zoom = (int)pos.z;
			getLayer(zoom).setVisible(true);
			camera.position.set(pos.x, pos.y, 0);
		}
		updateTiles();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * Returns the {@link no.mehl.libgdx.map.util.GeoPosition} as a point in pixels relative to the zoom
	 */
	public Vector2 geoInPixels(GeoPosition pos) {
		Dimension dim = GeoUtil.getMapSize(zoom, info);
		int mapW = dim.getWidth() * getTileSize();
		int mapH = dim.getHeight() * getTileSize();
		Vector2 point = geoToPixel(pos, zoom);
		return new Vector2((float) ((point.x / mapW) * Math.pow(2, zoom)), (float) ((point.y / mapH) * Math.pow(2, zoom)));
	}
}
