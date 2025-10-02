package com.hotpack.krocs.domain.timeline.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TimelineExceptionType implements BaseCode {

    TIMELINE_FOUND_FAILED(HttpStatus.NOT_FOUND, "TIMELINE404", "타임라인을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason getReason() {
        return com.hotpack.krocs.global.common.response.code.Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .data("")
                .build();
    }

    @Override
    public Reason getReasonHttpStatus() {
        return com.hotpack.krocs.global.common.response.code.Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .data("")
                .build();
    }
}
