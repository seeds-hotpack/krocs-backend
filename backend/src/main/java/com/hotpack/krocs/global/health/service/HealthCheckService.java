package com.hotpack.krocs.global.health.service;

import com.hotpack.krocs.global.health.dto.DependencyHealthStatus;
import com.hotpack.krocs.global.health.dto.HealthCheckResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final StringRedisTemplate stringRedisTemplate;

    public HealthCheckResponse check(String serviceName) {
        DependencyHealthStatus redisHealth = checkRedis();
        String overallStatus = "UP".equals(redisHealth.getStatus()) ? "UP" : "DOWN";

        return HealthCheckResponse.builder()
            .service(serviceName)
            .status(overallStatus)
            .timestamp(LocalDateTime.now())
            .redis(redisHealth)
            .build();
    }

    private DependencyHealthStatus checkRedis() {
        try {
            String pong = stringRedisTemplate.execute((RedisCallback<String>) RedisConnection::ping);
            boolean up = "PONG".equalsIgnoreCase(pong);
            return DependencyHealthStatus.builder()
                .name("redis")
                .status(up ? "UP" : "DOWN")
                .message(up ? "PONG" : "Unexpected response: " + pong)
                .build();
        } catch (RuntimeException e) {
            return DependencyHealthStatus.builder()
                .name("redis")
                .status("DOWN")
                .message(e.getMessage())
                .build();
        }
    }
}
