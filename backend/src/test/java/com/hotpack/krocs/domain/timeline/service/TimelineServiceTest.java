package com.hotpack.krocs.domain.timeline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.service.SubGoalService;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.plans.service.PlanService;
import com.hotpack.krocs.domain.timeline.converter.TimelineConverter;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineItemDTO;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;
import com.hotpack.krocs.global.common.entity.Color;
import java.time.LocalDate;
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
class TimelineServiceTest {

    @Mock
    private PlanService planService;

    @Mock
    private SubGoalService subGoalService;

    @Mock
    private TimelineConverter timelineConverter;

    @InjectMocks
    private TimelineServiceImpl timelineService;

    private PlanResponseDTO planResponseDTO;
    private SubGoalResponseDTO subGoalResponseDTO;
    private TimelineItemDTO planItemDTO;
    private TimelineItemDTO subGoalItemDTO;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2025, 10, 1);
        endDate = LocalDate.of(2025, 10, 31);
        userId = 1L;

        planResponseDTO = PlanResponseDTO.builder()
                .planId(1L)
                .title("회의 참석")
                .color(Color.PLAN_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 5, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 5, 15, 0))
                .allDay(false)
                .planCategory(PlanCategory.WORK)
                .build();

        subGoalResponseDTO = SubGoalResponseDTO.builder()
                .subGoalId(1L)
                .title("영어 공부")
                .color(Color.GOAL_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 10, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 10, 12, 0))
                .build();

        planItemDTO = TimelineItemDTO.builder()
                .id(1L)
                .title("회의 참석")
                .color(Color.PLAN_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 5, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 5, 15, 0))
                .allDay(false)
                .planCategory(PlanCategory.WORK)
                .build();

        subGoalItemDTO = TimelineItemDTO.builder()
                .id(1L)
                .title("영어 공부")
                .color(Color.GOAL_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 10, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 10, 12, 0))
                .build();
    }

    // ========== getTimeline - 두 타입 모두 요청 ==========

    @Test
    @DisplayName("plan과 subgoal 타입 모두 요청 시 성공")
    void getTimeline_BothTypes_Success() {
        // given
        List<String> types = Arrays.asList("plan", "subgoal");
        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO);
        List<SubGoalResponseDTO> subGoals = Arrays.asList(subGoalResponseDTO);
        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO);
        List<TimelineItemDTO> subGoalItems = Arrays.asList(subGoalItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(subGoalItems)
                .totalCount(2)
                .planCount(1)
                .subgoalCount(1)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, userId)).thenReturn(plans);
        when(subGoalService.getSubGoalsInDateRange(startDate, endDate, userId)).thenReturn(subGoals);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertSubGoalToTimelineItem(subGoalResponseDTO)).thenReturn(subGoalItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(planItems, subGoalItems)).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getPlanCount()).isEqualTo(1);
        assertThat(result.getSubgoalCount()).isEqualTo(1);
        assertThat(result.getPlans()).hasSize(1);
        assertThat(result.getSubgoals()).hasSize(1);

        verify(planService, times(1)).getPlansInDateRange(startDate, endDate, userId);
        verify(subGoalService, times(1)).getSubGoalsInDateRange(startDate, endDate, userId);
    }

    // ========== getTimeline - plan만 요청 ==========

    @Test
    @DisplayName("plan 타입만 요청 시 성공")
    void getTimeline_PlanOnly_Success() {
        // given
        List<String> types = Arrays.asList("plan");
        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO);
        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(Collections.emptyList())
                .totalCount(1)
                .planCount(1)
                .subgoalCount(0)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, userId)).thenReturn(plans);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(eq(planItems), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getPlanCount()).isEqualTo(1);
        assertThat(result.getSubgoalCount()).isEqualTo(0);
        assertThat(result.getPlans()).hasSize(1);
        assertThat(result.getSubgoals()).isEmpty();

        verify(planService, times(1)).getPlansInDateRange(startDate, endDate, userId);
        verify(subGoalService, times(0)).getSubGoalsInDateRange(any(), any(), any());
    }

    // ========== getTimeline - subgoal만 요청 ==========

    @Test
    @DisplayName("subgoal 타입만 요청 시 성공")
    void getTimeline_SubGoalOnly_Success() {
        // given
        List<String> types = Arrays.asList("subgoal");
        List<SubGoalResponseDTO> subGoals = Arrays.asList(subGoalResponseDTO);
        List<TimelineItemDTO> subGoalItems = Arrays.asList(subGoalItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(Collections.emptyList())
                .subgoals(subGoalItems)
                .totalCount(1)
                .planCount(0)
                .subgoalCount(1)
                .build();

        when(subGoalService.getSubGoalsInDateRange(startDate, endDate, userId)).thenReturn(subGoals);
        when(timelineConverter.convertSubGoalToTimelineItem(subGoalResponseDTO)).thenReturn(subGoalItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(any(), eq(subGoalItems))).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getPlanCount()).isEqualTo(0);
        assertThat(result.getSubgoalCount()).isEqualTo(1);
        assertThat(result.getPlans()).isEmpty();
        assertThat(result.getSubgoals()).hasSize(1);

        verify(planService, times(0)).getPlansInDateRange(any(), any(), any());
        verify(subGoalService, times(1)).getSubGoalsInDateRange(startDate, endDate, userId);
    }

    // ========== getTimeline - 빈 타입 리스트 ==========

    @Test
    @DisplayName("빈 타입 리스트로 요청 시 빈 결과 반환")
    void getTimeline_EmptyTypes_Success() {
        // given
        List<String> emptyTypes = Collections.emptyList();

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(Collections.emptyList())
                .subgoals(Collections.emptyList())
                .totalCount(0)
                .planCount(0)
                .subgoalCount(0)
                .build();

        when(timelineConverter.convertTimelineResponseDTO(any(), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, emptyTypes, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(0);
        assertThat(result.getPlanCount()).isEqualTo(0);
        assertThat(result.getSubgoalCount()).isEqualTo(0);

        verify(planService, times(0)).getPlansInDateRange(any(), any(), any());
        verify(subGoalService, times(0)).getSubGoalsInDateRange(any(), any(), any());
    }

    // ========== getTimeline - 결과가 없는 경우 ==========

    @Test
    @DisplayName("조회 결과가 없는 경우 빈 리스트 반환")
    void getTimeline_NoResults_Success() {
        // given
        List<String> types = Arrays.asList("plan", "subgoal");

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(Collections.emptyList())
                .subgoals(Collections.emptyList())
                .totalCount(0)
                .planCount(0)
                .subgoalCount(0)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, userId)).thenReturn(Collections.emptyList());
        when(subGoalService.getSubGoalsInDateRange(startDate, endDate, userId)).thenReturn(Collections.emptyList());
        when(timelineConverter.convertTimelineResponseDTO(any(), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(0);
        assertThat(result.getPlans()).isEmpty();
        assertThat(result.getSubgoals()).isEmpty();
    }

    // ========== getTimeline - 다중 데이터 ==========

    @Test
    @DisplayName("여러 plan과 subgoal이 있는 경우")
    void getTimeline_MultipleItems_Success() {
        // given
        List<String> types = Arrays.asList("plan", "subgoal");

        PlanResponseDTO plan2 = PlanResponseDTO.builder()
                .planId(2L)
                .title("운동")
                .planCategory(PlanCategory.WORKOUT)
                .build();

        SubGoalResponseDTO subGoal2 = SubGoalResponseDTO.builder()
                .subGoalId(2L)
                .title("독서")
                .build();

        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO, plan2);
        List<SubGoalResponseDTO> subGoals = Arrays.asList(subGoalResponseDTO, subGoal2);

        TimelineItemDTO planItem2 = TimelineItemDTO.builder()
                .id(2L)
                .title("운동")
                .build();

        TimelineItemDTO subGoalItem2 = TimelineItemDTO.builder()
                .id(2L)
                .title("독서")
                .build();

        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO, planItem2);
        List<TimelineItemDTO> subGoalItems = Arrays.asList(subGoalItemDTO, subGoalItem2);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(subGoalItems)
                .totalCount(4)
                .planCount(2)
                .subgoalCount(2)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, userId)).thenReturn(plans);
        when(subGoalService.getSubGoalsInDateRange(startDate, endDate, userId)).thenReturn(subGoals);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertPlanToTimelineItem(plan2)).thenReturn(planItem2);
        when(timelineConverter.convertSubGoalToTimelineItem(subGoalResponseDTO)).thenReturn(subGoalItemDTO);
        when(timelineConverter.convertSubGoalToTimelineItem(subGoal2)).thenReturn(subGoalItem2);
        when(timelineConverter.convertTimelineResponseDTO(planItems, subGoalItems)).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getPlanCount()).isEqualTo(2);
        assertThat(result.getSubgoalCount()).isEqualTo(2);
        assertThat(result.getPlans()).hasSize(2);
        assertThat(result.getSubgoals()).hasSize(2);
    }

    // ========== getTimeline - 날짜 범위 테스트 ==========

    @Test
    @DisplayName("같은 날짜로 시작일과 종료일 설정")
    void getTimeline_SameStartAndEndDate_Success() {
        // given
        LocalDate sameDate = LocalDate.of(2025, 10, 15);
        List<String> types = Arrays.asList("plan");
        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO);
        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(Collections.emptyList())
                .totalCount(1)
                .planCount(1)
                .subgoalCount(0)
                .build();

        when(planService.getPlansInDateRange(sameDate, sameDate, userId)).thenReturn(plans);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(any(), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(sameDate, sameDate, types, userId);

        // then
        assertThat(result).isNotNull();
        verify(planService, times(1)).getPlansInDateRange(sameDate, sameDate, userId);
    }

    @Test
    @DisplayName("긴 날짜 범위로 조회")
    void getTimeline_LongDateRange_Success() {
        // given
        LocalDate longStartDate = LocalDate.of(2025, 1, 1);
        LocalDate longEndDate = LocalDate.of(2025, 12, 31);
        List<String> types = Arrays.asList("plan", "subgoal");

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(Collections.emptyList())
                .subgoals(Collections.emptyList())
                .totalCount(0)
                .planCount(0)
                .subgoalCount(0)
                .build();

        when(planService.getPlansInDateRange(longStartDate, longEndDate, userId)).thenReturn(Collections.emptyList());
        when(subGoalService.getSubGoalsInDateRange(longStartDate, longEndDate, userId)).thenReturn(Collections.emptyList());
        when(timelineConverter.convertTimelineResponseDTO(any(), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(longStartDate, longEndDate, types, userId);

        // then
        assertThat(result).isNotNull();
        verify(planService, times(1)).getPlansInDateRange(longStartDate, longEndDate, userId);
        verify(subGoalService, times(1)).getSubGoalsInDateRange(longStartDate, longEndDate, userId);
    }

    // ========== getTimeline - 다른 userId ==========

    @Test
    @DisplayName("다른 사용자 ID로 조회")
    void getTimeline_DifferentUserId_Success() {
        // given
        Long differentUserId = 999L;
        List<String> types = Arrays.asList("plan");
        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO);
        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(Collections.emptyList())
                .totalCount(1)
                .planCount(1)
                .subgoalCount(0)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, differentUserId)).thenReturn(plans);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(any(), any())).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, differentUserId);

        // then
        assertThat(result).isNotNull();
        verify(planService, times(1)).getPlansInDateRange(startDate, endDate, differentUserId);
    }

    // ========== getTimeline - 잘못된 타입 문자열 ==========

    @Test
    @DisplayName("인식할 수 없는 타입 문자열이 포함된 경우 해당 타입은 무시")
    void getTimeline_UnrecognizedType_Ignored() {
        // given
        List<String> types = Arrays.asList("plan", "unknown", "subgoal");
        List<PlanResponseDTO> plans = Arrays.asList(planResponseDTO);
        List<SubGoalResponseDTO> subGoals = Arrays.asList(subGoalResponseDTO);
        List<TimelineItemDTO> planItems = Arrays.asList(planItemDTO);
        List<TimelineItemDTO> subGoalItems = Arrays.asList(subGoalItemDTO);

        TimelineResponseDTO expectedResponse = TimelineResponseDTO.builder()
                .plans(planItems)
                .subgoals(subGoalItems)
                .totalCount(2)
                .planCount(1)
                .subgoalCount(1)
                .build();

        when(planService.getPlansInDateRange(startDate, endDate, userId)).thenReturn(plans);
        when(subGoalService.getSubGoalsInDateRange(startDate, endDate, userId)).thenReturn(subGoals);
        when(timelineConverter.convertPlanToTimelineItem(planResponseDTO)).thenReturn(planItemDTO);
        when(timelineConverter.convertSubGoalToTimelineItem(subGoalResponseDTO)).thenReturn(subGoalItemDTO);
        when(timelineConverter.convertTimelineResponseDTO(planItems, subGoalItems)).thenReturn(expectedResponse);

        // when
        TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(2);
        // "unknown" 타입은 무시되고 plan과 subgoal만 조회됨
        verify(planService, times(1)).getPlansInDateRange(startDate, endDate, userId);
        verify(subGoalService, times(1)).getSubGoalsInDateRange(startDate, endDate, userId);
    }
}