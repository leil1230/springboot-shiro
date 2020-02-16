package com.leil.session;


import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisSessionDAO extends AbstractSessionDAO {

    @Autowired
    RedisTemplate<String, byte[]> shiroRedisTemplate;

    @Value("${shiro.sessionKeyPrefix}")
    String shiroSessionRedisKeyPrefix;

    @Value("${shiro.sessionExpiredTime}")
    long shiroSessionExpiredTime;

    protected String getSessionKey(String sessionId) {
        return this.shiroSessionRedisKeyPrefix + sessionId;
    }

    protected void saveSession(Session session) {
        if (session.getId() != null) {
            String key = this.getSessionKey(session.getId().toString());
            byte[] bytes = SerializationUtils.serialize(session);
            this.shiroRedisTemplate.opsForValue().set(key, bytes);
            this.shiroRedisTemplate.expire(key, shiroSessionExpiredTime, TimeUnit.SECONDS);
        }
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String key = this.getSessionKey(sessionId.toString());
        byte[] bytes = this.shiroRedisTemplate.opsForValue().get(key);
        Session session = (Session) SerializationUtils.deserialize(bytes);
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session != null) {
            this.saveSession(session);
        }
    }

    @Override
    public void delete(Session session) {
        String key = this.getSessionKey(session.getId().toString());
        this.shiroRedisTemplate.delete(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> keys = this.shiroRedisTemplate.keys(this.shiroSessionRedisKeyPrefix + "*");
        Set<Session> sessions = new HashSet<>();
        for (String key : keys) {
            byte[] bytes = this.shiroRedisTemplate.opsForValue().get(key);
            Session session = (Session) SerializationUtils.deserialize(bytes);
            sessions.add(session);
        }
        return sessions;
    }
}
