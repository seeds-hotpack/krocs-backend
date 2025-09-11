package com.hotpack.krocs.global.security.oauth2.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotpack.krocs.domain.auth.dto.UserSession;
import com.hotpack.krocs.domain.auth.service.RedisTokenService;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.repository.UserRepository;
import com.hotpack.krocs.global.common.entity.Status;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;
    private final RedisTokenService redisTokenService;
    private final ObjectMapper objectMapper;

    @Value("${frontOrigin}")
    private String successRedirect;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        // 유저 조회/생성
        User user = findOrCreateUser(authentication);
        // 클라이언트에게 AUTH-TOKEN 추출
        Cookie[] cookies = request.getCookies();
        String token = findToken(cookies);

        // 클라이언트가 토큰을 가지고 있고, Redis에 토큰이 있다면 바로 프론트로 리다이렉트
        if (token != null && redisTokenService.existsToken(token)) {
            response.sendRedirect(successRedirect);
            return;
        }

        UserSession session = UserSession.of(String.valueOf(user.getUserId()), user.getName());
        token = UUID.randomUUID().toString();
        try {
            String json = objectMapper.writeValueAsString(session);
            stringRedisTemplate.opsForValue()
                .set("auth:token:" + token, json, Duration.ofDays(7));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("세션 직렬화 실패", e);
        }

        Cookie cookie = createCookie(token);
        response.addCookie(cookie);

        response.sendRedirect(successRedirect);
    }

    private String findToken(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("AUTH-TOKEN".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }

    private Cookie createCookie(String token) {
        Cookie cookie = new Cookie("AUTH-TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 3600);
        cookie.setAttribute("SameSite", "None");

        return cookie;
    }

    private AccountType getAccountType(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            if (registrationId.equals("kakao")) {
                return AccountType.KAKAO;
            }
        }

        return null;
    }

    private User findOrCreateUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = ((Number) oAuth2User.getAttribute("id")).toString();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = null;
        if (name != null) {
            user = userRepository.findUserByAccountIdAndStatus(providerId, Status.ACTIVE)
                .orElse(null);
        }

        if (user == null) {
            user = User.builder()
                .accountId(providerId)
                .name(name != null ? name : "user")
                .email(email)
                .accountType(getAccountType(authentication))
                .build();
            user = userRepository.save(user);
        }

        return user;
    }
}
