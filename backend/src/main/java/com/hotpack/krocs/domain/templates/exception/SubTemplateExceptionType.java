package com.hotpack.krocs.domain.templates.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SubTemplateExceptionType implements BaseCode {
    SUB_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBTEMPLATE404", "서브 탬플릿을 찾을 수 없습니다."),
    SUB_TEMPLATE_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBTEMPLATE404",
        "서브 템플릿의 상위 템플릿을 찾을 수 없습니다."),
    SUB_TEMPLATE_TEMPLATE_ID_IS_NULL(HttpStatus.NOT_FOUND, "SUBTEMPLATE404",
        "서브 템플릿의 상위 템플릿의 아이디가 null 입니다."),
    SUB_TEMPLATE_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "SUBTEMPLATE400", "서브 탬플릿 제목은 필수입니다."),
    SUB_TEMPLATE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBTEMPLATE500",
        "서브 탬플릿 생성에 실패했습니다."),
    SUB_TEMPLATE_FOUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBTEMPLATE500",
        "서브 탬플릿 조회에 실패했습니다."),
    SUB_TEMPLATE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBTEMPLATE500",
        "서브 탬플릿 수정에 실패했습니다."),
    SUB_TEMPLATE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBTEMPLATE500",
        "서브 탬플릿 삭제에 실패했습니다."),
    SUB_TEMPLATE_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SUBTEMPLATE400", "서브 탬플릿 제목이 너무 깁니다."),
    SUB_TEMPLATE_SUB_TEMPLATE_ID_IS_NULL(HttpStatus.BAD_REQUEST,
        "SUBTEMPLATE400",
        "서브 템플릿 아이디가 null 입니다.");

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
