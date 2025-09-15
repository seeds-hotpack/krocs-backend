package com.hotpack.krocs.global.security.oauth2.service;

import com.hotpack.krocs.global.security.oauth2.exception.OAuth2ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Value("${NAME_ATTRIBUTE_KEY}")
    private String nameAttributeKey;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        Map<String, Object> attrs = oAuth2User.getAttributes();

        Object idObj = attrs.get("id");
        String accountId = null;
        if (idObj instanceof Number num) {
            accountId = String.valueOf(num.longValue());
        } else if (idObj instanceof String str) {
            accountId = str;
        }

        if (accountId == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorType.KAKAO_ACCOUNT_ID_MISSING.getCode(),
                    OAuth2ErrorType.KAKAO_ACCOUNT_ID_MISSING.getMessage(),
                    OAuth2ErrorType.KAKAO_ACCOUNT_ID_MISSING.getUrl())
            );
        }

        Map<String, Object> profile = extractProfile(attrs);
        String name = (String) profile.get("nickname");
        String email = (String) profile.get("email");

        if (name == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorType.KAKAO_NAME_MISSING.getCode(),
                    OAuth2ErrorType.KAKAO_NAME_MISSING.getMessage(),
                    OAuth2ErrorType.KAKAO_NAME_MISSING.getUrl())
            );
        }

        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("accountId", accountId);
        customAttributes.put("name", name);
        customAttributes.put("email", email);

        return new DefaultOAuth2User(
            oAuth2User.getAuthorities(),
            customAttributes,
            nameAttributeKey
        );
    }

    private Map<String, Object> extractProfile(Map<String, Object> attribute) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");

        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorType.KAKAO_KAKAO_ACCOUNT_MISSING.getCode(),
                    OAuth2ErrorType.KAKAO_KAKAO_ACCOUNT_MISSING.getMessage(),
                    OAuth2ErrorType.KAKAO_KAKAO_ACCOUNT_MISSING.getUrl())
            );
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        if (profile == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorType.KAKAO_PROFILE_MISSING.getCode(),
                    OAuth2ErrorType.KAKAO_PROFILE_MISSING.getMessage(),
                    OAuth2ErrorType.KAKAO_PROFILE_MISSING.getUrl())
            );
        }

        return profile;
    }
}