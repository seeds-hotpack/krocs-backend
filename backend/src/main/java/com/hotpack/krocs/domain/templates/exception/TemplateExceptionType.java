package com.hotpack.krocs.domain.templates.exception;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.repository.TemplateRepository;
import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Getter
@AllArgsConstructor
public enum TemplateExceptionType implements BaseCode {
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "TEMPLATE404", "탬플릿을 찾을 수 없습니다."),
    TEMPLATE_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "TEMPLATE400", "탬플릿 제목은 필수입니다."),
    TEMPLATE_DURATION_INVALID(HttpStatus.BAD_REQUEST, "TEMPLATE400", "탬플릿 기간은 1일 이상이어야 합니다."),
    TEMPLATE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TEMPLATE500", "탬플릿 생성에 실패했습니다."),
    TEMPLATE_FOUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TEMPLATE500", "탬플릿 조회에 실패했습니다."),
    TEMPLATE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TEMPLATE500", "탬플릿 수정에 실패했습니다."),
    TEMPLATE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TEMPLATE500", "탬플릿 삭제에 실패했습니다."),
    TEMPLATE_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "TEMPLATE400", "탬플릿 제목이 너무 깁니다."),
    TEMPLATE_INVALID_PRIORITY(HttpStatus.BAD_REQUEST, "TEMPLATE400", "유효하지 않은 우선순위입니다.");

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
