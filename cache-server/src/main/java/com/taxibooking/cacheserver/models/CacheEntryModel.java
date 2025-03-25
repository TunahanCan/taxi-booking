package com.taxibooking.cacheserver.models;

public class CacheEntryModel {

    private String value;
    private final long expireAt;
    private volatile long lastAccessTime;
    private volatile long casUnique;

    public CacheEntryModel(String value, long ttlSeconds, long casUnique)
    {
        this.value = value;
        this.lastAccessTime = System.currentTimeMillis();
        this.expireAt = (ttlSeconds > 0) ? this.lastAccessTime + ttlSeconds * 1000 : 0;
        this.casUnique = casUnique;
    }

    public synchronized String getValue()
    {
        lastAccessTime = System.currentTimeMillis();
        return value;
    }

    public synchronized void setValue(String newValue) {
        value = newValue;
        lastAccessTime = System.currentTimeMillis();
        casUnique = System.nanoTime();
    }

    public boolean isExpired() {
        return expireAt > 0 && System.currentTimeMillis() > expireAt;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public long getCasUnique() {
        return casUnique;
    }

}
