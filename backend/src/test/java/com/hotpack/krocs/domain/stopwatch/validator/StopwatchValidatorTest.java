package com.hotpack.krocs.domain.stopwatch.validator;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StopwatchValidatorTest {

    private StopwatchValidator stopwatchValidator;

    @BeforeEach
    void setUp() {
        stopwatchValidator = new StopwatchValidator();
    }

    // ========== validateCreateRequest 성공 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 유효한 데이터")
    void validateCreateRequest_Success() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatCode(() -> stopwatchValidator.validateCreateRequest(validRequest))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 최소 경과 시간 (1초)")
    void validateCreateRequest_Success_MinimumElapsedTime() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(validRequest));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 최대 경과 시간 (24시간)")
    void validateCreateRequest_Success_MaximumElapsedTime() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(validRequest));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 다양한 유효한 경과 시간")
    void validateCreateRequest_Success_VariousValidElapsedTimes() {
        // given
        String[] validElapsedTimes = {
                "00:00:01",  // 1초
                "00:01:00",  // 1분
                "01:00:00",  // 1시간
                "12:30:45",  // 중간값
                "23:59:59",  // 최대 근처
                "24:00:00"   // 최대값
        };

        // when & then
        for (String elapsedTime : validElapsedTimes) {
            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                    .build();

            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    "경과 시간 " + elapsedTime + "에 대해 검증 실패");
        }
    }

    // ========== validateCreateRequest 실패 테스트 - 시간 순서 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 시작 시간이 완료 시간보다 이후")
    void validateCreateRequest_Fail_StartAfterCompleted() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 12, 0, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    // ========== validateCreateRequest 실패 테스트 - elapsedTime 형식 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime이 null")
    void validateCreateRequest_Fail_ElapsedTimeNull() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime(null)
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime이 빈 문자열")
    void validateCreateRequest_Fail_ElapsedTimeEmpty() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime 형식 오류 (콜론 2개 미만)")
    void validateCreateRequest_Fail_ElapsedTimeInvalidFormat_LessColons() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30")  // HH:MM:SS 형식이 아님
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime 형식 오류 (콜론 3개 이상)")
    void validateCreateRequest_Fail_ElapsedTimeInvalidFormat_MoreColons() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00:00")  // 너무 많은 콜론
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime에 문자 포함")
    void validateCreateRequest_Fail_ElapsedTimeWithLetters() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:3a:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - elapsedTime에 공백 포함")
    void validateCreateRequest_Fail_ElapsedTimeWithSpaces() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01: 30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    // ========== validateCreateRequest 실패 테스트 - 시간 값 범위 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 음수 시간")
    void validateCreateRequest_Fail_NegativeHours() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("-01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 분이 60 이상")
    void validateCreateRequest_Fail_MinutesOutOfRange() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:60:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 초가 60 이상")
    void validateCreateRequest_Fail_SecondsOutOfRange() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:60")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 음수 분")
    void validateCreateRequest_Fail_NegativeMinutes() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:-30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 음수 초")
    void validateCreateRequest_Fail_NegativeSeconds() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:-15")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    // ========== validateCreateRequest 실패 테스트 - 경과 시간 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 경과 시간이 0초 이하 (음수)")
    void validateCreateRequest_Fail_ElapsedTimeNegative() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 경과 시간이 24시간 초과")
    void validateCreateRequest_Fail_ElapsedTimeExceedsMaximum() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:01")  // 24시간 1초
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 1))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    // ========== 경계값 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 경계값 테스트 (59분 59초)")
    void validateCreateRequest_BoundaryTest_59Minutes59Seconds() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:59:59")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 59, 59))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(validRequest));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 경계값 테스트 (23시간 59분 59초)")
    void validateCreateRequest_BoundaryTest_23Hours59Minutes59Seconds() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("23:59:59")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 23, 59, 59))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(validRequest));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 경계값 테스트 (정확히 24시간)")
    void validateCreateRequest_BoundaryTest_Exactly24Hours() {
        // given
        StopwatchCreateRequestDTO validRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(validRequest));
    }

    // ========== 다양한 날짜 시나리오 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 여러 날짜 패턴")
    void validateCreateRequest_Success_VariousDatePatterns() {
        // given
        LocalDateTime[][] dateTimePairs = {
                {LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 1, 1, 0)},
                {LocalDateTime.of(2025, 12, 31, 23, 0), LocalDateTime.of(2026, 1, 1, 0, 0)},
                {LocalDateTime.of(2025, 2, 28, 23, 30), LocalDateTime.of(2025, 3, 1, 0, 30)},
                {LocalDateTime.of(2024, 2, 29, 12, 0), LocalDateTime.of(2024, 2, 29, 13, 0)} // 윤년
        };

        // when & then
        for (LocalDateTime[] pair : dateTimePairs) {
            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(pair[0])
                    .elapsedTime("01:00:00")
                    .completedDateTime(pair[1])
                    .build();

            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    "날짜 패턴 " + pair[0] + " ~ " + pair[1] + "에 대해 검증 실패");
        }
    }

    // ========== 특수 케이스 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 앞뒤 공백이 있는 elapsedTime")
    void validateCreateRequest_Fail_ElapsedTimeWithLeadingTrailingSpaces() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime(" 01:30:00 ")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 특수 문자가 포함된 elapsedTime")
    void validateCreateRequest_Fail_ElapsedTimeWithSpecialCharacters() {
        // given
        String[] invalidElapsedTimes = {
                "01:30:00!",
                "01@30:00",
                "01:30#00",
                "01:30:00$"
        };

        // when & then
        for (String invalidTime : invalidElapsedTimes) {
            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                    .elapsedTime(invalidTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                    .build();

            assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(request))
                    .isInstanceOf(StopwatchException.class)
                    .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                            StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 실패 - 소수점이 포함된 elapsedTime")
    void validateCreateRequest_Fail_ElapsedTimeWithDecimal() {
        // given
        StopwatchCreateRequestDTO invalidRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30.5:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(invalidRequest))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 다양한 유효한 시간 간격")
    void validateCreateRequest_Success_VariousTimeIntervals() {
        // given
        Object[][] testCases = {
                {"00:00:01", 1},      // 1초
                {"00:00:30", 30},     // 30초
                {"00:01:00", 60},     // 1분
                {"00:30:00", 1800},   // 30분
                {"01:00:00", 3600},   // 1시간
                {"12:00:00", 43200},  // 12시간
                {"18:30:45", 66645},  // 18시간 30분 45초
                {"24:00:00", 86400}   // 24시간
        };

        // when & then
        for (Object[] testCase : testCases) {
            String elapsedTime = (String) testCase[0];
            int expectedSeconds = (int) testCase[1];

            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0).plusSeconds(expectedSeconds))
                    .build();

            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    "경과 시간 " + elapsedTime + " (" + expectedSeconds + "초)에 대해 검증 실패");
        }
    }

    // ========== convertTimeToSeconds 간접 테스트 ==========

    @Test
    @DisplayName("convertTimeToSeconds 간접 테스트 - 다양한 시간 변환")
    void convertTimeToSeconds_IndirectTest_VariousConversions() {
        // given - convertTimeToSeconds는 private이므로 validateCreateRequest를 통해 간접 테스트
        Object[][] testCases = {
                {"00:00:01", true},   // 1초 - 유효
                {"00:00:59", true},   // 59초 - 유효
                {"00:01:00", true},   // 1분 - 유효
                {"00:59:59", true},   // 59분 59초 - 유효
                {"01:00:00", true},   // 1시간 - 유효
                {"23:59:59", true},   // 23시간 59분 59초 - 유효
                {"24:00:00", true},   // 24시간 - 유효 (경계값)
                {"24:00:01", false},  // 24시간 1초 - 초과
                {"25:00:00", false},  // 25시간 - 초과
        };

        // when & then
        for (Object[] testCase : testCases) {
            String elapsedTime = (String) testCase[0];
            boolean shouldPass = (boolean) testCase[1];

            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                    .build();

            if (shouldPass) {
                assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                        "경과 시간 " + elapsedTime + "는 유효해야 함");
            } else {
                assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(request))
                        .isInstanceOf(StopwatchException.class)
                        .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                                StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
            }
        }
    }

    // ========== 엣지 케이스 종합 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 모든 엣지 케이스 종합")
    void validateCreateRequest_AllEdgeCases() {
        // given - 유효한 엣지 케이스들
        String[] validEdgeCases = {
                "00:00:01",  // 최소값
                "00:00:59",  // 초 경계
                "00:01:00",  // 분 단위
                "00:59:59",  // 분 경계
                "01:00:00",  // 시간 단위
                "23:59:59",  // 최대 근처
                "24:00:00"   // 최대값
        };

        // when & then
        for (String elapsedTime : validEdgeCases) {
            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                    .build();

            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    "유효한 엣지 케이스 " + elapsedTime + "에 대해 검증 실패");
        }

        // given - 무효한 엣지 케이스들
        String[] invalidEdgeCases = {
                "00:00:00",   // 0초
                "00:00:-01",  // 음수 초
                "00:-01:00",  // 음수 분
                "-01:00:00",  // 음수 시간
                "00:60:00",   // 분 초과
                "00:00:60",   // 초 초과
                "24:00:01",   // 최대값 초과
                "25:00:00",   // 25시간
                "100:00:00"   // 100시간
        };

        for (String elapsedTime : invalidEdgeCases) {
            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                    .build();

            assertThatThrownBy(() -> stopwatchValidator.validateCreateRequest(request))
                    .isInstanceOf(StopwatchException.class)
                    .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                            StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }
    }

    // ========== 정확한 시간 계산 검증 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 정확한 초 단위 계산")
    void validateCreateRequest_ExactSecondCalculation() {
        // given - 다양한 시간을 초로 변환하여 검증
        Object[][] timeMappings = {
                {"00:00:01", 1},
                {"00:01:00", 60},
                {"00:10:00", 600},
                {"01:00:00", 3600},
                {"01:30:00", 5400},
                {"02:45:30", 9930},
                {"12:00:00", 43200},
                {"23:59:59", 86399},
                {"24:00:00", 86400}
        };

        // when & then
        for (Object[] mapping : timeMappings) {
            String elapsedTime = (String) mapping[0];
            int expectedSeconds = (int) mapping[1];

            StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                    .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                    .elapsedTime(elapsedTime)
                    .completedDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0).plusSeconds(expectedSeconds))
                    .build();

            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    elapsedTime + " (" + expectedSeconds + "초) 검증 실패");
        }
    }

    // ========== 월 경계 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 월 말에서 다음 달로")
    void validateCreateRequest_Success_EndOfMonthToNextMonth() {
        // given - 10월 31일 23시부터 11월 1일 1시까지
        StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 31, 23, 0, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2025, 11, 1, 1, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 연말에서 다음 해로")
    void validateCreateRequest_Success_EndOfYearToNextYear() {
        // given - 12월 31일 23시부터 1월 1일 1시까지
        StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 12, 31, 23, 0, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2026, 1, 1, 1, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 2월 말 (윤년)")
    void validateCreateRequest_Success_LeapYearFebruary() {
        // given - 2024년 2월 29일 (윤년)
        StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2024, 2, 29, 23, 0, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2024, 3, 1, 1, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request));
    }

    @Test
    @DisplayName("스톱워치 생성 요청 검증 성공 - 2월 말 (평년)")
    void validateCreateRequest_Success_NonLeapYearFebruary() {
        // given - 2025년 2월 28일 (평년)
        StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 2, 28, 23, 0, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2025, 3, 1, 1, 0, 0))
                .build();

        // when & then
        assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request));
    }

    // ========== 종합 검증 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 요청 검증 - 모든 유효한 케이스 통과 확인")
    void validateCreateRequest_AllValidCases_Pass() {
        // given - 다양한 유효한 시나리오
        StopwatchCreateRequestDTO[] validRequests = {
                createRequest("00:00:01", 0, 0, 0, 0, 0, 1),
                createRequest("00:30:00", 0, 10, 0, 0, 10, 30),
                createRequest("01:00:00", 0, 9, 0, 0, 10, 0),
                createRequest("12:00:00", 0, 0, 0, 0, 12, 0),
                createRequest("24:00:00", 0, 0, 0, 1, 0, 0)
        };

        // when & then
        for (StopwatchCreateRequestDTO request : validRequests) {
            assertDoesNotThrow(() -> stopwatchValidator.validateCreateRequest(request),
                    "유효한 요청이 검증 실패: " + request.getElapsedTime());
        }
    }

    // ========== Helper Methods ==========

    private StopwatchCreateRequestDTO createRequest(String elapsedTime,
                                                    int startDayOffset, int startHour, int startMinute,
                                                    int endDayOffset, int endHour, int endMinute) {
        return createRequest(elapsedTime, startDayOffset, startHour, startMinute, 0,
                endDayOffset, endHour, endMinute, 0);
    }

    private StopwatchCreateRequestDTO createRequest(String elapsedTime,
                                                    int startDayOffset, int startHour, int startMinute, int startSecond,
                                                    int endDayOffset, int endHour, int endMinute, int endSecond) {
        LocalDateTime baseDate = LocalDateTime.of(2025, 10, 3, 0, 0, 0);

        return StopwatchCreateRequestDTO.builder()
                .startDateTime(baseDate.plusDays(startDayOffset)
                        .withHour(startHour)
                        .withMinute(startMinute)
                        .withSecond(startSecond))
                .elapsedTime(elapsedTime)
                .completedDateTime(baseDate.plusDays(endDayOffset)
                        .withHour(endHour)
                        .withMinute(endMinute)
                        .withSecond(endSecond))
                .build();
    }
}