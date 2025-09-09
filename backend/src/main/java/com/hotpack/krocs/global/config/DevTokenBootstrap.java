package com.hotpack.krocs.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotpack.krocs.domain.auth.dto.UserSession;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.domain.enums.UserRole;
import com.hotpack.krocs.domain.user.repository.UserRepository;
import com.hotpack.krocs.global.common.entity.Status;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class DevTokenBootstrap {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Value("${auth.dev.bootstrap-token:test-token}")
    private String bootstrapToken;

    @Value("${auth.dev.user.id:1}")
    private String bootstrapUserId;

    @Value("${auth.dev.user.name:Developer}")
    private String bootstrapUserName;

    @Value("${auth.dev.user.role:USER}")
    private String bootstrapUserRole;

    @Value("${auth.dev.user.email:dev@krocs.local}")
    private String bootstrapUserEmail;

    @PostConstruct
    public void init() {
        try {
            UserRole role;
            try {
                role = UserRole.valueOf(bootstrapUserRole.toUpperCase());
            } catch (Exception e) {
                role = UserRole.USER;
            }
            final UserRole resolvedRole = role;

            User user;
            try {
                user = userRepository.findUsersByEmailAndStatus(bootstrapUserEmail, Status.ACTIVE)
                    .orElseGet(() -> userRepository.save(User.builder()
                        .name(bootstrapUserName)
                        .email(bootstrapUserEmail)
                        .password("{noop}dev")
                        .role(resolvedRole)
                        .accountType(AccountType.LOCAL)
                        .build()));
            } catch (Exception e) {
                user = userRepository.findUsersByEmailAndStatus(bootstrapUserEmail, Status.ACTIVE)
                    .orElse(null);
                if (user == null) {
                    System.err.println("Failed to create or find user: " + bootstrapUserEmail);
                    return;
                }
            }

            UserSession session = UserSession.of(String.valueOf(user.getUserId()), bootstrapUserName,
                Set.of(resolvedRole));
            try {
                String json = objectMapper.writeValueAsString(session);
                stringRedisTemplate.opsForValue()
                    .set("auth:token:" + bootstrapToken, json, Duration.ofDays(7));
            } catch (JsonProcessingException e) {
            }
        } catch (Exception e) {
            System.err.println("DevTokenBootstrap initialization failed: " + e.getMessage());
        }
    }
}

