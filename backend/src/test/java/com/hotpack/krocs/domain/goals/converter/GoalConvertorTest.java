package com.hotpack.krocs.domain.goals.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalSearchRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.global.common.entity.Priority;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalConvertorTest {

  private GoalConverter goalConvertor;

  private GoalCreateRequestDTO validRequestDTO;
  private Goal validGoal;
  private Goal existingGoal;

  @BeforeEach
  void setUp() {
    goalConvertor = new GoalConverter();

    validRequestDTO = GoalCreateRequestDTO.builder()
        .title("테스트 목표")
        .priority(Priority.HIGH)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2024, 12, 31))
        .build();

    validGoal = Goal.builder()
        .goalId(1L)
        .title("테스트 목표")
        .priority(Priority.HIGH)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2024, 12, 31))
        .isCompleted(false)
        .build();

    existingGoal = Goal.builder()
        .goalId(1L)
        .title("기존 제목")
        .priority(Priority.MEDIUM)
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(7))
        .isCompleted(false)
        .build();
  }

  // ========== CreateGoalRequestDTO 변환 테스트 ==========

  @Test
  @DisplayName("CreateGoalRequestDTO를 Goal 엔티티로 변환")
  void toEntity_Success() {
    // when
    Goal result = goalConvertor.toEntity(validRequestDTO);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("테스트 목표");
    assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(result.getIsCompleted()).isFalse();
  }

  @Test
  @DisplayName("Goal 엔티티를 GoalCreateResponseDTO로 변환")
  void toCreateResponseDTO_Success() {
    // when
    GoalCreateResponseDTO result = goalConvertor.toCreateResponseDTO(validGoal);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getGoalId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("테스트 목표");
    assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(result.isCompleted()).isFalse();
  }

  @Test
  @DisplayName("최소 데이터로 DTO를 엔티티로 변환")
  void toEntity_MinimalData() {
    // given
    GoalCreateRequestDTO minimalRequest = GoalCreateRequestDTO.builder()
        .title("최소 목표")
        .build();

    // when
    Goal result = goalConvertor.toEntity(minimalRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("최소 목표");
    assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM); // 기본값
    assertThat(result.getStartDate()).isNull();
    assertThat(result.getEndDate()).isNull();
    assertThat(result.getIsCompleted()).isFalse();
  }

  @Test
  @DisplayName("null 값이 포함된 DTO를 엔티티로 변환")
  void toEntity_WithNullValues() {
    // given
    GoalCreateRequestDTO requestWithNulls = GoalCreateRequestDTO.builder()
        .title("null 테스트")
        .priority(null)
        .startDate(null)
        .endDate(null)
        .build();

    // when
    Goal result = goalConvertor.toEntity(requestWithNulls);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("null 테스트");
    assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM); // 기본값
    assertThat(result.getStartDate()).isNull();
    assertThat(result.getEndDate()).isNull();
  }

  @Test
  @DisplayName("완료된 목표를 응답 DTO로 변환")
  void toCreateResponseDTO_CompletedGoal() {
    // given
    Goal completedGoal = Goal.builder()
        .goalId(1L)
        .title("완료된 목표")
        .priority(Priority.HIGH)
        .isCompleted(true)
        .build();

    // when
    GoalCreateResponseDTO result = goalConvertor.toCreateResponseDTO(completedGoal);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getGoalId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("완료된 목표");
    assertThat(result.isCompleted()).isTrue();
  }

  @Test
  @DisplayName("null 값이 포함된 Goal을 응답 DTO로 변환")
  void toCreateResponseDTO_WithNullValues() {
    // given
    Goal goalWithNulls = Goal.builder()
        .goalId(1L)
        .title("null 테스트")
        .priority(null)
        .startDate(null)
        .endDate(null)
        .isCompleted(false)
        .build();

    // when
    GoalCreateResponseDTO result = goalConvertor.toCreateResponseDTO(goalWithNulls);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getGoalId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("null 테스트");
    assertThat(result.getPriority()).isNull();
    assertThat(result.getStartDate()).isNull();
    assertThat(result.getEndDate()).isNull();
    assertThat(result.isCompleted()).isFalse();
  }

  // ========== toGoalResponseDTO 테스트 ==========

  @Test
  @DisplayName("Goal 엔티티를 GoalResponseDTO로 변환")
  void toGoalResponseDTO_Success() {
    // given
    Goal goal = Goal.builder()
        .goalId(2L)
        .title("GoalResponseDTO 테스트")
        .priority(Priority.CRITICAL)
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 8, 31))
        .isCompleted(true)
        .build();

    // when
    GoalResponseDTO result = goalConvertor.toGoalResponseDTO(goal);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getGoalId()).isEqualTo(2L);
    assertThat(result.getTitle()).isEqualTo("GoalResponseDTO 테스트");
    assertThat(result.getPriority()).isEqualTo(Priority.CRITICAL);
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 8, 1));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 31));
    assertThat(result.getIsCompleted()).isTrue();
    assertThat(result.getCompletionPercentage()).isEqualTo(100);
  }

  @Test
  @DisplayName("List<Goal>을 List<GoalResponseDTO>로 변환")
  void toGoalResponseDTO_List_Success() {
    // given
    Goal goal1 = Goal.builder()
        .goalId(1L)
        .title("목표 1")
        .priority(Priority.HIGH)
        .isCompleted(false)
        .build();

    Goal goal2 = Goal.builder()
        .goalId(2L)
        .title("목표 2")
        .priority(Priority.LOW)
        .isCompleted(true)
        .build();

    List<Goal> goals = Arrays.asList(goal1, goal2);

    // when
    List<GoalResponseDTO> result = goalConvertor.toGoalResponseDTO(goals);

    // then
    assertThat(result).hasSize(2);

    // 첫 번째 목표 검증
    GoalResponseDTO firstResult = result.get(0);
    assertThat(firstResult.getGoalId()).isEqualTo(1L);
    assertThat(firstResult.getTitle()).isEqualTo("목표 1");
    assertThat(firstResult.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(firstResult.getIsCompleted()).isFalse();
    assertThat(firstResult.getCompletionPercentage()).isEqualTo(0);

    // 두 번째 목표 검증
    GoalResponseDTO secondResult = result.get(1);
    assertThat(secondResult.getGoalId()).isEqualTo(2L);
    assertThat(secondResult.getTitle()).isEqualTo("목표 2");
    assertThat(secondResult.getPriority()).isEqualTo(Priority.LOW);
    assertThat(secondResult.getIsCompleted()).isTrue();
    assertThat(secondResult.getCompletionPercentage()).isEqualTo(100);
  }

  @Test
  @DisplayName("빈 Goal 리스트를 빈 GoalResponseDTO 리스트로 변환")
  void toGoalResponseDTO_EmptyList() {
    // given
    List<Goal> emptyGoals = Collections.emptyList();

    // when
    List<GoalResponseDTO> result = goalConvertor.toGoalResponseDTO(emptyGoals);

    // then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  // ========== 날짜/시간 필드 테스트 ==========

  @Test
  @DisplayName("null createdAt, updatedAt 필드 처리")
  void dateTimeFields_Null() {
    // given
    Goal goalWithNullDates = Goal.builder()
        .goalId(1L)
        .title("null 날짜 테스트")
        .priority(Priority.LOW)
        .isCompleted(false)
        .build();
    // createdAt, updatedAt는 null로 남겨둠

    // when
    GoalCreateResponseDTO result = goalConvertor.toCreateResponseDTO(goalWithNullDates);

    // then
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  // ========== toGoalSearchRequestDTO 테스트 ==========

  @Test
  @DisplayName("GoalSearchRequestDTO 변환 성공 - 모든 필드 값 있음")
  void toGoalSearchRequestDTO_Success_AllFields() {
    // given
    LocalDate searchDate = LocalDate.of(2024, 3, 15);
    String keyword = "운동";
    String status = "IN_PROGRESS";

    // when
    GoalSearchRequestDTO result = goalConvertor.toGoalSearchRequestDTO(searchDate, keyword, status);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSearchDate()).isEqualTo(LocalDate.of(2024, 3, 15));
    assertThat(result.getKeyword()).isEqualTo("운동");
    assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
  }

  @Test
  @DisplayName("GoalSearchRequestDTO 변환 성공 - 모든 필드 null")
  void toGoalSearchRequestDTO_Success_AllFieldsNull() {
    // given
    LocalDate searchDate = null;
    String keyword = null;
    String status = null;

    // when
    GoalSearchRequestDTO result = goalConvertor.toGoalSearchRequestDTO(searchDate, keyword, status);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSearchDate()).isNull();
    assertThat(result.getKeyword()).isNull();
    assertThat(result.getStatus()).isNull();
  }

  @Test
  @DisplayName("GoalSearchRequestDTO 변환 성공 - 일부 필드만 값 있음")
  void toGoalSearchRequestDTO_Success_PartialFields() {
    // given
    LocalDate searchDate = LocalDate.of(2024, 6, 1);
    String keyword = null;
    String status = "COMPLETED";

    // when
    GoalSearchRequestDTO result = goalConvertor.toGoalSearchRequestDTO(searchDate, keyword, status);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSearchDate()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(result.getKeyword()).isNull();
    assertThat(result.getStatus()).isEqualTo("COMPLETED");
  }

  @Test
  @DisplayName("GoalSearchRequestDTO 변환 성공 - 빈 문자열 처리")
  void toGoalSearchRequestDTO_Success_EmptyStrings() {
    // given
    LocalDate searchDate = LocalDate.now();
    String keyword = "";
    String status = "";

    // when
    GoalSearchRequestDTO result = goalConvertor.toGoalSearchRequestDTO(searchDate, keyword, status);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSearchDate()).isEqualTo(LocalDate.now());
    assertThat(result.getKeyword()).isEqualTo("");
    assertThat(result.getStatus()).isEqualTo("");
  }

  @Test
  @DisplayName("GoalSearchRequestDTO 변환 성공 - 다양한 상태값")
  void toGoalSearchRequestDTO_Success_VariousStatusValues() {
    // given & when & then
    // IN_PROGRESS 상태
    GoalSearchRequestDTO inProgressResult = goalConvertor.toGoalSearchRequestDTO(
            LocalDate.now(), "키워드", "IN_PROGRESS");
    assertThat(inProgressResult.getStatus()).isEqualTo("IN_PROGRESS");

    // COMPLETED 상태
    GoalSearchRequestDTO completedResult = goalConvertor.toGoalSearchRequestDTO(
            LocalDate.now(), "키워드", "COMPLETED");
    assertThat(completedResult.getStatus()).isEqualTo("COMPLETED");

    // EXPIRED 상태
    GoalSearchRequestDTO expiredResult = goalConvertor.toGoalSearchRequestDTO(
            LocalDate.now(), "키워드", "EXPIRED");
    assertThat(expiredResult.getStatus()).isEqualTo("EXPIRED");
  }
} 