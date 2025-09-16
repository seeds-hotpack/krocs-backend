package com.hotpack.krocs.domain.plans.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlanExceptionType implements BaseCode {
    PLAN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PLAN500", "일정 수정에 실패했습니다."),
    PLAN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PLAN500", "일정 생성에 실패했습니다."),
    PLAN_FOUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PLAN500", "일정 조회에 실패했습니다."),
    PLAN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PLAN500", "일정 삭제에 실패했습니다."),
    PLAN_DUPLICATE_TITLE(HttpStatus.CONFLICT, "PLAN409", "동일한 제목의 일정이 이미 존재합니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404", "일정을 찾을 수 없습니다."),
    PLAN_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "PLAN400", "이미 완료된 일정입니다."),
    INVALID_PLAN_DATE_RANGE(HttpStatus.BAD_REQUEST, "PLAN400", "유효하지 않은 일정 기간입니다."),
    PLAN_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "PLAN400", "일정 제목은 필수입니다."),
    PLAN_PLANCATEGORY_IS_NULL(HttpStatus.BAD_REQUEST, "PLAN400", "일정 카테고리가 기본값이 아닌 null입니다"),
    PLAN_DURATION_INVALID(HttpStatus.BAD_REQUEST, "PLAN400", "일정 기간은 1일 이상이어야 합니다."),
    PLAN_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "PLAN400", "일정 제목이 너무 깁니다."),
    PLAN_INVALID_PRIORITY(HttpStatus.BAD_REQUEST, "PLAN400", "유효하지 않은 우선순위입니다."),
    PLAN_DURATION_TOO_LONG(HttpStatus.BAD_REQUEST, "PLAN400", "일정 기간이 너무 깁니다."),
    PLAN_INVALID_PLAN_ID(HttpStatus.BAD_REQUEST, "PLAN400", "유효하지 않은 일정 ID입니다."),
    PLAN_START_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "PLAN400", "일정 시작시간을 입력해주세요."),
    PLAN_END_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "PLAN400", "일정 종료시간을 입력해주세요."),
    PLAN_INVALID_ENERGY(HttpStatus.BAD_REQUEST, "PLAN400", "유효하지 않은 에너지 값입니다."),
    PLAN_SUB_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404", "세부일정을 찾을 수 없습니다."),
    PLAN_ID_IS_NULL(HttpStatus.BAD_REQUEST, "PLAN400", "계획 ID가 null 입니다.");

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
