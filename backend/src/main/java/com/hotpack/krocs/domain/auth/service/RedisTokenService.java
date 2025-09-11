package com.hotpack.krocs.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotpack.krocs.domain.auth.dto.UserSession;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_KEY_PREFIX = "auth:token:";

    public Optional<UserSession> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String json = stringRedisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, UserSession.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public boolean existsToken(String token) {
        String json = stringRedisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        return json != null;
    }

    public void storeToken(String token, UserSession session, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(session);
            String key = TOKEN_KEY_PREFIX + token;
            if (ttl == null || ttl.isZero() || ttl.isNegative()) {
                stringRedisTemplate.opsForValue().set(key, json);
            } else {
                stringRedisTemplate.opsForValue().set(key, json, ttl);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize session", e);
        }
    }

    public void revokeToken(String token) {
        stringRedisTemplate.delete(TOKEN_KEY_PREFIX + token);
    }
}

