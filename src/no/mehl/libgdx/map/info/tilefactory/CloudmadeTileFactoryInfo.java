package no.mehl.libgdx.map.info.tilefactory;

import no.mehl.libgdx.map.info.AbstractTileInfo;

/**
 * Wires map to the Cloudmade back end
 * http://cloudmade.com/documentation/map-tiles
 *
 * This backend will need a key to work.
 */
public class CloudmadeTileFactoryInfo extends AbstractTileInfo {


    private static final int max = 18;

    public CloudmadeTileFactoryInfo() {
        super("Cloudmade",
                1, max, max,
                256, true, true, 					// tile size is 256 and x/y orientation is normal
                "http://b.tile.cloudmade.com/8ee2a50541944fb9bcedded5165f09d9/3/256/",
                "x", "y", "z");						// 5/15/10.png
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {
        // zoom = max - zoom;
        String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
        return url;
    }

}
