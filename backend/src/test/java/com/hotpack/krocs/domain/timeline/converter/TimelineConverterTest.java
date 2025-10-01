package com.hotpack.krocs.domain.timeline.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineItemDTO;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;
import com.hotpack.krocs.global.common.entity.Color;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimelineConverterTest {

    private TimelineConverter timelineConverter;

    private PlanResponseDTO planResponseDTO;
    private SubGoalResponseDTO subGoalResponseDTO;

    @BeforeEach
    void setUp() {
        timelineConverter = new TimelineConverter();

        planResponseDTO = PlanResponseDTO.builder()
                .planId(1L)
                .title("회의 참석")
                .color(Color.PLAN_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 1, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 1, 15, 0))
                .allDay(false)
                .planCategory(PlanCategory.WORK)
                .build();

        subGoalResponseDTO = SubGoalResponseDTO.builder()
                .subGoalId(1L)
                .title("영어 공부")
                .color(Color.GOAL_BLUE)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 1, 12, 0))
                .build();
    }

    // ========== convertTimelineResponseDTO 테스트 ==========

    @Test
    @DisplayName("plans와 subgoals를 TimelineResponseDTO로 변환 성공")
    void convertTimelineResponseDTO_Success() {
        // given
        TimelineItemDTO planItem = TimelineItemDTO.builder()
                .id(1L)
                .title("회의 참석")
                .build();

        TimelineItemDTO subgoalItem = TimelineItemDTO.builder()
                .id(2L)
                .title("영어 공부")
                .build();

        List<TimelineItemDTO> plans = Arrays.asList(planItem);
        List<TimelineItemDTO> subgoals = Arrays.asList(subgoalItem);

        // when
        TimelineResponseDTO result = timelineConverter.convertTimelineResponseDTO(plans, subgoals);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).hasSize(1);
        assertThat(result.getSubgoals()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getPlanCount()).isEqualTo(1);
        assertThat(result.getSubgoalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 리스트로 TimelineResponseDTO 변환")
    void convertTimelineResponseDTO_EmptyLists() {
        // given
        List<TimelineItemDTO> emptyPlans = Collections.emptyList();
        List<TimelineItemDTO> emptySubgoals = Collections.emptyList();

        // when
        TimelineResponseDTO result = timelineConverter.convertTimelineResponseDTO(emptyPlans, emptySubgoals);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).isEmpty();
        assertThat(result.getSubgoals()).isEmpty();
        assertThat(result.getTotalCount()).isEqualTo(0);
        assertThat(result.getPlanCount()).isEqualTo(0);
        assertThat(result.getSubgoalCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("plans만 있고 subgoals가 비어있는 경우")
    void convertTimelineResponseDTO_OnlyPlans() {
        // given
        TimelineItemDTO planItem1 = TimelineItemDTO.builder().id(1L).build();
        TimelineItemDTO planItem2 = TimelineItemDTO.builder().id(2L).build();
        List<TimelineItemDTO> plans = Arrays.asList(planItem1, planItem2);
        List<TimelineItemDTO> emptySubgoals = Collections.emptyList();

        // when
        TimelineResponseDTO result = timelineConverter.convertTimelineResponseDTO(plans, emptySubgoals);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).hasSize(2);
        assertThat(result.getSubgoals()).isEmpty();
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getPlanCount()).isEqualTo(2);
        assertThat(result.getSubgoalCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("subgoals만 있고 plans가 비어있는 경우")
    void convertTimelineResponseDTO_OnlySubgoals() {
        // given
        List<TimelineItemDTO> emptyPlans = Collections.emptyList();
        TimelineItemDTO subgoalItem1 = TimelineItemDTO.builder().id(1L).build();
        TimelineItemDTO subgoalItem2 = TimelineItemDTO.builder().id(2L).build();
        TimelineItemDTO subgoalItem3 = TimelineItemDTO.builder().id(3L).build();
        List<TimelineItemDTO> subgoals = Arrays.asList(subgoalItem1, subgoalItem2, subgoalItem3);

        // when
        TimelineResponseDTO result = timelineConverter.convertTimelineResponseDTO(emptyPlans, subgoals);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlans()).isEmpty();
        assertThat(result.getSubgoals()).hasSize(3);
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getPlanCount()).isEqualTo(0);
        assertThat(result.getSubgoalCount()).isEqualTo(3);
    }

    // ========== convertPlanToTimelineItem 테스트 ==========

    @Test
    @DisplayName("PlanResponseDTO를 TimelineItemDTO로 변환 성공")
    void convertPlanToTimelineItem_Success() {
        // when
        TimelineItemDTO result = timelineConverter.convertPlanToTimelineItem(planResponseDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("회의 참석");
        assertThat(result.getColor()).isEqualTo(Color.PLAN_BLUE);
        assertThat(result.getIsCompleted()).isFalse();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 1, 14, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 1, 15, 0));
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.WORK);
    }

    @Test
    @DisplayName("완료된 Plan을 TimelineItemDTO로 변환")
    void convertPlanToTimelineItem_CompletedPlan() {
        // given
        PlanResponseDTO completedPlan = PlanResponseDTO.builder()
                .planId(2L)
                .title("완료된 계획")
                .color(Color.PLAN_GREEN)
                .isCompleted(true)
                .startDateTime(LocalDateTime.of(2025, 9, 30, 9, 0))
                .endDateTime(LocalDateTime.of(2025, 9, 30, 10, 0))
                .allDay(false)
                .planCategory(PlanCategory.STUDY)
                .build();

        // when
        TimelineItemDTO result = timelineConverter.convertPlanToTimelineItem(completedPlan);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getIsCompleted()).isTrue();
        assertThat(result.getColor()).isEqualTo(Color.PLAN_GREEN);
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.STUDY);
    }

    @Test
    @DisplayName("종일 Plan을 TimelineItemDTO로 변환")
    void convertPlanToTimelineItem_AllDayPlan() {
        // given
        PlanResponseDTO allDayPlan = PlanResponseDTO.builder()
                .planId(3L)
                .title("종일 행사")
                .color(Color.PLAN_RED)
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 10, 5, 0, 0))
                .endDateTime(LocalDateTime.of(2025, 10, 5, 23, 59))
                .allDay(true)
                .planCategory(PlanCategory.REST)
                .build();

        // when
        TimelineItemDTO result = timelineConverter.convertPlanToTimelineItem(allDayPlan);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.REST);
    }

    @Test
    @DisplayName("다양한 PlanCategory로 변환")
    void convertPlanToTimelineItem_VariousCategories() {
        // given
        PlanResponseDTO workoutPlan = PlanResponseDTO.builder()
                .planId(4L)
                .title("운동")
                .planCategory(PlanCategory.WORKOUT)
                .build();

        PlanResponseDTO healthPlan = PlanResponseDTO.builder()
                .planId(5L)
                .title("병원")
                .planCategory(PlanCategory.HEALTH)
                .build();

        // when
        TimelineItemDTO workoutResult = timelineConverter.convertPlanToTimelineItem(workoutPlan);
        TimelineItemDTO healthResult = timelineConverter.convertPlanToTimelineItem(healthPlan);

        // then
        assertThat(workoutResult.getPlanCategory()).isEqualTo(PlanCategory.WORKOUT);
        assertThat(healthResult.getPlanCategory()).isEqualTo(PlanCategory.HEALTH);
    }

    // ========== convertSubGoalToTimelineItem 테스트 ==========

    @Test
    @DisplayName("SubGoalResponseDTO를 TimelineItemDTO로 변환 성공")
    void convertSubGoalToTimelineItem_Success() {
        // when
        TimelineItemDTO result = timelineConverter.convertSubGoalToTimelineItem(subGoalResponseDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("영어 공부");
        assertThat(result.getColor()).isEqualTo(Color.GOAL_BLUE);
        assertThat(result.getIsCompleted()).isFalse();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 1, 10, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 1, 12, 0));
        assertThat(result.getAllDay()).isNull();  // SubGoal에는 allDay 필드가 없음
        assertThat(result.getPlanCategory()).isNull();  // SubGoal에는 planCategory 필드가 없음
    }

    @Test
    @DisplayName("완료된 SubGoal을 TimelineItemDTO로 변환")
    void convertSubGoalToTimelineItem_CompletedSubGoal() {
        // given
        SubGoalResponseDTO completedSubGoal = SubGoalResponseDTO.builder()
                .subGoalId(2L)
                .title("완료된 목표")
                .color(Color.GOAL_GREEN)
                .isCompleted(true)
                .startDateTime(LocalDateTime.of(2025, 9, 28, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 9, 28, 16, 0))
                .build();

        // when
        TimelineItemDTO result = timelineConverter.convertSubGoalToTimelineItem(completedSubGoal);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getIsCompleted()).isTrue();
        assertThat(result.getColor()).isEqualTo(Color.GOAL_GREEN);
    }

    @Test
    @DisplayName("다양한 Color로 SubGoal 변환")
    void convertSubGoalToTimelineItem_VariousColors() {
        // given
        SubGoalResponseDTO redSubGoal = SubGoalResponseDTO.builder()
                .subGoalId(3L)
                .title("빨간 목표")
                .color(Color.GOAL_RED)
                .build();

        SubGoalResponseDTO purpleSubGoal = SubGoalResponseDTO.builder()
                .subGoalId(4L)
                .title("보라 목표")
                .color(Color.GOAL_PURPLE)
                .build();

        // when
        TimelineItemDTO redResult = timelineConverter.convertSubGoalToTimelineItem(redSubGoal);
        TimelineItemDTO purpleResult = timelineConverter.convertSubGoalToTimelineItem(purpleSubGoal);

        // then
        assertThat(redResult.getColor()).isEqualTo(Color.GOAL_RED);
        assertThat(purpleResult.getColor()).isEqualTo(Color.GOAL_PURPLE);
    }

    @Test
    @DisplayName("null 날짜를 가진 SubGoal 변환")
    void convertSubGoalToTimelineItem_NullDates() {
        // given
        SubGoalResponseDTO subGoalWithNullDates = SubGoalResponseDTO.builder()
                .subGoalId(5L)
                .title("날짜 없는 목표")
                .color(Color.GOAL_YELLOW)
                .isCompleted(false)
                .startDateTime(null)
                .endDateTime(null)
                .build();

        // when
        TimelineItemDTO result = timelineConverter.convertSubGoalToTimelineItem(subGoalWithNullDates);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDateTime()).isNull();
        assertThat(result.getEndDateTime()).isNull();
    }
}