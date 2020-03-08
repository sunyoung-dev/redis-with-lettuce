package com.example.lettuce.config;

import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379/0");
    }
}
