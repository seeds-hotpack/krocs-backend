package com.hotpack.krocs.global.security.oauth2.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            throw new OAuth2AuthenticationException("Naver response attribute is missing");
        }

        String accountId = (String) response.get("id");
        String name = (String) response.get("name");
        String email = (String) response.get("email");
        
        if (accountId == null) {
            throw new OAuth2AuthenticationException("Naver accountId is missing");
        }
        if (name == null) {
            name = (String) response.get("naverUser");
        }

        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("accountId", accountId);
        customAttributes.put("name", name);
        customAttributes.put("email", email);

        String userNameAttributeName = "accountId";

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
            oauth2User.getAuthorities(),
            customAttributes,
            userNameAttributeName
        );
    }

}