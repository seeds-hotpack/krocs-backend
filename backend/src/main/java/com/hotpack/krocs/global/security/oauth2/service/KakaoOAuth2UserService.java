package com.hotpack.krocs.global.security.oauth2.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attrs = oAuth2User.getAttributes();
        Long accountId = ((Number) attrs.get("id")).longValue();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attrs.get("kakao_account");
        Map<String, Object> profile =
            kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String name = profile != null ? (String) profile.get("nickname") : null;

        return new DefaultOAuth2User(
            oAuth2User.getAuthorities(),
            Map.of(
                "accountId", accountId,
                "name", name
            ),
            "accountId"
        );
    }
}