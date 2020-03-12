package com.example.lettuce.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class LettuceConnection<K, V> {
    @Getter
    private final RedisClient redisClient;
    @Getter
    private final RedisCodec<K, V> redisCodec;

    public LettuceConnection(RedisClient redisClient, RedisCodec<K, V> redisCodec) {
        this.redisClient = redisClient;
        this.redisCodec = redisCodec;
    }

    private StatefulRedisConnection<K, V> connect() {
        return redisClient.connect(redisCodec);
    }

    public StatefulRedisConnection<K, V> getConnection() {
        return RedisConnectionHolder.getConnection(this);
    }

    private static class RedisConnectionHolder {
        private static Map<LettuceConnection<?, ?>, StatefulRedisConnection> holder = new HashMap<>();

        private static synchronized <K, V> StatefulRedisConnection<K, V> getConnection(LettuceConnection<K, V> lettuceConnection) {
            Assert.notNull(lettuceConnection, "No RedisTemplate specified");
            if (!holder.containsKey(lettuceConnection)) {
                holder.put(lettuceConnection, lettuceConnection.connect());
            }

            return holder.get(lettuceConnection);
        }
    }
}
