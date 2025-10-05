package com.hotpack.krocs.domain.retrospectives.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RetrospectiveExceptionType implements BaseCode {

    RETRO_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "RETRO404", "회고를 진행할 목표를 찾을 수 없습니다."),
    RETRO_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "RETRO404", "회고 주체 사람을 찾을 수 없습니다."),
    RETRO_INVALID_OUTCOME_KEY(HttpStatus.BAD_REQUEST, "RETRO400", "존재하지 않는 결과가 포함되어 있습니다."),
    RETRO_INVALID_FACTORS_SIZE(HttpStatus.BAD_REQUEST, "RETRO400", "회고 요인은 1개 이상, 3개 이하로 선택해야 합니다."),
    RETRO_FACTORS_MISMATCH_OUTCOME(HttpStatus.BAD_REQUEST, "RETRO400", "회고 결과(성공/실패)와 요인의 타입이 일치하지 않습니다."),
    RETRO_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RETRO500", "회고 생성에 실패했습니다."),

    RETRO_NOT_FOUND(HttpStatus.NOT_FOUND, "RETRO404", "삭제할 회고를 찾을 수 없습니다."),
    RETRO_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RETRO500", "회고 삭제에 실패했습니다.");

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
