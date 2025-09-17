package com.hotpack.krocs.global.security.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class CustomOAuth2AuthenticationException extends OAuth2AuthenticationException {

    public CustomOAuth2AuthenticationException(OAuth2ErrorType errorType) {
        super(new OAuth2Error(
            errorType.getCode(),
            errorType.getMessage(),
            errorType.getUrl()
        ));
    }
}