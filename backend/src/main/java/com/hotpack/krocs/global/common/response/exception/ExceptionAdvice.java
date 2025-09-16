package com.hotpack.krocs.global.common.response.exception;

import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.common.response.code.Reason;
import com.hotpack.krocs.global.common.response.code.resultCode.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.context.MessageSource;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
public class ExceptionAdvice extends ResponseEntityExceptionHandler {


    private final MessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternalConstraint(e, ErrorStatus.VALIDATION_ERROR, HttpHeaders.EMPTY,request);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> exception(Exception e, WebRequest request) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
        return handleExceptionInternalFalse(e, ErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(),request, e.getMessage());
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> onThrowException(GeneralException generalException, HttpServletRequest request) {
        Reason errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException,errorReasonHttpStatus,null,request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        log.warn("잘못된 인자가 전달됨: {}", e.getMessage());
        return handleExceptionInternalFalse(e, ErrorStatus.BAD_REQUEST, HttpHeaders.EMPTY, ErrorStatus.BAD_REQUEST.getHttpStatus(), request, e.getMessage());
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e, WebRequest request) {
        log.warn("잘못된 상태: {}", e.getMessage());
        return handleExceptionInternalFalse(e, ErrorStatus.BUSINESS_LOGIC_ERROR, HttpHeaders.EMPTY, ErrorStatus.BUSINESS_LOGIC_ERROR.getHttpStatus(), request, e.getMessage());
    }

    private ResponseEntity<ApiResponse<Void>> handleExceptionInternal(Exception e, Reason reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        ApiResponse<Void> body = ApiResponse.onFailure(reason.getCode(),reason.getMessage());
//        e.printStackTrace();

        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
        return new ResponseEntity<>(body, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }

    private ResponseEntity<ApiResponse<Void>> handleExceptionInternalFalse(Exception e, ErrorStatus errorCommonStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiResponse<Void> body = ApiResponse.onFailure(errorCommonStatus.getCode(),errorCommonStatus.getMessage(),errorPoint);
        ResponseEntity<Object> responseEntity = super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
        return new ResponseEntity<>(body, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }

    private ResponseEntity<ApiResponse<Void>> handleExceptionInternalArgs(Exception e, HttpHeaders headers, ErrorStatus errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Void> body = ApiResponse.onFailure(errorCommonStatus.getCode(),errorCommonStatus.getMessage(),errorArgs);
        ResponseEntity<Object> responseEntity = super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
        return new ResponseEntity<>(body, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }

    private ResponseEntity<ApiResponse<Void>> handleExceptionInternalConstraint(Exception e, ErrorStatus errorCommonStatus,
                                                                     HttpHeaders headers, WebRequest request) {
        ApiResponse<Void> body = ApiResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage());
        ResponseEntity<Object> responseEntity = super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
        return new ResponseEntity<>(body, responseEntity.getHeaders(), responseEntity.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String resolvedMessage = messageSource.getMessage(
                error.getDefaultMessage(), // 메시지 키
                error.getArguments(),      // {max}, {min} 같은 파라미터 치환
                error.getDefaultMessage(), // 키가 없으면 fallback
                LocaleContextHolder.getLocale()
            );
            errors.put(error.getField(), resolvedMessage);
        });

        Object target = ex.getBindingResult().getTarget();
        ErrorStatus errorStatus = ErrorStatus.VALIDATION_ERROR;

        ApiResponse<Void> body = ApiResponse.onFailure(
            errorStatus.getCode(),
            errorStatus.getMessage(),
            errors
        );

        return new ResponseEntity<>(body, errorStatus.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        ErrorStatus errorStatus = ErrorStatus.BAD_REQUEST_BODY;

        String message = messageSource.getMessage(
            errorStatus.getCode(),
            null,
            "잘못된 데이터 타입",
            LocaleContextHolder.getLocale()
        );

        Map<String, String> result = new LinkedHashMap<>();

        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException mie) {
            if (!mie.getPath().isEmpty()) {
                String fieldName = mie.getPath().get(0).getFieldName();
                result.put(fieldName, message);
            } else {
                result.put("unknown", message);
            }
        } else {
            result.put("unknown", message);
        }

        ApiResponse<Map<String, String>> body = ApiResponse.onFailure(
            errorStatus.getCode(),
            message,
            result
        );

        return new ResponseEntity<>(body, headers, errorStatus.getHttpStatus());
    }


}