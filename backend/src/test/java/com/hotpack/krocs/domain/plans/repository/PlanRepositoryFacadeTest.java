package com.hotpack.krocs.domain.plans.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.plans.domain.Color;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.plans.facade.PlanRepositoryFacade;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlanRepositoryFacadeTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanRepositoryFacade planRepositoryFacade;

    private Plan validPlan;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        validPlan = Plan.builder()
                .planId(1L)
                .title("테스트 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.BLUE)
                .startDateTime(LocalDateTime.of(2024, 9, 15, 9, 0))
                .endDateTime(LocalDateTime.of(2024, 9, 15, 10, 0))
                .allDay(false)
                .isCompleted(false)
                .build();
    }

    // ========== FIND ACTIVE PLANS BY MONTH 테스트 ==========

    @Test
    @DisplayName("월별 일정 조회 성공 - 일정이 있는 경우")
    void findActivePlansByMonth_Success_WithPlans() {
        // given
        int year = 2024;
        int month = 9;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 9, 30, 23, 59, 59, 999999999);

        Plan plan1 = Plan.builder()
                .planId(1L)
                .title("9월 첫째 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.BLUE)
                .startDateTime(LocalDateTime.of(2024, 9, 5, 9, 0))
                .endDateTime(LocalDateTime.of(2024, 9, 5, 10, 0))
                .allDay(false)
                .isCompleted(false)
                .build();

        Plan plan2 = Plan.builder()
                .planId(2L)
                .title("9월 둘째 일정")
                .planCategory(PlanCategory.STUDY)
                .color(Color.GREEN)
                .startDateTime(LocalDateTime.of(2024, 9, 20, 14, 0))
                .endDateTime(LocalDateTime.of(2024, 9, 20, 15, 0))
                .allDay(false)
                .isCompleted(false)
                .build();

        List<Plan> expectedPlans = Arrays.asList(plan1, plan2);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(expectedPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("9월 첫째 일정");
        assertThat(result.get(1).getTitle()).isEqualTo("9월 둘째 일정");

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 일정이 없는 경우")
    void findActivePlansByMonth_Success_NoPlans() {
        // given
        int year = 2024;
        int month = 9;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 9, 30, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 2월 윤년")
    void findActivePlansByMonth_Success_LeapYearFebruary() {
        // given
        int year = 2024; // 윤년
        int month = 2;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 2, 29, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 2월 평년")
    void findActivePlansByMonth_Success_NonLeapYearFebruary() {
        // given
        int year = 2025; // 평년
        int month = 2;

        LocalDateTime startOfMonth = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2025, 2, 28, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 12월 (31일)")
    void findActivePlansByMonth_Success_December() {
        // given
        int year = 2024;
        int month = 12;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 월의 경계에 있는 일정들")
    void findActivePlansByMonth_Success_BoundaryPlans() {
        // given
        int year = 2024;
        int month = 6;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 6, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 6, 30, 23, 59, 59, 999999999);

        // 월의 첫날과 마지막날에 있는 일정들
        Plan firstDayPlan = Plan.builder()
                .planId(1L)
                .title("6월 첫날 일정")
                .startDateTime(LocalDateTime.of(2024, 6, 1, 0, 0))
                .endDateTime(LocalDateTime.of(2024, 6, 1, 1, 0))
                .build();

        Plan lastDayPlan = Plan.builder()
                .planId(2L)
                .title("6월 마지막날 일정")
                .startDateTime(LocalDateTime.of(2024, 6, 30, 23, 0))
                .endDateTime(LocalDateTime.of(2024, 6, 30, 23, 59))
                .build();

        List<Plan> boundaryPlans = Arrays.asList(firstDayPlan, lastDayPlan);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(boundaryPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("6월 첫날 일정");
        assertThat(result.get(1).getTitle()).isEqualTo("6월 마지막날 일정");

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 여러 카테고리의 일정들")
    void findActivePlansByMonth_Success_MultipleCategories() {
        // given
        int year = 2024;
        int month = 8;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 8, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 8, 31, 23, 59, 59, 999999999);

        Plan workPlan = Plan.builder()
                .planId(1L)
                .title("업무 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.BLUE)
                .startDateTime(LocalDateTime.of(2024, 8, 10, 9, 0))
                .endDateTime(LocalDateTime.of(2024, 8, 10, 10, 0))
                .build();

        Plan studyPlan = Plan.builder()
                .planId(2L)
                .title("공부 일정")
                .planCategory(PlanCategory.STUDY)
                .color(Color.GREEN)
                .startDateTime(LocalDateTime.of(2024, 8, 15, 19, 0))
                .endDateTime(LocalDateTime.of(2024, 8, 15, 20, 0))
                .build();

        Plan personalPlan = Plan.builder()
                .planId(3L)
                .title("개인 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.RED)
                .startDateTime(LocalDateTime.of(2024, 8, 20, 14, 0))
                .endDateTime(LocalDateTime.of(2024, 8, 20, 15, 0))
                .build();

        List<Plan> multiCategoryPlans = Arrays.asList(workPlan, studyPlan, personalPlan);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(multiCategoryPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // 각 카테고리별 일정이 모두 포함되어 있는지 확인
        List<PlanCategory> categories = result.stream()
                .map(Plan::getPlanCategory)
                .toList();
        assertThat(categories).contains(PlanCategory.WORK, PlanCategory.STUDY, PlanCategory.WORK);

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 완료된 일정과 미완료 일정 모두 포함")
    void findActivePlansByMonth_Success_CompletedAndIncompleteePlans() {
        // given
        int year = 2024;
        int month = 7;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 7, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 7, 31, 23, 59, 59, 999999999);

        Plan completedPlan = Plan.builder()
                .planId(1L)
                .title("완료된 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.GREEN)
                .startDateTime(LocalDateTime.of(2024, 7, 10, 9, 0))
                .endDateTime(LocalDateTime.of(2024, 7, 10, 10, 0))
                .isCompleted(true)
                .completedAt(LocalDateTime.now())
                .build();

        Plan incompletePlan = Plan.builder()
                .planId(2L)
                .title("미완료 일정")
                .planCategory(PlanCategory.STUDY)
                .color(Color.BLUE)
                .startDateTime(LocalDateTime.of(2024, 7, 15, 14, 0))
                .endDateTime(LocalDateTime.of(2024, 7, 15, 15, 0))
                .isCompleted(false)
                .build();

        List<Plan> mixedPlans = Arrays.asList(completedPlan, incompletePlan);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(mixedPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // 완료 상태 확인
        Plan firstPlan = result.get(0);
        Plan secondPlan = result.get(1);
        assertThat(firstPlan.getIsCompleted()).isTrue();
        assertThat(secondPlan.getIsCompleted()).isFalse();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - allDay 일정들")
    void findActivePlansByMonth_Success_AllDayPlans() {
        // given
        int year = 2024;
        int month = 5;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 5, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 5, 31, 23, 59, 59, 999999999);

        Plan allDayPlan1 = Plan.builder()
                .planId(1L)
                .title("하루 종일 일정 1")
                .planCategory(PlanCategory.WORK)
                .color(Color.PURPLE)
                .startDateTime(LocalDateTime.of(2024, 5, 10, 0, 0))
                .endDateTime(LocalDateTime.of(2024, 5, 10, 23, 59, 59))
                .allDay(true)
                .build();

        Plan allDayPlan2 = Plan.builder()
                .planId(2L)
                .title("하루 종일 일정 2")
                .planCategory(PlanCategory.ETC)
                .color(Color.ORANGE)
                .startDateTime(LocalDateTime.of(2024, 5, 25, 0, 0))
                .endDateTime(LocalDateTime.of(2024, 5, 25, 23, 59, 59))
                .allDay(true)
                .build();

        List<Plan> allDayPlans = Arrays.asList(allDayPlan1, allDayPlan2);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(allDayPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAllDay()).isTrue();
        assertThat(result.get(1).getAllDay()).isTrue();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 여러 날에 걸친 일정")
    void findActivePlansByMonth_Success_MultiDayPlans() {
        // given
        int year = 2024;
        int month = 10;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 10, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 10, 31, 23, 59, 59, 999999999);

        // 10월 내에서 여러 날에 걸친 일정
        Plan multiDayPlan = Plan.builder()
                .planId(1L)
                .title("여러 날 일정")
                .planCategory(PlanCategory.WORK)
                .color(Color.NAVY)
                .startDateTime(LocalDateTime.of(2024, 10, 15, 9, 0))
                .endDateTime(LocalDateTime.of(2024, 10, 18, 17, 0))
                .allDay(false)
                .build();

        List<Plan> multiDayPlans = Arrays.asList(multiDayPlan);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(multiDayPlans);

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("여러 날 일정");
        assertThat(result.get(0).getStartDateTime().toLocalDate().getMonthValue()).isEqualTo(10);
        assertThat(result.get(0).getEndDateTime().toLocalDate().getMonthValue()).isEqualTo(10);

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 경계값 년도/월 테스트")
    void findActivePlansByMonth_Success_BoundaryYearMonth() {
        // given - 최소 년도/월
        int minYear = 2000;
        int minMonth = 1;

        LocalDateTime startOfMonth = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2000, 1, 31, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(minYear, minMonth, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);

        // given - 최대 년도/월
        int maxYear = 3000;
        int maxMonth = 12;

        LocalDateTime maxStartOfMonth = LocalDateTime.of(3000, 12, 1, 0, 0, 0);
        LocalDateTime maxEndOfMonth = LocalDateTime.of(3000, 12, 31, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(maxStartOfMonth), eq(maxEndOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> maxResult = planRepositoryFacade.findActivePlansByMonth(maxYear, maxMonth, userId);

        // then
        assertThat(maxResult).isNotNull();
        assertThat(maxResult).isEmpty();

        verify(planRepository).findPlansByMonthAndStatus(
                maxStartOfMonth, maxEndOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 - Repository에서 예외 발생시 그대로 전파")
    void findActivePlansByMonth_Exception_RepositoryException() {
        // given
        int year = 2024;
        int month = 9;

        LocalDateTime startOfMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2024, 9, 30, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(startOfMonth), eq(endOfMonth), eq(userId), eq(Status.ACTIVE)))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        assertThatThrownBy(() -> planRepositoryFacade.findActivePlansByMonth(year, month, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(planRepository).findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE);
    }

    @Test
    @DisplayName("월별 일정 조회 성공 - 시간 계산 정확성 검증")
    void findActivePlansByMonth_Success_TimeCalculationAccuracy() {
        // given
        int year = 2024;
        int month = 4; // 4월 (30일)

        // 정확한 시간 계산 검증을 위한 테스트
        LocalDateTime expectedStart = LocalDateTime.of(2024, 4, 1, 0, 0, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2024, 4, 30, 23, 59, 59, 999999999);

        when(planRepository.findPlansByMonthAndStatus(
                eq(expectedStart), eq(expectedEnd), eq(userId), eq(Status.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // when
        List<Plan> result = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 정확한 시작일과 종료일로 호출되었는지 검증
        verify(planRepository).findPlansByMonthAndStatus(
                expectedStart, expectedEnd, userId, Status.ACTIVE);
    }
}
