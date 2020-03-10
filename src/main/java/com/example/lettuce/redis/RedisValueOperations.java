package com.example.lettuce.redis;

public interface RedisValueOperations<K, V> {
    V get(K key);

    String set(K key, V value);

    Long del(K key);

    Long incr(K key);
}
