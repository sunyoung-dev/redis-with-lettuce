package com.example.lettuce.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Bean
    public StatefulRedisConnection<String, String> redisClient() {
        return RedisClient.create("redis://localhost:6379/0").connect();
    }
}
