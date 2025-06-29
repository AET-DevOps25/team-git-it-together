package com.gitittogether.skillforge.server.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        log.info("Creating ReactiveRedisTemplate for rate limiting");
        
        StringRedisSerializer serializer = new StringRedisSerializer();
        
        RedisSerializationContext<String, String> serializationContext = 
            RedisSerializationContext.<String, String>newSerializationContext()
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
} 