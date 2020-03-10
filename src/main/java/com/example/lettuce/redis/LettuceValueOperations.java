package com.example.lettuce.redis;

public class LettuceValueOperations<K, V> implements RedisValueOperations<K, V>{
    private LettuceConnection<K, V> lettuceConnection;

    public LettuceValueOperations(LettuceConnection<K, V> lettuceConnection) {
        this.lettuceConnection = lettuceConnection;
    }

    public V get(K key) {
        return lettuceConnection.getConnection().sync().get(key);
    }

    public String set(K key, V value) {
        return lettuceConnection.getConnection().sync().set(key, value);
    }

    public Long del(K key) {
        return lettuceConnection.getConnection().sync().del(key);
    }

    public Long incr(K key) {
        return lettuceConnection.getConnection().sync().incr(key);
    }
}
