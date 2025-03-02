package com.taxibooking.bookingservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String hashKey, Object value) {
        log.info("Saving value to Redis with key: {} and hashKey: {}", key, hashKey);
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object get(String key, String hashKey) {
        log.info("Getting value from Redis with key: {} and hashKey: {}", key, hashKey);
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void delete(String key) {
        log.info("Deleting value from Redis with key: {}", key);
        redisTemplate.delete(key);
    }

    public void delete(String key, String hashKey) {
        log.info("Deleting value from Redis with key: {} and hashKey: {}", key, hashKey);
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public Object leftPop(String key) {
        log.info("Left popping value from Redis list with key: {}", key);
        return redisTemplate.opsForList().leftPop(key);
    }
}