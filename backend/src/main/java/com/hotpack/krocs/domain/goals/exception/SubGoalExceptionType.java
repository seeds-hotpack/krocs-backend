package com.hotpack.krocs.domain.goals.exception;

import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SubGoalExceptionType implements BaseCode {
    SUB_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBGOAL404", "소목표를 찾을 수 없습니다."),
    SUB_GOAL_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBGOAL404", "상위 대목표를 찾을 수 없습니다."),
    SUB_GOAL_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "SUBGOAL400", "이미 완료된 소목표입니다."),
    SUB_GOAL_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "SUBGOAL400", "소목표 제목은 필수입니다."),
    SUB_GOAL_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBGOAL500", "소목표 생성에 실패했습니다."),
    SUB_GOAL_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SUBGOAL400", "목표 제목이 너무 깁니다."),
    SUB_GOAL_GOAL_ID_MISSING(HttpStatus.BAD_REQUEST, "SUBGOAL400", "상위 목표(goalId)는 필수입니다."),
    SUB_GOAL_UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "SUBGOAL401", "소목표에 대한 접근 권한이 없습니다."),
    SUB_GOAL_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBGOAL500", "소목표 생성에 실패했습니다."),
    SUB_GOAL_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBGOAL500", "소목표 수정에 실패했습니다."),
    SUB_GOAL_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBGOAL500", "소목표 삭제에 실패했습니다."),
    SUB_GOAL_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SUBGOAL500", "소목표 조회에 실패했습니다."),
    SUB_GOAL_INVALID_STATE(HttpStatus.BAD_REQUEST, "SUBGOAL400", "소목표의 상태가 유효하지 않습니다."),
    SUB_GOAL_INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "SUBGOAL400", "소목표 ID 형식이 잘못되었습니다."),
    SUB_GOAL_CREATE_EMPTY(HttpStatus.BAD_REQUEST, "SUBGOAL400", "생성할 소목표가 없습니다."),
    SUB_GOAL_GOAL_ID_IS_NULL(HttpStatus.BAD_REQUEST, "SUBGOAL400", "대목표 ID가 null 입니다."),
    SUB_GOAL_ID_IS_NULL(HttpStatus.BAD_REQUEST, "SUBGOAL400", "소목표 ID가 null 입니다."),
    SUB_GOAL_NOT_BELONG_TO_GOAL(HttpStatus.BAD_REQUEST, "SUBGOAL400", "소목표가 해당 목표에 속하지 않습니다."),
    SUB_GOAL_ACCESS_DENIED(HttpStatus.NOT_FOUND, "SUBGOAL404", "해당 소목표에 접근할 수 없습니다.");

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
