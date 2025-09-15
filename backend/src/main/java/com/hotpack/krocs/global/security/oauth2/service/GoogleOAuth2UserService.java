package com.hotpack.krocs.global.security.oauth2.service;

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
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Value("${NAME_ATTRIBUTE_KEY}")
    private String nameAttributeKey;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String accountId = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");

        if (accountId == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("google_account_id_missing", "구글 로그인 중 필수 정보가 누락되었습니다.(accountId)",
                    null)
            );
        }

        if (name == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("google_name_missing", "네이버 로그인 중 필수 정보가 누락되었습니다.(name)",
                    null)
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

}
