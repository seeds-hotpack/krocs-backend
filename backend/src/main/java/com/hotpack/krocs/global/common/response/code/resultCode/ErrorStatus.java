package com.hotpack.krocs.global.common.response.code.resultCode;


import com.hotpack.krocs.global.common.response.code.BaseCode;
import com.hotpack.krocs.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {
    // 글로벌 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL500", "서버 오류"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL400", "잘못된 요청"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLOBAL401", "인증 실패"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL403", "접근 권한 없음"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL404", "리소스를 찾을 수 없음"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL405", "허용되지 않는 메서드"),
    CONFLICT(HttpStatus.CONFLICT, "GLOBAL409", "리소스 충돌"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "GLOBAL415", "지원하지 않는 미디어 타입"),

    // 검증 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION400", "입력값 검증 실패"),
    BAD_REQUEST_BODY(HttpStatus.BAD_REQUEST, "BAD_REQUEST_BODY400", "잘못된 데이터 타입"),
    INVALID_SORT_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_SORT_PARAMETER400", "잘못된 정렬 기준입니다."),

    // 비즈니스 로직 에러
    BUSINESS_LOGIC_ERROR(HttpStatus.BAD_REQUEST, "BUSINESS400", "비즈니스 로직 오류"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE404", "요청한 리소스를 찾을 수 없습니다"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DUPLICATE409", "중복된 리소스입니다"),
    OAUTH2_SESSION_SERIALIZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_500",
        "세션 직렬화에 실패했습니다.");

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