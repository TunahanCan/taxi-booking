package com.taxibooking.cacheserver.service;

import com.taxibooking.cacheserver.models.CacheEntryModel;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheService {

    private final ConcurrentHashMap<String, CacheEntryModel> cache = new ConcurrentHashMap<>();
    private final int maxSize = 1000; // Maksimum kayıt sayısı
    private final AtomicLong casCounter = new AtomicLong(1);
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final long startTime = System.currentTimeMillis();

    public synchronized String set(String key, String value, long ttlSeconds) {
        CacheEntryModel entry = new CacheEntryModel(value, ttlSeconds, casCounter.getAndIncrement());
        cache.put(key, entry);
        evictIfNeeded();
        return "STORED";
    }

    public synchronized String add(String key, String value, long ttlSeconds) {
        if (cache.containsKey(key) && !cache.get(key).isExpired()) {
            return "NOT_STORED";
        }
        CacheEntryModel entry = new CacheEntryModel(value, ttlSeconds, casCounter.getAndIncrement());
        cache.put(key, entry);
        evictIfNeeded();
        return "STORED";
    }

    public synchronized String replace(String key, String value, long ttlSeconds) {
        if (!cache.containsKey(key) || cache.get(key).isExpired()) {
            return "NOT_STORED";
        }
        CacheEntryModel entry = new CacheEntryModel(value, ttlSeconds, casCounter.getAndIncrement());
        cache.put(key, entry);
        return "STORED";
    }

    public synchronized String append(String key, String value) {
        CacheEntryModel entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return "NOT_STORED";
        }
        entry.setValue(entry.getValue() + value);
        return "STORED";
    }

    public synchronized String prepend(String key, String value) {
        CacheEntryModel entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return "NOT_STORED";
        }
        entry.setValue(value + entry.getValue());
        return "STORED";
    }

    public String get(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            CacheEntryModel entry = cache.get(key);
            if (entry != null && !entry.isExpired()) {
                hits.incrementAndGet();
                sb.append(key).append(" => ").append(entry.getValue()).append("\n");
            } else {
                misses.incrementAndGet();
            }
        }
        if (sb.isEmpty()) {
            return "NULL";
        }
        return sb.append("END").toString();
    }

    public synchronized String incr(String key, long delta) {
        CacheEntryModel entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return "NOT_FOUND";
        }
        try {
            long current = Long.parseLong(entry.getValue().trim());
            long updated = current + delta;
            entry.setValue(Long.toString(updated));
            return Long.toString(updated);
        } catch (NumberFormatException e) {
            return "CLIENT_ERROR cannot increment non-numeric value";
        }
    }

    public synchronized String decr(String key, long delta) {
        CacheEntryModel entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return "NOT_FOUND";
        }
        try {
            long current = Long.parseLong(entry.getValue().trim());
            long updated = Math.max(0, current - delta);
            entry.setValue(Long.toString(updated));
            return Long.toString(updated);
        } catch (NumberFormatException e) {
            return "CLIENT_ERROR cannot decrement non-numeric value";
        }
    }

    public synchronized String delete(String key) {
        if (cache.remove(key) != null) {
            return "DELETED";
        }
        return "NOT_FOUND";
    }

    public synchronized String flushAll() {
        cache.clear();
        return "OK";
    }

    public String stats() {
        long uptime = (System.currentTimeMillis() - startTime) / 1000;
        return "STAT items " + cache.size() + "\n" +
                "STAT hits " + hits.get() + "\n" +
                "STAT misses " + misses.get() + "\n" +
                "STAT uptime " + uptime + "\nEND";
    }

    public String version() {
        return "VERSION 1.0 (Java 21 + Netty + Spring Core)";
    }

    private void evictIfNeeded() {
        if (cache.size() <= maxSize) return;
        String lruKey = null;
        long oldestAccess = Long.MAX_VALUE;
        for (var entry : cache.entrySet()) {
            CacheEntryModel ce = entry.getValue();
            if (ce.getLastAccessTime() < oldestAccess) {
                oldestAccess = ce.getLastAccessTime();
                lruKey = entry.getKey();
            }
        }
        if (lruKey != null) {
            cache.remove(lruKey);
        }
    }

    public void removeExpiredEntries() {
        for (String key : cache.keySet()) {
            CacheEntryModel entry = cache.get(key);
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
            }
        }
    }

}
