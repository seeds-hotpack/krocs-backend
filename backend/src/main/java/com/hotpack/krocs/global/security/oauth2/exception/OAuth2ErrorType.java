package com.hotpack.krocs.global.security.oauth2.exception;

public enum OAuth2ErrorType {
    PROVIDER_UNAVAILABLE("provider_unavailable", "지원하지 않는 로그인 방식입니다.", null),
    GOOGLE_ACCOUNT_ID_MISSING("google_account_id_missing", "구글 로그인 중 필수 정보가 누락되었습니다.(accountId)",
        null),
    GOOGLE_NAME_MISSING("google_name_missing", "네이버 로그인 중 필수 정보가 누락되었습니다.(name)",
        null),
    KAKAO_ACCOUNT_ID_MISSING("kakao_account_id_missing", "카카오 로그인 중 필수 정보가 누락되었습니다.(accountId)",
        null),
    KAKAO_NAME_MISSING("kakao_name_missing", "카카오 로그인 중 필수 정보가 누락되었습니다.(name)", null),
    KAKAO_KAKAO_ACCOUNT_MISSING("kakao_kakao_account_missing",
        "카카로 로그인 중 필수 정보가 누락되었습니다.(kakao_account)", null),
    KAKAO_PROFILE_MISSING("kakao_profile_missing", "카카오 로그인 중 필수 정보가 누락되었습니다.(profile)", null),
    NAVER_RESPONSE_MISSING("naver_response_missing", "네이버 로그인 중 필수 정보가 누락되었습니다.(response)",
        null),
    NAVER_ACCOUNT_ID_MISSING("naver_account_id_missing", "네이버 로그인 중 필수 정보가 누락되었습니다.(accountId)",
        null),
    NAVER_NAME_MISSING("naver_name_missing", "네이버 로그인 중 필수 정보가 누락되었습니다.(name)",
        null);

    private String code;
    private String message;
    private String url;

    OAuth2ErrorType(String code, String message, String url) {

    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
