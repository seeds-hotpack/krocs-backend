package com.hotpack.krocs.domain.plans.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.plans.converter.PlanConverter;
import com.hotpack.krocs.domain.plans.domain.Color;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.facade.PlanRepositoryFacade;
import com.hotpack.krocs.domain.plans.validator.PlanValidator;
import com.hotpack.krocs.domain.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

    @Mock
    private PlanRepositoryFacade planRepositoryFacade;
    @Mock
    private PlanConverter planConverter;
    @Mock
    private SubGoalRepositoryFacade subGoalRepositoryFacade;
    @Mock
    private PlanValidator planValidator;

    @InjectMocks
    private PlanServiceImpl planService;

    // 테스트 데이터
    private PlanCreateRequestDTO validRequestDTO;
    private Plan validPlan;
    private Goal validGoal;
    private SubGoal validSubGoal;
    private PlanResponseDTO validResponseDTO;
    private List<Plan> validPlanList;
    private List<PlanResponseDTO> validPlanResponseList;
    private PlanListResponseDTO validPlanListResponseDTO;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validGoal = Goal.builder()
            .goalId(1L)
            .title("테스트 목표")
            .build();

        validSubGoal = SubGoal.builder()
            .subGoalId(1L)
            .goal(validGoal)
            .title("테스트 서브목표")
            .isCompleted(false)
            .build();

        validRequestDTO = PlanCreateRequestDTO.builder()
            .title("테스트 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        validPlan = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .title("테스트 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        validResponseDTO = PlanResponseDTO.builder()
            .planId(1L)
            .goalId(1L)
            .subGoalId(1L)
            .title("테스트 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        Plan plan2 = Plan.builder()
            .planId(2L)
            .goal(null)
            .subGoal(null)
            .title("독립 일정")
            .planCategory(PlanCategory.STUDY)
            .color(Color.GREEN)
            .startDateTime(LocalDateTime.of(2025, 8, 2, 14, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 2, 15, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        validPlanList = Arrays.asList(validPlan, plan2);

        PlanResponseDTO responseDTO2 = PlanResponseDTO.builder()
            .planId(2L)
            .goalId(null)
            .subGoalId(null)
            .title("독립 일정")
            .planCategory(PlanCategory.STUDY)
            .color(Color.GREEN)
            .startDateTime(LocalDateTime.of(2025, 8, 2, 14, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 2, 15, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        validPlanResponseList = Arrays.asList(validResponseDTO, responseDTO2);

        validPlanListResponseDTO = PlanListResponseDTO.builder()
            .plans(validPlanResponseList)
            .build();
    }

    // ========== CREATE 테스트 ==========

    @Test
    @DisplayName("일정 생성 성공")
    void createPlan_Success() {
        // given
        Long userId = 1L;
        Long subGoalId = 10L;

        doNothing().when(planValidator).validatePlanCreation(validRequestDTO, subGoalId);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toEntity(eq(validRequestDTO), eq(validGoal), eq(validSubGoal),
            any(User.class))).thenReturn(
            validPlan);
        when(planRepositoryFacade.savePlan(validPlan)).thenReturn(validPlan);
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.createPlan(validRequestDTO, userId, subGoalId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.WORK);
        assertThat(result.getColor()).isEqualTo(Color.BLUE);
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getSubGoalId()).isEqualTo(1L); // SubGoalId 검증도 추가
    }

    @Test
    @DisplayName("일정 생성 실패 - SubGoal이 존재하지 않음")
    void createPlan_Fail_SubGoalNotFound() {
        // given
        Long userId = 1L;
        Long subGoalId = 999L; // goalId → subGoalId 변경

        doNothing().when(planValidator).validatePlanCreation(validRequestDTO, subGoalId);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            null); // Mock 변경

        // when & then
        assertThatThrownBy(
            () -> planService.createPlan(validRequestDTO, userId, subGoalId)) // 파라미터 변경
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_SUB_GOAL_NOT_FOUND); // 예외 타입 변경
    }

    @Test
    @DisplayName("일정 생성 실패 - 유효성 검사 실패")
    void createPlan_Fail_ValidationError() {
        // given
        Long userId = 1L;
        Long subGoalId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_TITLE_EMPTY))
            .when(planValidator).validatePlanCreation(validRequestDTO, subGoalId);

        // when & then
        assertThatThrownBy(() -> planService.createPlan(validRequestDTO, userId, subGoalId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_EMPTY);
    }

    @Test
    @DisplayName("일정 생성 성공 - allDay = true")
    void createPlan_Success_AllDay() {
        // given
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanCreateRequestDTO allDayRequest = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.RED)
            .allDay(true)
            .build();

        Plan allDayPlan = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .planCategory(PlanCategory.WORK)
            .color(Color.RED)
            .title("하루 종일 일정")
            .allDay(true)
            .isCompleted(false)
            .build();

        PlanResponseDTO allDayResponse = PlanResponseDTO.builder()
            .planId(1L)
            .goalId(1L)
            .subGoalId(1L)
            .planCategory(PlanCategory.WORK)
            .color(Color.RED)
            .title("하루 종일 일정")
            .allDay(true)
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validatePlanCreation(allDayRequest, subGoalId);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toEntity(eq(allDayRequest), eq(validGoal), eq(validSubGoal),
            any(User.class))).thenReturn(allDayPlan);
//        when(planConverter.toEntity(allDayRequest, validGoal, validSubGoal)).thenReturn(allDayPlan);
        when(planRepositoryFacade.savePlan(allDayPlan)).thenReturn(allDayPlan);
        when(planConverter.toEntity(allDayPlan)).thenReturn(allDayResponse);
//        when(goalConverter.toEntity(eq(minimalRequest), any(User.class))).thenReturn(
//            validGoal);

        // when
        PlanResponseDTO result = planService.createPlan(allDayRequest, userId, subGoalId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("하루 종일 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.WORK);
        assertThat(result.getColor()).isEqualTo(Color.RED);
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getStartDateTime()).isNull();  // allDay = true면 시간은 null
        assertThat(result.getEndDateTime()).isNull();
    }

    @Test
    @DisplayName("일정 생성 실패 - allDay = false인데 시간 누락")
    void createPlan_Fail_AllDayFalse_NoDateTime() {
        // given
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanCreateRequestDTO invalidRequest = PlanCreateRequestDTO.builder()
            .title("시간 지정 일정")
            .allDay(false)
            // startDateTime, endDateTime 누락
            .build();

        doThrow(new PlanException(PlanExceptionType.PLAN_START_TIME_REQUIRED))
            .when(planValidator).validatePlanCreation(invalidRequest, subGoalId);

        // when & then
        assertThatThrownBy(() -> planService.createPlan(invalidRequest, userId, subGoalId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_START_TIME_REQUIRED);

        verify(planValidator).validatePlanCreation(invalidRequest, subGoalId);
        verify(subGoalRepositoryFacade, never()).findActiveSubGoalBySubGoalId(any());
        verify(subGoalRepositoryFacade, never()).findActiveGoalBySubGoalId(any());
        verify(planConverter, never()).toEntity(any(), any(), any());
    }

    @Test
    @DisplayName("일정 생성 실패 - 시간 순서 오류")
    void createPlan_Fail_InvalidTimeOrder() {
        // given
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanCreateRequestDTO invalidTimeRequest = PlanCreateRequestDTO.builder()
            .title("시간 순서 오류")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))  // 종료시간보다 늦음
            .endDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .allDay(false)
            .build();

        doThrow(new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE))
            .when(planValidator).validatePlanCreation(invalidTimeRequest, subGoalId);

        // when & then
        assertThatThrownBy(() -> planService.createPlan(invalidTimeRequest, userId, subGoalId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.INVALID_PLAN_DATE_RANGE);
    }

    @Test
    @DisplayName("일정 생성 실패 - Repository에서 예외 발생")
    void createPlan_Fail_RepositoryException() {
        // given
        Long userId = 1L;
        Long subGoalId = 1L;

        doNothing().when(planValidator).validatePlanCreation(validRequestDTO, subGoalId);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toEntity(eq(validRequestDTO), eq(validGoal), eq(validSubGoal),
            any(User.class))).thenReturn(
            validPlan);
        when(planRepositoryFacade.savePlan(validPlan)).thenThrow(new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> planService.createPlan(validRequestDTO, userId, subGoalId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_CREATION_FAILED);
    }

    // ========== GET 테스트 ==========

    @Test
    @DisplayName("모든 일정 조회 성공")
    void getAllPlans_Success() {
        // given
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 8, 1);

        when(planRepositoryFacade.findActivePlansByDateRange(any(LocalDateTime.class),
            any(LocalDateTime.class), eq(userId))).thenReturn(validPlanList);
        when(planConverter.toListPlanResponseDTO(validPlanList)).thenReturn(validPlanResponseList);

        // when
        PlanListResponseDTO result = planService.getPlans(date, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).isNotNull();
        assertThat(result.getPlans()).hasSize(2);

        // 첫 번째 일정 (Goal/SubGoal 연결된 일정)
        PlanResponseDTO firstPlan = result.getPlans().getFirst();
        assertThat(firstPlan.getPlanId()).isEqualTo(1L);
        assertThat(firstPlan.getTitle()).isEqualTo("테스트 일정");
        assertThat(firstPlan.getColor()).isEqualTo(Color.BLUE);
        assertThat(firstPlan.getGoalId()).isEqualTo(1L);
        assertThat(firstPlan.getSubGoalId()).isEqualTo(1L);
        assertThat(firstPlan.getIsCompleted()).isFalse();

        // 두 번째 일정 (독립 일정)
        PlanResponseDTO secondPlan = result.getPlans().get(1);
        assertThat(secondPlan.getPlanId()).isEqualTo(2L);
        assertThat(secondPlan.getTitle()).isEqualTo("독립 일정");
        assertThat(secondPlan.getColor()).isEqualTo(Color.GREEN);
        assertThat(secondPlan.getGoalId()).isNull();
        assertThat(secondPlan.getSubGoalId()).isNull();
        assertThat(secondPlan.getIsCompleted()).isFalse();
    }

    @Test
    @DisplayName("모든 일정 조회 성공 - 빈 리스트")
    void getAllPlans_Success_EmptyList() {
        // given
        Long userId = 1L;
        List<Plan> emptyPlanList = Collections.emptyList();
        List<PlanResponseDTO> emptyResponseList = Collections.emptyList();
        LocalDate date = LocalDate.of(2025, 8, 1);

        when(planRepositoryFacade.findActivePlansByDateRange(any(LocalDateTime.class),
            any(LocalDateTime.class), eq(userId)))
            .thenReturn(emptyPlanList);
        when(planConverter.toListPlanResponseDTO(emptyPlanList)).thenReturn(emptyResponseList);

        // when
        PlanListResponseDTO result = planService.getPlans(date, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).isNotNull();
        assertThat(result.getPlans()).isEmpty();
    }

    @Test
    @DisplayName("모든 일정 조회 실패 - Repository에서 예외 발생")
    void getAllPlans_Fail_RepositoryException() {

        // given
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 8, 1);

        when(planRepositoryFacade.findActivePlansByDateRange(any(LocalDateTime.class),
            any(LocalDateTime.class), eq(userId))).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> planService.getPlans(date, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_FOUND_FAILED);
        verify(planRepositoryFacade).findActivePlansByDateRange(any(LocalDateTime.class),
            any(LocalDateTime.class), eq(userId));
        verify(planConverter, never()).toListPlanResponseDTO(any());
    }

    @Test
    @DisplayName("모든 일정 조회 실패 - Converter에서 예외 발생")
    void getAllPlans_Fail_ConverterException() {
        // given
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 8, 1);

        when(planRepositoryFacade.findActivePlansByDateRange(any(LocalDateTime.class),
            any(LocalDateTime.class), eq(userId)))
            .thenReturn(validPlanList);
        when(planConverter.toListPlanResponseDTO(validPlanList)).thenThrow(
            new RuntimeException("변환 오류"));

        // when & then
        assertThatThrownBy(() -> planService.getPlans(date, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_FOUND_FAILED);
    }

    @Test
    @DisplayName("특정 일정 조회 성공 - Goal/SubGoal 연결된 일정")
    void getPlanById_Success_WithGoalAndSubGoal() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateGetPlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.getPlanById(planId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.WORK);
        assertThat(result.getColor()).isEqualTo(Color.BLUE);
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getSubGoalId()).isEqualTo(1L);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isFalse();
        verify(planValidator).validateGetPlan(planId);
    }

    @Test
    @DisplayName("특정 일정 조회 성공 - 독립 일정 (Goal/SubGoal 없음)")
    void getPlanById_Success_IndependentPlan() {
        // given
        Long planId = 2L;
        Long userId = 1L;

        Plan independentPlan = Plan.builder()
            .planId(2L)
            .goal(null)
            .subGoal(null)
            .title("독립 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.NAVY)
            .allDay(true)
            .isCompleted(false)
            .build();

        PlanResponseDTO independentResponse = PlanResponseDTO.builder()
            .planId(2L)
            .goalId(null)
            .subGoalId(null)
            .title("독립 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.NAVY)
            .allDay(true)
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validateGetPlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(independentPlan);
        when(planConverter.toEntity(independentPlan)).thenReturn(independentResponse);

        // when
        PlanResponseDTO result = planService.getPlanById(planId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("독립 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.ETC);
        assertThat(result.getColor()).isEqualTo(Color.NAVY);
        assertThat(result.getGoalId()).isNull();
        assertThat(result.getSubGoalId()).isNull();
        assertThat(result.getAllDay()).isTrue();
        verify(planValidator).validateGetPlan(planId);
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - 존재하지 않는 planId")
    void getPlanById_Fail_PlanNotFound() {
        // given
        Long planId = 999L;
        Long userId = 1L;

        doNothing().when(planValidator).validateGetPlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_NOT_FOUND);

        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planConverter, never()).toEntity(any(Plan.class));
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - Repository에서 예외 발생")
    void getPlanById_Fail_RepositoryException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateGetPlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_FOUND_FAILED);
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - Converter에서 예외 발생")
    void getPlanById_Fail_ConverterException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateGetPlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(planConverter.toEntity(validPlan)).thenThrow(new RuntimeException("변환 오류"));

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_FOUND_FAILED);
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - null planId")
    void getPlanById_Fail_NullPlanId() {
        // given
        Long planId = null;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateGetPlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateGetPlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planConverter, never()).toEntity(any(Plan.class));
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - 음수 planId")
    void getPlanById_Fail_NegativePlanId() {
        // given
        Long planId = -1L;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateGetPlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateGetPlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planConverter, never()).toEntity(any(Plan.class));
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - 0인 planId")
    void getPlanById_Fail_ZeroPlanId() {
        // given
        Long planId = 0L;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateGetPlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.getPlanById(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateGetPlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planConverter, never()).toEntity(any(Plan.class));
    }

    // ========== UPDATE 테스트 ==========

    @Test
    @DisplayName("일정 수정 성공 - 제목만 수정")
    void updatePlanById_Success_TitleOnly() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doNothing().when(planValidator).validateTitle("수정된 제목");
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class)); // 아무 DTO나 반환
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(validResponseDTO);

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateTitle("수정된 제목");
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            validPlan.getAllDay(),
            validPlan.getStartDateTime(),
            validPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - 카테고리만 수정")
    void updatePlanById_Success_CategoryOnly() {

        PlanResponseDTO expectedUpdateResponse = PlanResponseDTO.builder()
            .planId(1L)
            .goalId(1L)
            .subGoalId(1L)
            .planCategory(PlanCategory.STUDY)
            .color(validPlan.getColor())
            .title("테스트 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;
        PlanCategory newCategory = PlanCategory.STUDY; // 기존 WORK에서 STUDY로 변경

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .planCategory(newCategory)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        when(planConverter.toEntity(validPlan)).thenReturn(expectedUpdateResponse);
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanCategory()).isEqualTo(newCategory);
        assertThat(result.getTitle()).isEqualTo(validPlan.getTitle());
        assertThat(result.getColor()).isEqualTo(validPlan.getColor());
        assertThat(result.getStartDateTime()).isEqualTo(validPlan.getStartDateTime());

        verify(planValidator).validateUpdatePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);

        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            validPlan.getAllDay(),
            validPlan.getStartDateTime(),
            validPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - 색상만 수정")
    void updatePlanById_Success_ColorOnly() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Color newColor = Color.ORANGE; // 새로운 색상

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .color(newColor)
            .build();

        PlanResponseDTO expectedResponse = PlanResponseDTO.builder()
            .planId(validPlan.getPlanId())
            .goalId(validPlan.getGoal().getGoalId())
            .subGoalId(validPlan.getSubGoal().getSubGoalId())
            .title(validPlan.getTitle())
            .planCategory(validPlan.getPlanCategory())
            .color(newColor) // 색상만 새로운 값으로 변경
            .startDateTime(validPlan.getStartDateTime())
            .endDateTime(validPlan.getEndDateTime())
            .allDay(validPlan.getAllDay())
            .isCompleted(validPlan.getIsCompleted())
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any())).thenReturn(
            updateRequest);
        when(planConverter.toEntity(validPlan)).thenReturn(expectedResponse);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, null, updateRequest, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getColor()).isEqualTo(newColor); // 색상이 잘 바뀌었는지 확인
        assertThat(result.getTitle()).isEqualTo(validPlan.getTitle()); // 다른 값은 그대로인지 확인
    }

    @Test
    @DisplayName("일정 수정 성공 - 시간만 수정")
    void updatePlanById_Success_DateTimeOnly() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        LocalDateTime newStartTime = LocalDateTime.of(2025, 8, 2, 14, 0);
        LocalDateTime newEndTime = LocalDateTime.of(2025, 8, 2, 16, 0);

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .startDateTime(newStartTime)
            .endDateTime(newEndTime)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doNothing().when(planValidator).validateDateRange(newStartTime, newEndTime);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateDateRange(newStartTime, newEndTime);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            validPlan.getAllDay(),
            newStartTime,
            newEndTime
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - allDay를 true로 변경 (시간 정규화)")
    void updatePlanById_Success_ChangeToAllDay() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        LocalDateTime normalizedStartTime = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime normalizedEndTime = LocalDateTime.of(2025, 8, 1, 23, 59, 59);

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .allDay(true)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doNothing().when(planValidator)
            .validateAllDayDateTime(true, normalizedStartTime, normalizedEndTime);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateAllDayDateTime(true, normalizedStartTime, normalizedEndTime);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            true,
            normalizedStartTime,
            normalizedEndTime
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - allDay를 false로 변경")
    void updatePlanById_Success_ChangeToNonAllDay() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        Plan allDayPlan = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .planCategory(PlanCategory.WORK)
            .title("하루 종일 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 23, 59, 59))
            .allDay(true)
            .isCompleted(false)
            .build();

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .allDay(false)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(allDayPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doNothing().when(planValidator).validateAllDayDateTime(false, allDayPlan.getStartDateTime(),
            allDayPlan.getEndDateTime());
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(allDayPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateAllDayDateTime(false, allDayPlan.getStartDateTime(),
            allDayPlan.getEndDateTime());
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            false,
            allDayPlan.getStartDateTime(),
            allDayPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - 완료 상태로 변경 (completedAt 설정)")
    void updatePlanById_Success_ChangeToCompleted() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .isCompleted(true)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            validPlan.getAllDay(),
            validPlan.getStartDateTime(),
            validPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - 완료 상태 해제 (completedAt null)")
    void updatePlanById_Success_ChangeToNotCompleted() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        Plan completedPlan = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .planCategory(PlanCategory.WORK)
            .title("완료된 일정")
            .startDateTime(validPlan.getStartDateTime())
            .endDateTime(validPlan.getEndDateTime())
            .allDay(false)
            .isCompleted(true)
            .completedAt(LocalDateTime.now())
            .build();

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(completedPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(completedPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            completedPlan.getAllDay(),
            completedPlan.getStartDateTime(),
            completedPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - 모든 필드 동시 수정")
    void updatePlanById_Success_AllFieldsUpdate() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        LocalDateTime newStartTime = LocalDateTime.of(2025, 8, 3, 10, 0);
        LocalDateTime newEndTime = LocalDateTime.of(2025, 8, 3, 12, 0);
        LocalDateTime normalizedStartTime = LocalDateTime.of(2025, 8, 3, 0, 0);
        LocalDateTime normalizedEndTime = LocalDateTime.of(2025, 8, 3, 23, 59, 59);

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("완전히 새로운 제목")
            .startDateTime(newStartTime)
            .endDateTime(newEndTime)
            .planCategory(PlanCategory.STUDY)
            .allDay(true)
            .isCompleted(true)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        doNothing().when(planValidator).validateTitle("완전히 새로운 제목");
        doNothing().when(planValidator).validateDateRange(newStartTime, newEndTime);
        doNothing().when(planValidator)
            .validateAllDayDateTime(true, normalizedStartTime, normalizedEndTime);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateTitle("완전히 새로운 제목");
        verify(planValidator).validateDateRange(newStartTime, newEndTime);
        verify(planValidator).validateAllDayDateTime(true, normalizedStartTime, normalizedEndTime);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            true,
            normalizedStartTime,
            normalizedEndTime
        );
    }

    @Test
    @DisplayName("일정 수정 실패 - 존재하지 않는 planId")
    void updatePlanById_Fail_PlanNotFound() {
        // given
        Long planId = 999L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(null);

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_NOT_FOUND);

        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planValidator, never()).validateTitle(any());
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    @Test
    @DisplayName("일정 수정 실패 - 제목 유효성 검사 실패")
    void updatePlanById_Fail_InvalidTitle() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("") // 빈 제목
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doThrow(new PlanException(PlanExceptionType.PLAN_TITLE_EMPTY))
            .when(planValidator).validateTitle("");

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_TITLE_EMPTY);

        verify(planValidator).validateTitle("");
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    @Test
    @DisplayName("일정 수정 실패 - 날짜 범위 유효성 검사 실패")
    void updatePlanById_Fail_InvalidDateRange() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        LocalDateTime invalidStartTime = LocalDateTime.of(2025, 8, 1, 15, 0);
        LocalDateTime invalidEndTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .startDateTime(invalidStartTime)
            .endDateTime(invalidEndTime)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doThrow(new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE))
            .when(planValidator).validateDateRange(invalidStartTime, invalidEndTime);

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.INVALID_PLAN_DATE_RANGE);

        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateDateRange(invalidStartTime, invalidEndTime);
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    @Test
    @DisplayName("일정 수정 실패 - allDay 유효성 검사 실패")
    void updatePlanById_Fail_AllDayValidationError() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .allDay(true)
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doThrow(new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE))
            .when(planValidator).validateAllDayDateTime(any(), any(), any());

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.INVALID_PLAN_DATE_RANGE);

        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(subGoalId);
        verify(planValidator).validateAllDayDateTime(any(), any(), any());
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    @Test
    @DisplayName("일정 수정 실패 - Repository에서 예외 발생")
    void updatePlanById_Fail_RepositoryException() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_UPDATE_FAILED);

        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    @Test
    @DisplayName("일정 수정 실패 - Converter에서 예외 발생")
    void updatePlanById_Fail_ConverterException() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        doNothing().when(planValidator).validateTitle("수정된 제목");
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("변환 오류"));

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, subGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_UPDATE_FAILED);
    }

    @Test
    @DisplayName("일정 수정 성공 - 빈 request (아무것도 변경하지 않음)")
    void updatePlanById_Success_EmptyRequest() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long subGoalId = 1L;

        PlanUpdateRequestDTO emptyRequest = PlanUpdateRequestDTO.builder().build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId)).thenReturn(
            validSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, subGoalId, emptyRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(planValidator, never()).validateTitle(any());
        verify(planValidator, never()).validateDateRange(any(), any());
        verify(planValidator, never()).validateAllDayDateTime(any(), any(), any());
        verify(planConverter).toUpdatePlanRequestDTO(
            emptyRequest,
            validPlan.getAllDay(),
            validPlan.getStartDateTime(),
            validPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 성공 - subGoalId만 변경")
    void updatePlanById_Success_SubGoalIdOnly() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long newSubGoalId = 2L;

        // 새로운 SubGoal과 Goal 생성
        SubGoal newSubGoal = SubGoal.builder()
            .subGoalId(2L)
            .goal(validGoal)
            .title("새로운 서브목표")
            .isCompleted(false)
            .build();

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .build(); // 빈 요청 (subGoalId만 변경)

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(newSubGoalId)).thenReturn(
            newSubGoal);
        when(subGoalRepositoryFacade.findActiveGoalBySubGoalId(newSubGoalId)).thenReturn(validGoal);
        when(planConverter.toUpdatePlanRequestDTO(any(), any(), any(), any()))
            .thenReturn(mock(PlanUpdateRequestDTO.class));
        when(planConverter.toEntity(validPlan)).thenReturn(validResponseDTO);

        // when
        PlanResponseDTO result = planService.updatePlanById(planId, newSubGoalId, updateRequest,
            userId);

        // then
        assertThat(result).isNotNull();

        verify(planValidator).validateUpdatePlan(planId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(newSubGoalId);
        verify(subGoalRepositoryFacade).findActiveGoalBySubGoalId(newSubGoalId);
        verify(planConverter).toUpdatePlanRequestDTO(
            updateRequest,
            validPlan.getAllDay(),
            validPlan.getStartDateTime(),
            validPlan.getEndDateTime()
        );
    }

    @Test
    @DisplayName("일정 수정 실패 - 존재하지 않는 subGoalId")
    void updatePlanById_Fail_SubGoalNotFound() {
        // given
        Long planId = 1L;
        Long userId = 1L;
        Long invalidSubGoalId = 999L;

        PlanUpdateRequestDTO updateRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        doNothing().when(planValidator).validateUpdatePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(invalidSubGoalId)).thenReturn(
            null);

        // when & then
        assertThatThrownBy(
            () -> planService.updatePlanById(planId, invalidSubGoalId, updateRequest, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_SUB_GOAL_NOT_FOUND);

        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(invalidSubGoalId);
        verify(subGoalRepositoryFacade, never()).findActiveGoalBySubGoalId(any());
        verify(planConverter, never()).toUpdatePlanRequestDTO(any(), any(), any(), any());
    }

    // ========== DELETE 테스트 ==========

    @Test
    @DisplayName("일정 삭제 성공")
    void deletePlan_Success() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 성공 - Goal/SubGoal과 연결된 일정")
    void deletePlan_Success_WithGoalAndSubGoal() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        Plan planWithRelations = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .title("목표와 연결된 일정")
            .planCategory(PlanCategory.STUDY)
            .color(Color.PINK)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(planWithRelations);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 성공 - 독립 일정 (Goal/SubGoal 없음)")
    void deletePlan_Success_IndependentPlan() {
        // given
        Long planId = 2L;
        Long userId = 1L;

        Plan independentPlan = Plan.builder()
            .planId(2L)
            .goal(null)
            .subGoal(null)
            .title("독립 일정")
            .planCategory(PlanCategory.STUDY)
            .color(Color.PINK)
            .startDateTime(LocalDateTime.of(2025, 8, 2, 14, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 2, 15, 0))
            .allDay(false)
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(independentPlan);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 성공 - 완료된 일정")
    void deletePlan_Success_CompletedPlan() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        Plan completedPlan = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .title("완료된 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.PINK)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(true)
            .completedAt(LocalDateTime.now())
            .build();

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(completedPlan);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 성공 - allDay 일정")
    void deletePlan_Success_AllDayPlan() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        Plan allDayPlan = Plan.builder()
            .planId(1L)
            .goal(null)
            .subGoal(null)
            .title("하루 종일 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.PINK)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 23, 59, 59))
            .allDay(true)
            .isCompleted(false)
            .build();

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(allDayPlan);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 실패 - 존재하지 않는 planId")
    void deletePlan_Fail_PlanNotFound() {
        // given
        Long planId = 999L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_NOT_FOUND);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - null planId")
    void deletePlan_Fail_NullPlanId() {
        // given
        Long planId = null;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateDeletePlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - 음수 planId")
    void deletePlan_Fail_NegativePlanId() {
        // given
        Long planId = -1L;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateDeletePlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - 0인 planId")
    void deletePlan_Fail_ZeroPlanId() {
        // given
        Long planId = 0L;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateDeletePlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - Repository findById에서 예외 발생")
    void deletePlan_Fail_FindByIdRepositoryException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenThrow(
            new RuntimeException("데이터베이스 조회 오류"));

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_DELETE_FAILED);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - Repository delete에서 예외 발생")
    void deletePlan_Fail_DeleteRepositoryException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        doThrow(new RuntimeException("데이터베이스 삭제 오류"))
            .when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_DELETE_FAILED);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 실패 - Validator에서 예외 발생")
    void deletePlan_Fail_ValidatorException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doThrow(new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID))
            .when(planValidator).validateDeletePlan(planId);

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType",
                PlanExceptionType.PLAN_INVALID_PLAN_ID);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade, never()).findActivePlanById(any());
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - SubPlan이 연결된 일정 (Cascade 삭제 동작 확인)")
    void deletePlan_Success_WithSubPlans() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        // SubPlan 리스트가 있는 Plan 생성 (실제로는 JPA Cascade가 처리)
        Plan planWithSubPlans = Plan.builder()
            .planId(1L)
            .goal(validGoal)
            .subGoal(validSubGoal)
            .title("SubPlan이 있는 일정")
            .planCategory(PlanCategory.WORK)
            .color(Color.PINK)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .subPlans(Collections.emptyList()) // 실제로는 SubPlan들이 들어있겠지만 테스트에서는 빈 리스트
            .build();

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(planWithSubPlans);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then
        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 로직 검증 - 메서드 호출 순서 확인")
    void deletePlan_Success_MethodCallOrder() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId)).thenReturn(validPlan);
        doNothing().when(planRepositoryFacade).deleteActivePlanByPlanId(planId);

        // when
        planService.deletePlan(planId, userId);

        // then - 메서드 호출 순서 검증
        InOrder inOrder = inOrder(planValidator, planRepositoryFacade);
        inOrder.verify(planValidator).validateDeletePlan(planId);
        inOrder.verify(planRepositoryFacade).findActivePlanById(planId);
        inOrder.verify(planRepositoryFacade).deleteActivePlanByPlanId(planId);
    }

    @Test
    @DisplayName("일정 삭제 실패 - PlanException이 아닌 다른 Exception 발생 시 PlanException으로 래핑")
    void deletePlan_Fail_UnexpectedException() {
        // given
        Long planId = 1L;
        Long userId = 1L;

        doNothing().when(planValidator).validateDeletePlan(planId);
        when(planRepositoryFacade.findActivePlanById(planId))
            .thenThrow(new IllegalArgumentException("예상치 못한 오류"));

        // when & then
        assertThatThrownBy(() -> planService.deletePlan(planId, userId))
            .isInstanceOf(PlanException.class)
            .hasFieldOrPropertyWithValue("planExceptionType", PlanExceptionType.PLAN_DELETE_FAILED);

        verify(planValidator).validateDeletePlan(planId);
        verify(planRepositoryFacade).findActivePlanById(planId);
        verify(planRepositoryFacade, never()).deleteActivePlanByPlanId(any());
    }
}