package no.mehl.libgdx.map.cache;

import com.badlogic.gdx.Gdx;

/** Store/load textures from some storage */
public class DiskCache implements ByteCache {

    private String path;

	/** Storage path relative to user directory */
    public DiskCache(String relativePath) {
        this.path = relativePath;
    }

    @Override
    public byte[] get(String url) {
        if(Gdx.files.external(path + url).exists()) {
            return Gdx.files.external(path + url).readBytes();
        }
        return null;
    }

    @Override
    public void put(String url, byte[] bytes) {
        Gdx.files.external(path+url).writeBytes(bytes, false);
    }
}
