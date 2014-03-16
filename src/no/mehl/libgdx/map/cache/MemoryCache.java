package no.mehl.libgdx.map.cache;

import com.badlogic.gdx.utils.ObjectMap;

/** Plain in-memory cache for textures */
public class MemoryCache implements ByteCache {

    private ObjectMap<String, byte[]> cache = new ObjectMap<String, byte[]>();

    @Override
    public byte[] get(String url) {
        return cache.get(url);
    }

    @Override
    public void put(String url, byte[] texture) {
        cache.put(url, texture);
    }
}
