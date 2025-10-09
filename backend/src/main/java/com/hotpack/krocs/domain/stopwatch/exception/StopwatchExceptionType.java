package com.hotpack.krocs.domain.stopwatch.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StopwatchExceptionType implements BaseCode {
    STOPWATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "STOPWATCH404", "스톱워치를 찾을 수 없습니다."),
    STOPWATCH_ALREADY_RUNNING(HttpStatus.CONFLICT, "STOPWATCH409", "이미 실행중인 스톱워치가 있습니다."),
    STOPWATCH_NOT_RUNNING(HttpStatus.BAD_REQUEST, "STOPWATCH400", "실행중인 스톱워치가 없습니다."),
    STOPWATCH_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "STOPWATCH400", "이미 완료된 스톱워치입니다."),
    INVALID_STOPWATCH_ACTION(HttpStatus.BAD_REQUEST, "STOPWATCH400", "유효하지 않은 스톱워치 동작입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason getReason() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .data("")
                .build();
    }

    @Override
    public Reason getReasonHttpStatus() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .data("")
                .build();
    }
}