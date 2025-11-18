package com.hotpack.krocs.domain.auth.controller;

import com.hotpack.krocs.domain.auth.dto.response.UserResponseDTO;
import com.hotpack.krocs.domain.auth.service.AuthService;
import com.hotpack.krocs.domain.auth.service.RedisTokenService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RedisTokenService redisTokenService;
    private final AuthService authService;

    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> me(@Login Long userId) {
        if (userId == null) {
            return ApiResponse.onFailure("GLOBAL401", "인증 실패");
        }

        // UserResponseDTO responseDto = authService.getUser(userId);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CookieValue("AUTH-TOKEN") String token,
        HttpServletResponse response) {

        if (redisTokenService.existsToken(token)) {
            redisTokenService.revokeToken(token);
        }

        ResponseCookie cookie = authService.expireAuthTokenCookie();
        response.addHeader("Set-Cookie", cookie.toString());

        return ApiResponse.success();
    }
}

