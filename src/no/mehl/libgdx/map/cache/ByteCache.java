package no.mehl.libgdx.map.cache;

public interface ByteCache {

    public byte[] get(String url);

    public void put(String url, byte[] bytes);

}
