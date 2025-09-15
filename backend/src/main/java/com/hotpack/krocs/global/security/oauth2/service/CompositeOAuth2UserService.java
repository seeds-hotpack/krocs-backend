package com.hotpack.krocs.global.security.oauth2.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompositeOAuth2UserService implements
    OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final KakaoOAuth2UserService kakaoOAuth2UserService;
    private final NaverOAuth2UserService naverOAuth2UserService;
    private final GoogleOAuth2UserService googleOAuth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        String registrationId = req.getClientRegistration().getRegistrationId();

        Map<String, OAuth2UserService<OAuth2UserRequest, OAuth2User>> delegates = Map.of(
            "kakao", kakaoOAuth2UserService,
            "naver", naverOAuth2UserService,
            "google", googleOAuth2UserService
        );

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = delegates.get(registrationId);
        if (delegate == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("provider_unavailable", "지원하지 않는 로그인 방식입니다.",
                    null)
            );
        }

        return delegate.loadUser(req);
    }
}
