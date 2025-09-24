package com.hotpack.krocs.domain.plans.validator;

import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.validator.PlanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlanValidatorTest {
    private PlanValidator planValidator;

    @BeforeEach
    void setUp() {
        planValidator = new PlanValidator();
    }

    // ========== CREATE 관련 테스트 (validatePlanCreation) ==========

    @Test
    @DisplayName("유효성 검사 성공 - allDay = false")
    void validatePlanCreation_Success_AllDayFalse() {
        // given
        PlanCreateRequestDTO validRequest = PlanCreateRequestDTO.builder()
            .title("테스트 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatCode(() -> planValidator.validatePlanCreation(validRequest))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효성 검사 성공 - allDay = true, 같은 날짜")
    void validatePlanCreation_Success_AllDayTrue_SameDate() {
        // given
        PlanCreateRequestDTO allDayRequest = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 3, 10, 30)) // 시간은 무시됨
            .endDateTime(LocalDateTime.of(2025, 8, 3, 15, 45))   // 시간은 무시됨
            .allDay(true)
            .build();

        // when & then
        assertThatCode(() -> planValidator.validatePlanCreation(allDayRequest))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효성 검사 성공 - allDay = true, 여러 날짜")
    void validatePlanCreation_Success_AllDayTrue_MultipleDays() {
        // given
        PlanCreateRequestDTO allDayRequest = PlanCreateRequestDTO.builder()
            .title("여러 날 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 3, 23, 59)) // 시간은 무시됨
            .endDateTime(LocalDateTime.of(2025, 8, 5, 0, 1))     // 시간은 무시됨
            .allDay(true)
            .build();

        // when & then
        assertThatCode(() -> planValidator.validatePlanCreation(allDayRequest))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효성 검사 성공 - subGoalId가 null")
    void validatePlanCreation_Success_NullSubGoalId() {
        // given
        PlanCreateRequestDTO validRequest = PlanCreateRequestDTO.builder()
            .title("서브골 없는 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatCode(() -> planValidator.validatePlanCreation(validRequest))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효성 검사 성공 - allDay가 null인 경우 (기본값 false로 처리)")
    void validatePlanCreation_Success_AllDayNull() {
        // given
        PlanCreateRequestDTO validRequest = PlanCreateRequestDTO.builder()
            .title("테스트 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(null) // null인 경우
            .build();

        // when & then
        assertThatCode(() -> planValidator.validatePlanCreation(validRequest))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = false인데 시작시간 누락")
    void validatePlanCreation_Fail_NoStartTime() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("시간 지정 일정")
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_START_TIME_REQUIRED);
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = false인데 종료시간 누락")
    void validatePlanCreation_Fail_NoEndTime() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("시간 지정 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_END_TIME_REQUIRED);
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = true인데 시작시간 누락")
    void validatePlanCreation_Fail_AllDayTrue_NoStartTime() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .endDateTime(LocalDateTime.of(2025, 8, 1, 0, 0))
            .allDay(true)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_START_TIME_REQUIRED);
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = true인데 종료시간 누락")
    void validatePlanCreation_Fail_AllDayTrue_NoEndTime() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 0, 0))
            .allDay(true)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_END_TIME_REQUIRED);
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = false, 시작시간이 종료시간보다 늦음")
    void validatePlanCreation_Fail_AllDayFalse_InvalidTimeRange() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("시간 순서 오류")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_PLAN_DATE_RANGE);
    }

    @Test
    @DisplayName("유효성 검사 실패 - allDay = true, 시작날짜가 종료날짜보다 늦음")
    void validatePlanCreation_Fail_AllDayTrue_InvalidDateRange() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("날짜 순서 오류")
            .startDateTime(LocalDateTime.of(2025, 8, 5, 1, 0))  // 시간은 무시되고 날짜만 비교
            .endDateTime(LocalDateTime.of(2025, 8, 3, 23, 0))   // 시간은 무시되고 날짜만 비교
            .allDay(true)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_PLAN_DATE_RANGE);
    }

    @Test
    @DisplayName("유효성 검사 실패 - 제목이 빈 문자열")
    void validatePlanCreation_Fail_EmptyTitle() {
        // given
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_EMPTY);
    }

    @Test
    @DisplayName("유효성 검사 실패 - 제목이 200자 초과")
    void validatePlanCreation_Fail_TitleTooLong() {
        // given
        String longTitle = "a".repeat(201); // 201자
        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title(longTitle)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        // when & then
        assertThatThrownBy(() -> planValidator.validatePlanCreation(invalidRequest))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_TOO_LONG);
    }

    // ========== GET 관련 테스트 (validateGetPlan) ==========

    @Test
    @DisplayName("validateGetPlan - 정상 케이스, 예외 발생하지 않음")
    void validateGetPlan_Success() {
        assertThatCode(() -> planValidator.validateGetPlan(1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateGetPlan - planId가 null이면 예외 발생")
    void validateGetPlan_Fail_PlanIdNull() {
        assertThatThrownBy(() -> planValidator.validateGetPlan(null))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_INVALID_PLAN_ID);
    }

    @Test
    @DisplayName("validateGetPlan - planId가 0 이하이면 예외 발생")
    void validateGetPlan_Fail_PlanIdZeroOrLess() {
        assertThatThrownBy(() -> planValidator.validateGetPlan(0L))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_INVALID_PLAN_ID);
    }

    // ========== UPDATE 관련 테스트 (validateUpdatePlan) ==========

    @Test
    @DisplayName("일정 수정 검증 성공 - 정상적인 planId")
    void validateUpdatePlan_Success() {
        // when & then
        assertThatCode(() -> planValidator.validateUpdatePlan(1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("일정 수정 검증 실패 - planId가 null")
    void validateUpdatePlan_Fail_PlanIdNull() {
        // when & then
        assertThatThrownBy(() -> planValidator.validateUpdatePlan(null))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_INVALID_PLAN_ID);
    }

    @Test
    @DisplayName("일정 수정 검증 실패 - planId가 0")
    void validateUpdatePlan_Fail_PlanIdZero() {
        // when & then
        assertThatThrownBy(() -> planValidator.validateUpdatePlan(0L))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_INVALID_PLAN_ID);
    }

    @Test
    @DisplayName("일정 수정 검증 실패 - planId가 음수")
    void validateUpdatePlan_Fail_PlanIdNegative() {
        // when & then
        assertThatThrownBy(() -> planValidator.validateUpdatePlan(-1L))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_INVALID_PLAN_ID);
    }

    // ========== validateDateRange 단독 테스트 ==========

    @Test
    @DisplayName("날짜 범위 검증 성공 - 정상적인 시간 순서")
    void validateDateRange_Success() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when & then
        assertThatCode(() -> planValidator.validateDateRange(startTime, endTime))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("날짜 범위 검증 성공 - 같은 시간")
    void validateDateRange_Success_SameTime() {
        // given
        LocalDateTime sameTime = LocalDateTime.of(2025, 8, 1, 9, 0);

        // when & then
        assertThatCode(() -> planValidator.validateDateRange(sameTime, sameTime))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("날짜 범위 검증 실패 - 시작시간이 종료시간보다 늦음")
    void validateDateRange_Fail_StartAfterEnd() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 8, 1, 9, 0);

        // when & then
        assertThatThrownBy(() -> planValidator.validateDateRange(startTime, endTime))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_PLAN_DATE_RANGE);
    }

    // ========== validateAllDayDateTime 단독 테스트 ==========

    @Test
    @DisplayName("allDay 날짜시간 검증 성공 - allDay=false")
    void validateAllDayDateTime_Success_AllDayFalse() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when & then
        assertThatCode(() -> planValidator.validateAllDayDateTime(false, startTime, endTime))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("allDay 날짜시간 검증 성공 - allDay=true, 같은 날짜")
    void validateAllDayDateTime_Success_AllDayTrue_SameDate() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 1, 15, 30);
        LocalDateTime endTime = LocalDateTime.of(2025, 8, 1, 8, 45);

        // when & then
        assertThatCode(() -> planValidator.validateAllDayDateTime(true, startTime, endTime))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("allDay 날짜시간 검증 실패 - allDay=true, 시작날짜가 종료날짜보다 늦음")
    void validateAllDayDateTime_Fail_AllDayTrue_InvalidDateRange() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 5, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 8, 3, 15, 0);

        // when & then
        assertThatThrownBy(() -> planValidator.validateAllDayDateTime(true, startTime, endTime))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_PLAN_DATE_RANGE);
    }

    // ========== 경계값 테스트 ==========

    @Test
    @DisplayName("제목 검증 성공 - 정확히 200자")
    void validateTitle_Success_ExactlyMaxLength() {
        // given
        String maxLengthTitle = "a".repeat(200);

        // when & then
        assertThatCode(() -> planValidator.validateTitle(maxLengthTitle))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("제목 검증 실패 - 공백만 있는 문자열")
    void validateTitle_Fail_WhitespaceOnly() {
        // when & then
        assertThatThrownBy(() -> planValidator.validateTitle("   "))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_EMPTY);
    }

    @Test
    @DisplayName("제목 검증 실패 - null 제목")
    void validateTitle_Fail_NullTitle() {
        // when & then
        assertThatThrownBy(() -> planValidator.validateTitle(null))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_EMPTY);
    }

    // ========== VALIDATE MONTHLY PLAN REQUEST 테스트 ==========

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 정상적인 년도와 월")
    void validateMonthlyPlanRequest_Success_ValidYearAndMonth() {
        // given
        Integer year = 2024;
        Integer month = 9;

        // when & then - 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month));
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 경계값 (최소 년도)")
    void validateMonthlyPlanRequest_Success_MinimumYear() {
        // given
        Integer year = 2000; // 최소 허용 년도
        Integer month = 1;

        assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month));
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 경계값 (최대 년도)")
    void validateMonthlyPlanRequest_Success_MaximumYear() {
        // given
        Integer year = 3000; // 최대 허용 년도
        Integer month = 12;

        assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month));
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 경계값 (최소 월)")
    void validateMonthlyPlanRequest_Success_MinimumMonth() {
        // given
        Integer year = 2024;
        Integer month = 1; // 최소 허용 월

        assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month));
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 경계값 (최대 월)")
    void validateMonthlyPlanRequest_Success_MaximumMonth() {
        // given
        Integer year = 2024;
        Integer month = 12; // 최대 허용 월

        assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month));
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - null year")
    void validateMonthlyPlanRequest_Fail_NullYear() {
        // given
        Integer year = null;
        Integer month = 9;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_REQUEST);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - null month")
    void validateMonthlyPlanRequest_Fail_NullMonth() {
        // given
        Integer year = 2024;
        Integer month = null;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_REQUEST);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - year와 month 모두 null")
    void validateMonthlyPlanRequest_Fail_BothNull() {
        // given
        Integer year = null;
        Integer month = null;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_REQUEST);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - year 범위 미만 (1999)")
    void validateMonthlyPlanRequest_Fail_YearTooLow() {
        // given
        Integer year = 1999; // 2000 미만
        Integer month = 9;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_YEAR);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - year 범위 초과 (3001)")
    void validateMonthlyPlanRequest_Fail_YearTooHigh() {
        // given
        Integer year = 3001; // 3000 초과
        Integer month = 9;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_YEAR);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - month 범위 미만 (0)")
    void validateMonthlyPlanRequest_Fail_MonthTooLow() {
        // given
        Integer year = 2024;
        Integer month = 0; // 1 미만

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_MONTH);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - month 범위 초과 (13)")
    void validateMonthlyPlanRequest_Fail_MonthTooHigh() {
        // given
        Integer year = 2024;
        Integer month = 13; // 12 초과

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_MONTH);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - 음수 year")
    void validateMonthlyPlanRequest_Fail_NegativeYear() {
        // given
        Integer year = -2024;
        Integer month = 9;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_YEAR);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - 음수 month")
    void validateMonthlyPlanRequest_Fail_NegativeMonth() {
        // given
        Integer year = 2024;
        Integer month = -9;

        // when & then
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_MONTH);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - year와 month 모두 범위 벗어남")
    void validateMonthlyPlanRequest_Fail_BothOutOfRange() {
        // given
        Integer year = 1999; // 범위 벗어남
        Integer month = 13;  // 범위 벗어남

        // when & then - year가 먼저 검사되므로 INVALID_YEAR 예외가 발생해야 함
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_YEAR);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 - 모든 유효한 월에 대해 성공")
    void validateMonthlyPlanRequest_Success_AllValidMonths() {
        // given
        Integer year = 2024;

        // when & then - 1월부터 12월까지 모든 월에 대해 검증
        for (int month = 1; month <= 12; month++) {
            final Integer testMonth = month;
            assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, testMonth), "월 " + month + "에 대해 유효성 검사가 실패했습니다.");
        }
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 - 여러 년도에 대해 성공")
    void validateMonthlyPlanRequest_Success_MultipleYears() {
        // given
        Integer month = 6;
        Integer[] testYears = {2000, 2010, 2020, 2024, 2030, 2050, 2100, 2500, 3000};

        // when & then - 여러 년도에 대해 검증
        for (Integer year : testYears) {
            assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month),
                    "년도 " + year + "에 대해 유효성 검사가 실패했습니다.");
        }
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - 경계값 테스트 (년도)")
    void validateMonthlyPlanRequest_Fail_YearBoundaryTest() {
        // given
        Integer month = 6;

        // when & then - 경계값 바로 밖의 값들 테스트
        Integer[] invalidYears = {1999, 3001};

        for (Integer invalidYear : invalidYears) {
            assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(invalidYear, month))
                    .isInstanceOf(PlanException.class)
                    .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_YEAR);
        }
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 실패 - 경계값 테스트 (월)")
    void validateMonthlyPlanRequest_Fail_MonthBoundaryTest() {
        // given
        Integer year = 2024;

        // when & then - 경계값 바로 밖의 값들 테스트
        Integer[] invalidMonths = {0, 13};

        for (Integer invalidMonth : invalidMonths) {
            assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, invalidMonth))
                    .isInstanceOf(PlanException.class)
                    .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_MONTH);
        }
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 - 유효성 검사 순서 확인")
    void validateMonthlyPlanRequest_ValidationOrder() {
        // given - year와 month 둘 다 null인 경우
        Integer year = null;
        Integer month = null;

        // when & then - INVALID_REQUEST가 먼저 체크되어야 함
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(year, month))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_REQUEST);

        // given - year는 유효하지 않고 month는 null인 경우
        Integer invalidYear = 1999;
        Integer nullMonth = null;

        // when & then - null 체크가 먼저이므로 INVALID_REQUEST가 발생해야 함
        assertThatThrownBy(() -> planValidator.validateMonthlyPlanRequest(invalidYear, nullMonth))
                .isInstanceOf(PlanException.class)
                .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.INVALID_REQUEST);
    }

    @Test
    @DisplayName("월별 일정 요청 유효성 검사 성공 - 실제 사용 시나리오")
    void validateMonthlyPlanRequest_Success_RealWorldScenarios() {
        // given - 현재 년도와 월
        Integer currentYear = java.time.LocalDate.now().getYear();
        Integer currentMonth = java.time.LocalDate.now().getMonthValue();

        // when & then - 현재 날짜로 테스트
        if (currentYear >= 2000 && currentYear <= 3000) {
            assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(currentYear, currentMonth));
        }

        // given - 일반적으로 많이 사용되는 년도들
        Integer[] commonYears = {2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030};
        Integer[] commonMonths = {1, 3, 6, 9, 12}; // 분기별 월

        // when & then - 실제 자주 사용되는 년도/월 조합으로 테스트
        for (Integer year : commonYears) {
            for (Integer month : commonMonths) {
                assertDoesNotThrow(() -> planValidator.validateMonthlyPlanRequest(year, month),
                        "년도 " + year + ", 월 " + month + " 조합에 대해 유효성 검사가 실패했습니다.");
            }
        }
    }
}