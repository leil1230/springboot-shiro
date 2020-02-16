package com.leil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {


    // 用于管理shiro session的redisTemplate
    @Bean
    public RedisTemplate<String, byte[]> shiroRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> shiroRedisTemplate = new RedisTemplate<>();
        shiroRedisTemplate.setKeySerializer(RedisSerializer.string());
        shiroRedisTemplate.setHashKeySerializer(RedisSerializer.string());
        shiroRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return shiroRedisTemplate;
    }


    @Bean
    public RedisTemplate<String, byte[]> cacheRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> cacheRedisTemplate = new RedisTemplate<>();
        cacheRedisTemplate.setKeySerializer(RedisSerializer.string());
        cacheRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return cacheRedisTemplate;
    }


}
