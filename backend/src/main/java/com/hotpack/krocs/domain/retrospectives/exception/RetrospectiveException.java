package com.hotpack.krocs.domain.retrospectives.exception;

import com.hotpack.krocs.global.common.response.exception.GeneralException;
import lombok.Getter;

@Getter
public class RetrospectiveException extends GeneralException {

    private final RetrospectiveExceptionType RetrospectiveExceptionType;

    public RetrospectiveException(RetrospectiveExceptionType RetrospectiveExceptionType) {
        super(RetrospectiveExceptionType);
        this.RetrospectiveExceptionType = RetrospectiveExceptionType;
    }
}
