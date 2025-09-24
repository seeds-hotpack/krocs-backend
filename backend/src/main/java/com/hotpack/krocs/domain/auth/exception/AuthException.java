package com.hotpack.krocs.domain.auth.exception;

import com.hotpack.krocs.global.common.response.exception.GeneralException;
import lombok.Getter;

@Getter
public class AuthException extends GeneralException {

    private final AuthExceptionType authExceptionType;


    public AuthException(AuthExceptionType authExceptionType) {
        super(authExceptionType);
        this.authExceptionType = authExceptionType;
    }
}
