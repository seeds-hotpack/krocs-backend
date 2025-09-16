package com.hotpack.krocs.domain.plans.validator;

import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.validator.PlanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
}