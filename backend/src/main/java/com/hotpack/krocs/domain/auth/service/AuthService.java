package com.hotpack.krocs.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTokenService redisTokenService;

    public ResponseCookie expireAuthTokenCookie() {
        return ResponseCookie.from("AUTH-TOKEN", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build();
    }
}
