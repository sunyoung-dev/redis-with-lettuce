package com.example.lettuce.config;

import com.example.lettuce.redis.LettuceConnection;
import com.example.lettuce.redis.LettuceTemplate;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379/0");
    }

    @Bean
    public LettuceTemplate<String, String> lettuceTemplate(RedisClient redisClient) {
        LettuceTemplate<String, String> lettuceTemplate = new LettuceTemplate<>();

        LettuceConnection<String, String> lettuceConnection = new LettuceConnection<>(redisClient, StringCodec.UTF8);
        lettuceTemplate.setLettuceConnection(lettuceConnection);

        return lettuceTemplate;
    }
}
