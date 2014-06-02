package no.mehl.libgdx.map.util;

import com.badlogic.gdx.math.Vector2;
import no.mehl.libgdx.map.info.AbstractTileInfo;

/** Various geo utilities */
public class GeoUtil {

    /**
     * @param x the x value
     * @param y the y value
     * @param zoomLevel the zoom level
     * @param info the tile factory info
     * @return true if this point in <em>tiles</em> is valid at this zoom level. For example, if the zoom level is 0
     * (zoomed all the way out, where there is only one tile), then x,y must be 0,0
     * TODO: Carry out width/height check
     */
    public static boolean isValidTile(int x, int y, int zoomLevel, AbstractTileInfo info) {
        // int x = (int)coord.getX();
        // int y = (int)coord.getY();
        // if off the map to the top or left

        if (x < 0 || y < 0) {
            return false;
        }
        // if of the map to the right
        /**
        if (info.getMapCenterInPixelsAtZoom(zoomLevel).x * 2 <= x * info.getTileSize()) {
            return false;
        }
        // if off the map to the bottom
        if (info.getMapCenterInPixelsAtZoom(zoomLevel).y * 2 <= y * info.getTileSize()) {
            return false;
        }
        **/
        // if out of zoom bounds
        if (zoomLevel < info.getMinimumZoomLevel() || zoomLevel > info.getMaximumZoomLevel()) {
            return false;
        }
        return true;
    }

    public static Dimension getMapSize(int zoom, AbstractTileInfo info) {
        return new Dimension(info.getMapWidthInTilesAtZoom(zoom), info.getMapWidthInTilesAtZoom(zoom));
    }

    /**
     * Given a position (latitude/longitude pair) and a zoom level, return the appropriate point in <em>pixels</em>. The
     * zoom level is necessary because pixel coordinates are in terms of the zoom level
     * @param c A lat/lon pair
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     * @param info the tile factory info
     * @return the coordinate
     */
    public static Vector2 getBitmapCoordinate(GeoPosition c, int zoomLevel, AbstractTileInfo info) {
        return getBitmapCoordinate(c.getLatitude(), c.getLongitude(), zoomLevel, info);
    }

    /**
     * Given a position (latitude/longitude pair) and a zoom level, return the appropriate point in <em>pixels</em>. The
     * zoom level is necessary because pixel coordinates are in terms of the zoom level
     * @param latitude the latitude
     * @param longitude the longitude
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     * @param info the tile factory info
     * @return the coordinate
     */
    public static Vector2 getBitmapCoordinate(double latitude, double longitude, int zoomLevel, AbstractTileInfo info)
    {
        float x = (float) (info.getMapCenterInPixelsAtZoom(zoomLevel).x + longitude
                        * info.getLongitudeDegreeWidthInPixels(zoomLevel));
        float e = (float) Math.sin(latitude * (Math.PI / 180.0));
        if (e > 0.9999f)
        {
            e = 0.9999f;
        }
        if (e < -0.9999f)
        {
            e = -0.9999f;
        }
        float y = (float) (info.getMapCenterInPixelsAtZoom(zoomLevel).y + 0.5 * Math.log((1 + e) / (1 - e)) * -1
                        * (info.getLongitudeRadianWidthInPixels(zoomLevel)));
        return new Vector2(x, y);
    }
}
