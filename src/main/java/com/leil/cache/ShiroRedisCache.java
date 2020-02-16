package com.leil.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShiroRedisCache<K, V> implements Cache<K, V> {

    @Autowired
    RedisTemplate<String, byte[]> shiroRedisTemplate;

    @Value("${shiro.cache.cacheKey}")
    String shiroCacheKey;

    protected byte[] getKey(K k) {
        byte[] bytes = SerializationUtils.serialize(k);
        return bytes;
    }

    @Override
    public V get(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        byte[] key = this.getKey(k);
        byte[] bytes = (byte[]) this.shiroRedisTemplate.opsForHash().get(this.shiroCacheKey, key);
        V val = (V) SerializationUtils.deserialize(bytes);
        return val;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        if (k == null || v == null) {
            return null;
        }
        byte[] key = this.getKey(k);
        byte[] bytes = SerializationUtils.serialize(v);
        this.shiroRedisTemplate.opsForHash().put(this.shiroCacheKey, key, bytes);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        byte[] key = this.getKey(k);
        V v = this.get(k);
        this.shiroRedisTemplate.opsForHash().delete(this.shiroCacheKey, key);
        return v;
    }

    @Override
    public void clear() throws CacheException {
        // 为了保护redis中的数据，clear()方法不要重写
    }

    @Override
    public int size() {
        String sizeStr = this.shiroRedisTemplate.opsForHash().size(this.shiroCacheKey).toString();
        return Integer.parseInt(sizeStr);
    }

    @Override
    public Set<K> keys() {
        Set<Object> keys = this.shiroRedisTemplate.opsForHash().keys(this.shiroCacheKey);
        Set<K> kSet = keys.stream().map(key -> {
            byte[] bytes = (byte[]) key;
            K k = (K) SerializationUtils.deserialize(bytes);
            return k;
        }).collect(Collectors.toSet());
        return kSet;
    }

    @Override
    public Collection<V> values() {
        List<Object> values = this.shiroRedisTemplate.opsForHash().values(this.shiroCacheKey);
        Set<V> vSet = values.stream().map(val -> {
            byte[] bytes = (byte[]) val;
            V v = (V) SerializationUtils.deserialize(bytes);
            return v;
        }).collect(Collectors.toSet());
        return vSet;
    }



}
