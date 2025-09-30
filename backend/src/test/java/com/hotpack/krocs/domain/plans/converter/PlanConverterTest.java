package com.hotpack.krocs.domain.plans.converter;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlanConverterTest {

    @Mock
    private SubPlanConverter subPlanConverter;

    @InjectMocks
    private PlanConverter planConverter;

    private Goal validGoal;
    private SubGoal validSubGoal;
    private PlanCreateRequestDTO validRequestDTO;
    private Plan validPlan;

    @BeforeEach
    void setUp() {
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
            .planCategory(PlanCategory.STUDY)
            .color(Color.PLAN_BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .build();

        validPlan = Plan.builder()
            .planId(1L)
            .title("테스트 일정")
            .planCategory(PlanCategory.STUDY)
            .color(Color.PLAN_BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 10, 0))
            .allDay(false)
            .isCompleted(false)
            .build();
    }

    // 1. toEntity from Request DTO

    @Test
    @DisplayName("RequestDTO를 Plan 엔티티로 변환")
    void toEntity_Success() {
        // when
        Plan result = planConverter.toEntity(validRequestDTO);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.STUDY);
        assertThat(result.getColor()).isEqualTo(Color.PLAN_BLUE);
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 9, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 10, 0));
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isFalse();
    }

    @Test
    @DisplayName("allDay = true인 경우 변환")
    void toEntity_AllDay_True() {
        // given
        PlanCreateRequestDTO allDayRequest = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .allDay(true)
            .build();

        // when
        Plan result = planConverter.toEntity(allDayRequest);

        // then
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getStartDateTime()).isNull();
        assertThat(result.getEndDateTime()).isNull();
    }

    @Test
    @DisplayName("allDay = false 명시적 테스트")
    void toEntity_AllDay_False() {
        // given
        PlanCreateRequestDTO request = PlanCreateRequestDTO.builder()
            .title("시간 지정 일정")
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 17, 0))
            .allDay(false)
            .build();

        // when
        Plan result = planConverter.toEntity(request);

        // then
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 9, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 17, 0));
    }

    @Test
    @DisplayName("allDay = null인 경우 기본값 false")
    void toEntity_AllDay_Null() {
        // given
        PlanCreateRequestDTO request = PlanCreateRequestDTO.builder()
            .title("allDay null 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 9, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 17, 0))
            .allDay(null)
            .build();

        // when
        Plan result = planConverter.toEntity(request);

        // then
        assertThat(result.getAllDay()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 9, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 17, 0));
    }

    @Test
    @DisplayName("allDay = true이면서 시간이 설정된 경우 시간 변환")
    void toEntity_AllDay_True_With_DateTime() {
        // given
        PlanCreateRequestDTO request = PlanCreateRequestDTO.builder()
            .title("하루 종일 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_BLUE)
            .startDateTime(LocalDateTime.of(2025, 8, 1, 15, 30))
            .endDateTime(LocalDateTime.of(2025, 8, 1, 20, 45))
            .allDay(true)
            .build();

        // when
        Plan result = planConverter.toEntity(request);

        // then
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 0, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 23, 59, 59));
    }


    @Test
    @DisplayName("RequestDTO가 null인 경우")
    void toEntity_RequestDTO_Null() {
        // when & then
        assertThatThrownBy(() -> planConverter.toEntity((PlanCreateRequestDTO) null)).isInstanceOf(NullPointerException.class);
    }

    // 2. toEntity from Plan

    @Test
    @DisplayName("Plan 엔티티를 ResponseDTO로 변환")
    void toCreateResponseDTO_Success() {
        // when
        PlanResponseDTO result = planConverter.toEntity(validPlan);

        // then
        assertThat(result.getPlanId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 일정");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.STUDY);
        assertThat(result.getColor()).isEqualTo(Color.PLAN_BLUE);
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 9, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 8, 1, 10, 0));
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isFalse();
    }

    @Test
    @DisplayName("Plan 객체가 null인 경우")
    void toCreateResponseDTO_PlanNull() {
        // when & then
        assertThatThrownBy(() -> planConverter.toEntity((Plan) null))
            .isInstanceOf(NullPointerException.class);
    }


    @Test
    @DisplayName("Plan의 SubPlans가 null인 경우")
    void toResponseDTO_Plan_SubPlans_Null() {
        // given
        Plan planWithNullSubPlans = Plan.builder()
            .planId(1L)
            .subPlans(null)
            .title("SubPlans null인 일정")
            .allDay(false)
            .isCompleted(false)
            .build();

        // when
        PlanResponseDTO result = planConverter.toEntity(planWithNullSubPlans);

        // then
        assertThat(result.getSubPlans()).isEmpty();
        verify(subPlanConverter, never()).toSubPlanResponseDTO(any());
    }

    @Test
    @DisplayName("Plan의 SubPlans가 빈 리스트인 경우")
    void toResponseDTO_Plan_SubPlans_Empty() {
        // given
        Plan planWithEmptySubPlans = Plan.builder()
            .planId(1L)
            .subPlans(List.of())
            .title("SubPlans 빈 리스트인 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_GREEN)
            .allDay(false)
            .isCompleted(false)
            .build();

        // when
        PlanResponseDTO result = planConverter.toEntity(planWithEmptySubPlans);

        // then
        assertThat(result.getSubPlans()).isEmpty();
        verify(subPlanConverter, never()).toSubPlanResponseDTO(any());
    }

    @Test
    @DisplayName("완료된 Plan 변환 테스트")
    void toResponseDTO_Plan_Completed() {
        // given
        LocalDateTime completedTime = LocalDateTime.of(2025, 8, 1, 15, 0);
        Plan completedPlan = Plan.builder()
            .planId(1L)
            .title("완료된 일정")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_GREEN)
            .allDay(false)
            .isCompleted(true)
            .completedAt(completedTime)
            .build();

        // when
        PlanResponseDTO result = planConverter.toEntity(completedPlan);

        // then
        assertThat(result.getIsCompleted()).isTrue();
        assertThat(result.getCompletedAt()).isEqualTo(completedTime);
    }

    // 3. toListPlanResponseDTO

    @Test
    @DisplayName("정상적인 Plan 리스트 변환")
    void toListPlanResponseDTO_Success() {
        // given
        Plan plan1 = Plan.builder()
            .planId(1L)
            .title("일정 1")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_GREEN)
            .allDay(false)
            .isCompleted(false)
            .build();

        Plan plan2 = Plan.builder()
            .planId(2L)
            .title("일정 2")
            .planCategory(PlanCategory.ETC)
            .color(Color.PLAN_GREEN)
            .allDay(true)
            .isCompleted(true)
            .build();

        List<Plan> plans = List.of(plan1, plan2);

        // when
        List<PlanResponseDTO> result = planConverter.toListPlanResponseDTO(plans);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPlanId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("일정 1");

        assertThat(result.get(1).getPlanId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("일정 2");
    }

    @Test
    @DisplayName("빈 Plan 리스트 변환")
    void toListPlanResponseDTO_Empty_List() {
        // given
        List<Plan> emptyPlans = List.of();

        // when
        List<PlanResponseDTO> result = planConverter.toListPlanResponseDTO(emptyPlans);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null Plan 리스트 변환")
    void toListPlanResponseDTO_Null_List() {
        // when & then
        assertThatThrownBy(() -> planConverter.toListPlanResponseDTO(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Plan이 null인 요소가 포함된 리스트")
    void toListPlanResponseDTO_Contains_Null_Plan() {
        // given
        List<Plan> plansWithNull = Arrays.asList(validPlan, null);

        // when & then
        assertThatThrownBy(() -> planConverter.toListPlanResponseDTO(plansWithNull))
            .isInstanceOf(NullPointerException.class);
    }

    // 3. toUpdatePlanRequestDTO

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 기본 케이스")
    void toUpdatePlanRequestDTO_Success_Basic() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .title("수정된 제목")
            .isCompleted(true)
            .build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - allDay=true, 시간 정규화")
    void toUpdatePlanRequestDTO_Success_AllDay_True() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .allDay(true)
            .build();

        Boolean allDay = true;
        LocalDateTime normalizedStartDateTime = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime normalizedEndDateTime = LocalDateTime.of(2025, 8, 1, 23, 59, 59);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, normalizedStartDateTime, normalizedEndDateTime);

        // then
        assertThat(result.getTitle()).isNull();
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(normalizedStartDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(normalizedEndDateTime);
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getIsCompleted()).isNull();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 시간만 수정")
    void toUpdatePlanRequestDTO_Success_DateTime_Only() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .startDateTime(LocalDateTime.of(2025, 8, 2, 14, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 2, 16, 0))
            .build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 2, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 2, 16, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getTitle()).isNull();
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isNull();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 완료 상태만 수정")
    void toUpdatePlanRequestDTO_Success_Completed_Only() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .isCompleted(true)
            .build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getTitle()).isNull();
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 모든 필드 수정")
    void toUpdatePlanRequestDTO_Success_All_Fields() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .title("완전히 새로운 제목")
            .planCategory(PlanCategory.WORK)
            .color(Color.PLAN_GREEN)
            .startDateTime(LocalDateTime.of(2025, 8, 3, 10, 0))
            .endDateTime(LocalDateTime.of(2025, 8, 3, 12, 0))
            .allDay(true)
            .isCompleted(true)
            .build();

        Boolean allDay = true;
        LocalDateTime normalizedStartDateTime = LocalDateTime.of(2025, 8, 3, 0, 0);
        LocalDateTime normalizedEndDateTime = LocalDateTime.of(2025, 8, 3, 23, 59, 59);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, normalizedStartDateTime, normalizedEndDateTime);

        // then
        assertThat(result.getTitle()).isEqualTo("완전히 새로운 제목");
        assertThat(result.getPlanCategory()).isEqualTo(PlanCategory.WORK);
        assertThat(result.getColor()).isEqualTo(Color.PLAN_GREEN);
        assertThat(result.getStartDateTime()).isEqualTo(normalizedStartDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(normalizedEndDateTime);
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 빈 요청")
    void toUpdatePlanRequestDTO_Success_Empty_Request() {
        // given
        PlanUpdateRequestDTO emptyRequest = PlanUpdateRequestDTO.builder().build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            emptyRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getTitle()).isNull();
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isNull();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - allDay false에서 true로 변경")
    void toUpdatePlanRequestDTO_Success_AllDay_False_To_True() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .allDay(true)
            .build();

        Boolean allDay = true;
        LocalDateTime normalizedStartDateTime = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime normalizedEndDateTime = LocalDateTime.of(2025, 8, 1, 23, 59, 59);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, normalizedStartDateTime, normalizedEndDateTime);

        // then
        assertThat(result.getAllDay()).isTrue();
        assertThat(result.getStartDateTime()).isEqualTo(normalizedStartDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(normalizedEndDateTime);
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - allDay true에서 false로 변경")
    void toUpdatePlanRequestDTO_Success_AllDay_True_To_False() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .allDay(false)
            .build();

        Boolean allDay = false; // 변경된 값
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 23, 59, 59);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 완료 상태 true에서 false로 변경")
    void toUpdatePlanRequestDTO_Success_Completed_True_To_False() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .isCompleted(false)
            .build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getIsCompleted()).isFalse();
        assertThat(result.getTitle()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
    }

    @Test
    @DisplayName("수정 요청 DTO 변환 성공 - 제목만 수정 (시간은 기존값 유지)")
    void toUpdatePlanRequestDTO_Success_Title_Only_Keep_DateTime() {
        // given
        PlanUpdateRequestDTO originalRequest = PlanUpdateRequestDTO.builder()
            .title("제목만 수정")
            .build();

        Boolean allDay = false;
        LocalDateTime startDateTime = LocalDateTime.of(2025, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 8, 1, 10, 0);

        // when
        PlanUpdateRequestDTO result = planConverter.toUpdatePlanRequestDTO(
            originalRequest, allDay, startDateTime, endDateTime);

        // then
        assertThat(result.getTitle()).isEqualTo("제목만 수정");
        assertThat(result.getPlanCategory()).isNull();
        assertThat(result.getColor()).isNull();
        assertThat(result.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(result.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(result.getAllDay()).isFalse();
        assertThat(result.getIsCompleted()).isNull();
    }
}