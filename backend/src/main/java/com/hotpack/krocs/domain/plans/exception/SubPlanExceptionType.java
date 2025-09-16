package com.hotpack.krocs.domain.plans.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SubPlanExceptionType implements BaseCode {
    SUB_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBPLAN404", "소계획을 찾을 수 없습니다."),
    SUB_PLAN_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBPLAN404", "상위 일정(plan)을 찾을 수 없습니다."),
    SUB_PLAN_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "SUBPLAN400", "이미 완료된 소계획입니다."),
    SUB_PLAN_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "SUBPLAN400", "소계획 제목은 필수입니다."),
    SUB_PLAN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBPLAN500", "소계획 생성에 실패했습니다."),
    SUB_PLAN_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SUBPLAN400", "일정 제목이 너무 깁니다."),
    SUB_PLAN_PLAN_ID_MISSING(HttpStatus.BAD_REQUEST, "SUBPLAN400", "상위 일정(planId)은 필수입니다."),
    SUB_PLAN_UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "SUBPLAN401", "소계획에 대한 접근 권한이 없습니다."),
    SUB_PLAN_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBPLAN500", "소계획 생성에 실패했습니다."),
    SUB_PLAN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBPLAN500", "소계획 수정에 실패했습니다."),
    SUB_PLAN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBPLAN500", "소계획 삭제에 실패했습니다."),
    SUB_PLAN_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBPLAN500", "소계획 조회에 실패했습니다."),
    SUB_PLAN_INVALID_STATE(HttpStatus.BAD_REQUEST, "SUBPLAN400", "소계획의 상태가 유효하지 않습니다."),
    SUB_PLAN_INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "SUBPLAN400", "소계획 ID 형식이 잘못되었습니다."),
    SUB_PLAN_CREATE_EMPTY(HttpStatus.BAD_REQUEST, "SUBPLAN400", "생성할 소계획이 없습니다."),
    SUB_PLAN_PLAN_ID_IS_NULL(HttpStatus.BAD_REQUEST, "SUBPLAN400", "일정 ID가 null 입니다."),
    SUB_PLAN_ID_IS_NULL(HttpStatus.BAD_REQUEST, "SUBPLAN400", "소계획 ID가 null 입니다."),
    SUB_PLAN_NOT_BELONG_TO_PLAN(HttpStatus.BAD_REQUEST, "SUBPLAN400", "소계획이 해당 일정에 속하지 않습니다.");

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
