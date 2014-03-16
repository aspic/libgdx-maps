package no.mehl.libgdx.map.info;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import no.mehl.libgdx.map.cache.DiskCache;
import no.mehl.libgdx.map.util.TextureTile;
import no.mehl.libgdx.map.cache.ByteCache;
import no.mehl.libgdx.map.util.Dimension;
import no.mehl.libgdx.map.util.GeoPosition;
import no.mehl.libgdx.map.util.GeoUtil;

import java.lang.ref.SoftReference;

/** A factory to encapsulate how we fetch tiles from the respective back end */
public class TileFactory {

	private Logger logger = new Logger(TileFactory.class, Logger.INFO);

    private AbstractTileInfo info;
    private ObjectMap<String, TextureTile> tileCache = new ObjectMap<String, TextureTile>(); // TODO: Rely on tiled maps instead.
    private ByteCache cache;
    private TileListener listener;
    private int loadTile = 0;

    public TileFactory(AbstractTileInfo info, TileListener listener) {
        this(info, listener, new DiskCache("libgdx-maps/"));
    }

    public TileFactory(AbstractTileInfo info, TileListener listener, ByteCache cache) {
        this.info = info;
        this.listener = listener;
        this.cache = cache;
    }

    public TextureTile getTile(int tpx, int typ, int zoom) {
        return getTile(tpx, typ, zoom, false);
    }

    private TextureTile getTile(int tpx, int tpy, int zoom, boolean eagerLoad) {
        // wrap the tiles horizontally --> mod the X with the max width
        // and use that
        int tileX = tpx;// tilePoint.getX();
        int numTilesWide = (int) getMapSize(zoom).getWidth();
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

    public Dimension getMapSize(int zoom) {
        return GeoUtil.getMapSize(zoom, info);
    }

    public int getTileSize(int zoom) {
        return info.getTileSize(zoom);
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
        byte[] bytes = cache.get(tile.getURL());

        if(bytes != null) {
            loadTile(tile, bytes);
            return;
        }
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(tile.getURL());
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                if(httpResponse.getStatus().getStatusCode() != HttpStatus.SC_OK) return;

                final byte[] content = httpResponse.getResult();
                System.out.println(content.length);

                Gdx.app.postRunnable(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                loadTile(tile, content);
                            } catch (Exception e) {
								logger.error("Could not load texture", e);
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

    private void loadTile(TextureTile tile, byte[] content) {
        Pixmap pixmap = new Pixmap(content, 0, content.length);
        Texture texture = new Texture(pixmap);
        cache.put(tile.getURL(), content);
        tile.setReference(new SoftReference<Texture>(texture));
        tile.setLoaded(true);
        TileFactory.this.listener.loaded(tile);
    }
}
