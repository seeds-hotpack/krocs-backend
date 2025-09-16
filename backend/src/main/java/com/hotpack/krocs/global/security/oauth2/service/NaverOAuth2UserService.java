package com.hotpack.krocs.global.security.oauth2.service;

import com.hotpack.krocs.global.security.oauth2.exception.CustomOAuth2AuthenticationException;
import com.hotpack.krocs.global.security.oauth2.exception.OAuth2ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Value("${NAME_ATTRIBUTE_KEY}")
    private String nameAttributeKey;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            throw new CustomOAuth2AuthenticationException(OAuth2ErrorType.NAVER_RESPONSE_MISSING);
        }

        String accountId = (String) response.get("id");
        String name = (String) response.get("name");
        String email = (String) response.get("email");

        if (accountId == null) {
            throw new CustomOAuth2AuthenticationException(OAuth2ErrorType.NAVER_ACCOUNT_ID_MISSING);
        }

        if (name == null) {
            throw new CustomOAuth2AuthenticationException(OAuth2ErrorType.NAVER_NAME_MISSING);
        }

        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("accountId", accountId);
        customAttributes.put("name", name);
        customAttributes.put("email", email);

        return new DefaultOAuth2User(
            oauth2User.getAuthorities(),
            customAttributes,
            nameAttributeKey
        );
    }

}