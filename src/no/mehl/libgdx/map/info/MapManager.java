package no.mehl.libgdx.map.info;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import no.mehl.libgdx.map.cache.DiskCache;
import no.mehl.libgdx.map.util.*;
import no.mehl.libgdx.map.cache.ByteCache;

import java.lang.ref.SoftReference;

/**
 *  A map manager which loads tiles from the back end and adds them to
 *  a {@link com.badlogic.gdx.maps.tiled.TiledMap}.
 */
public class MapManager {


	private float width, height;
	private float mapX, mapY;

	private Logger logger = new Logger(MapManager.class.getSimpleName(), Logger.INFO);

    private AbstractTileInfo info;
    private ObjectMap<String, TextureTile> tileCache = new ObjectMap<String, TextureTile>(); // TODO: Rely on tiled maps instead.
    private ByteCache cache;
    private TileListener listener;
    private int loadTile = 0;
	private int zoom = 1;

	// Tile map
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;

	private TiledMapTileLayer fromLayer;
	private TiledMapTileLayer toLayer;
	private FrameBuffer buffer;

	private TextureRegion mapRegion;

	private Interpolator<Vector2> panInterpolation = new Interpolator<Vector2>(0.1f, Interpolation.pow2) {
		@Override
		protected void interpolate(float elapsed, Interpolation interpolation, Vector2 start, Vector2 end) {
			float x = interpolation.apply(start.x, end.x, elapsed);
			float y = interpolation.apply(start.y, end.y, elapsed);
			camera.position.set(x, y, 0);
		}
	};
	private Interpolator<Integer> layerInterpolation = new Interpolator<Integer>(0.5f, Interpolation.pow2) {
		@Override
		protected void interpolate(float elapsed, Interpolation interpolation, Integer start, Integer end) {
			toLayer.setOpacity(interpolation.apply(0, 1, elapsed));
			fromLayer.setOpacity(interpolation.apply(end, start, elapsed));
		}
	};

    public MapManager(AbstractTileInfo info) {
        this(info, null, new DiskCache("libgdx-maps/"), 512, 512, 0, 0);
    }

    public MapManager(AbstractTileInfo info, TileListener listener, ByteCache cache, float width, float height, float mapX, float mapY) {
        this.info = info;
        this.listener = listener;
        this.cache = cache;

		this.width = width;
		this.height = height;
		this.mapX = mapX;
		this.mapY = mapY;

		tiledMap = new TiledMap();
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1f);
		buffer = new FrameBuffer(Pixmap.Format.RGB888, (int)width, (int)height, false);
		camera = new OrthographicCamera(width, height);
		camera.position.set(width*0.5f, height*0.5f, 0);

		mapRegion = new TextureRegion();

		// Initialize map
		updateTiles();
    }

	/** Redraws the map onto the frame buffer */
	public void update() {
		// Render to framebuffer
		buffer.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		camera.update();
		renderer.setView(camera);
		renderer.render();
		buffer.end();

		mapRegion.setRegion(buffer.getColorBufferTexture());

		layerInterpolation.interpolate(Gdx.graphics.getDeltaTime());
		panInterpolation.interpolate(Gdx.graphics.getDeltaTime());
	}

	/** Render this map to the provided spritebatch */
	public void draw(Batch batch) {
		// Render window
		batch.draw(mapRegion, mapX, mapY, width, height);
	}

	/**
	 * Load all tiles on this zoom level within the camera bounds.
	 * Could potentially be an expensive operation.
	 */
	public void updateTiles() {

		float h2 = (camera.viewportHeight * 0.75f)/info.getTileSize();
		float w2 = (camera.viewportWidth * 0.75f)/info.getTileSize();

		float x = camera.position.x/info.getTileSize();
		float y = camera.position.y/info.getTileSize();

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

			@Override
			public void cancelled() {
				logger.error("Request was cancelled");
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

		// logger.info(String.format("Adds tile: x=%d, y=%d", x, y));
		layer.setCell(x, y, cell);
	}

	/** Retrieve or create a new layer with the given index */
	private TiledMapTileLayer getLayer(int index) {
		TiledMapTileLayer layer = null;

		try {
			layer = (TiledMapTileLayer) tiledMap.getLayers().get(index);
		} catch(IndexOutOfBoundsException e) {
			logger.info(String.format("Layer did not exist for zoom %d", index));
		}

		if(layer == null) {
			layer = new TiledMapTileLayer((int) Math.pow(2, index), (int) Math.pow(2, index), info.getTileSize(), info.getTileSize());
			tiledMap.getLayers().add(layer);
		}
		return layer;
	}

	/** Zoom to some level in the tiled map */
	public void zoom(float centerX, float centerY, int dZoom) {
		transition(dZoom);
		// camera.position.set(centerX, centerY, 0);
	}

	private void transition(int dZoom) {
		if(dZoom == 1) {
			fromLayer = getLayer(zoom);
			zoom += dZoom;
			toLayer = getLayer(zoom);
			camera.position.set(camera.position.x * 2, camera.position.y * 2, 0);
		} else if(dZoom == -1) {
			fromLayer = getLayer(zoom);
			zoom -= 1;
			toLayer = getLayer(zoom);
			camera.position.set(camera.position.x * 0.5f, camera.position.y * 0.5f, 0);
		}
		layerInterpolation.start(0, 1);
		updateTiles();
	}

	public void zoomCamera(float diff) {
		float zoom = camera.zoom + diff;
		if(zoom < 0) {
			logger.error("Can't have negative zoom.");
			return;
		}

		// Load outer tiles
		if(zoom > 1.5f) {
			camera.zoom = 1f;
			transition(-1);
		} else if (zoom < 0.5f) {
			camera.zoom = 1f;
			transition(1);
		} else {
			camera.zoom += diff;
		}
		if(diff > 0) {
			// updateTiles();
		}
	}

	public void panCamera(float dX, float dY) {
		panInterpolation.start(new Vector2(camera.position.x, camera.position.y), new Vector2(camera.position.x + dX, camera.position.y + dY));
	}

	public void moveCamera(float dX, float dY) {
		camera.position.add(dX, dY, 0);
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

	// Only register clicks within map bounds
	public void click(float screenX, float screenY) {
		System.out.println("Clicked: " + screenX + " and " + screenY);
		if(screenX >= mapX && screenX <= mapX + width && screenY >= mapY && screenY <= mapY + height) {
			float x = screenX - mapX;
			float y = screenY - mapY;

			Vector3 vector3 = new Vector3(x, y, 0);
			camera.unproject(vector3);
			System.out.println(vector3);
			zoom(2 * vector3.x, 2 * vector3.y, 1);
		}
	}

	public TextureRegion getMapTexture() {
		return mapRegion;
	}
}
