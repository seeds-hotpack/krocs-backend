package com.hotpack.krocs.domain.goals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.goals.converter.GoalConverter;
import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalSearchRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.entity.Priority;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class GoalServiceTest {

    @Mock
    private GoalRepositoryFacade goalRepositoryFacade;
    @Mock
    private SubGoalRepositoryFacade subGoalRepositoryFacade;
    @Mock
    private UserRepositoryFacade userRepositoryFacade;
    @Mock
    private SubGoalConverter subGoalConverter;

    @InjectMocks
    private GoalServiceImpl goalService;
    @InjectMocks
    private SubGoalServiceImpl subGoalService;


    private GoalCreateRequestDTO validRequestDTO;
    private Goal validGoal;
    private GoalCreateResponseDTO validResponseDTO;

    @Mock
    private GoalConverter goalConverter;

    //    private GoalServiceImpl subGoalService;
    private GoalValidator goalValidator = new GoalValidator();

    private Goal existingGoal;

    private User user;

    private SubGoalCreateRequestDTO validSubGoalCreateRequestDTO;
    private SubGoal validSubGoal;
    private SubGoalResponseDTO validSubGoalResponseDTO;
    private SubGoalRequestDTO validSubGoalRequestDTO;

    private Goal createMockGoal(Long goalId, String title, LocalDate startDate, LocalDate endDate,
        boolean isCompleted) {
        return Goal.builder()
            .goalId(goalId)
            .title(title)
            .startDate(startDate)
            .endDate(endDate)
            .isCompleted(isCompleted)
            .priority(Priority.MEDIUM)
            .user(user)
            .build();
    }

    private GoalResponseDTO createMockGoalResponseDTO(Long goalId, String title) {
        return GoalResponseDTO.builder()
            .goalId(goalId)
            .title(title)
            .priority(Priority.MEDIUM)
            .isCompleted(false)
            .build();
    }

    @BeforeEach
    void setUp() {

        user = User.builder()
            .name("박성열")
            .email("qkrtjdduf@example.com")
            .accountType(AccountType.LOCAL)
            .build();

        goalService = new GoalServiceImpl(userRepositoryFacade, goalRepositoryFacade,
            goalConverter, goalValidator);

        validRequestDTO = GoalCreateRequestDTO.builder()
            .title("테스트 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(365))
            .build();

        validGoal = Goal.builder()
            .goalId(1L)
            .title("테스트 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(365))
            .isCompleted(false)
            .user(user)
            .build();

        validResponseDTO = GoalCreateResponseDTO.builder()
            .goalId(1L)
            .title("테스트 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(365))
            .isCompleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        validSubGoalRequestDTO = SubGoalRequestDTO.builder()
            .title("테스트 소목표1")
            .isTimeSelected(true)
            .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 10, 4, 0, 0))
            .build();

        validSubGoalCreateRequestDTO = SubGoalCreateRequestDTO.builder()
            .subGoals(List.of(validSubGoalRequestDTO))
            .build();

        validSubGoal = SubGoal.builder()
            .subGoalId(1L)
            .goal(validGoal)
            .title("테스트 소목표1")
            .isCompleted(false)
            .build();

        existingGoal = Goal.builder()
            .goalId(1L)
            .title("기존 제목")  // 원래 제목
            .priority(Priority.HIGH)
            .isCompleted(false)
            .subGoals(new ArrayList<>())
            .build();

        validSubGoalResponseDTO = SubGoalResponseDTO.builder()
            .subGoalId(validSubGoal.getSubGoalId())
            .title(validSubGoal.getTitle())
            .isCompleted(validSubGoal.getIsCompleted())
            .build();
    }

    // ========== CREATE 테스트 ==========

    @Test
    @DisplayName("대목표 생성 성공 테스트")
    void createGoal_Success() {
        // given
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toCreateResponseDTO(validGoal)).thenReturn(validResponseDTO);
        when(goalConverter.toEntity(eq(validRequestDTO), any(User.class))).thenReturn(
            validGoal);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

        // when
        GoalCreateResponseDTO result = goalService.createGoal(validRequestDTO, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 목표");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("대목표 생성 - 제목이 비어있는 경우")
    void createGoal_EmptyTitle() {
        // given
        when(goalConverter.toEntity(eq(validRequestDTO), any(User.class))).thenReturn(
            validGoal);
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toCreateResponseDTO(any(Goal.class))).thenReturn(validResponseDTO);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        // when
        GoalCreateResponseDTO result = goalService.createGoal(validRequestDTO, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 목표");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("대목표 생성 - 제목이 null인 경우")
    void createGoal_NullTitle() {
        // given
        GoalCreateRequestDTO invalidRequest = GoalCreateRequestDTO.builder()
            .title("")
            .build();

        // when & then
        assertThatThrownBy(() -> goalService.createGoal(invalidRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_TITLE_EMPTY);
    }

    @Test
    @DisplayName("대목표 생성 - 제목이 공백인 경우")
    void createGoal_BlankTitle() {
        // given
        GoalCreateRequestDTO invalidRequest = GoalCreateRequestDTO.builder()
            .title("   ")
            .build();

        // when & then
        assertThatThrownBy(() -> goalService.createGoal(invalidRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_TITLE_EMPTY);
    }

    @Test
    @DisplayName("대목표 생성 - 날짜 범위가 유효하지 않은 경우")
    void createGoal_InvalidDateRange() {
        // given
        GoalCreateRequestDTO invalidRequest = GoalCreateRequestDTO.builder()
            .title("테스트 목표")
            .startDate(LocalDate.of(2024, 12, 31))
            .endDate(LocalDate.of(2024, 1, 1))
            .build();

        // when & then
        assertThatThrownBy(() -> goalService.createGoal(invalidRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType",
                GoalExceptionType.INVALID_GOAL_DATE_RANGE);
    }

    @Test
    @DisplayName("대목표 생성 - Repository에서 예외 발생")
    void createGoal_RepositoryException() {
        // given
        when(goalConverter.toEntity(any())).thenReturn(validGoal);
        when(goalRepositoryFacade.saveGoal(any())).thenThrow(new RuntimeException("데이터베이스 오류"));
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        // when & then
        assertThatThrownBy(() -> goalService.createGoal(validRequestDTO, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType",
                GoalExceptionType.GOAL_CREATION_FAILED);
    }

    @Test
    @DisplayName("대목표 생성 - Convertor에서 예외 발생")
    void createGoal_ConvertorException() {
        // given
        when(goalConverter.toEntity(any())).thenThrow(new RuntimeException("변환 오류"));
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        // when & then
        assertThatThrownBy(() -> goalService.createGoal(validRequestDTO, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType",
                GoalExceptionType.GOAL_CREATION_FAILED);
    }

    @Test
    @DisplayName("대목표 생성 - 최소 필수 데이터만으로 성공")
    void createGoal_MinimalData() {
        // given
        GoalCreateRequestDTO minimalRequest = GoalCreateRequestDTO.builder()
            .title("최소 목표")
            .build();

        Goal minimalGoal = Goal.builder()
            .goalId(1L)
            .title("최소 목표")
            .priority(Priority.MEDIUM)
            .isCompleted(false)
            .user(user)
            .build();

        GoalCreateResponseDTO minimalResponse = GoalCreateResponseDTO.builder()
            .goalId(1L)
            .title("최소 목표")
            .priority(Priority.MEDIUM)
            .isCompleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(goalConverter.toCreateResponseDTO(any(Goal.class))).thenReturn(minimalResponse);
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toEntity(eq(minimalRequest), any(User.class))).thenReturn(
            validGoal);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        // when
        GoalCreateResponseDTO result = goalService.createGoal(minimalRequest, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("최소 목표");
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    // ========== UPDATE 테스트 ==========

    @Test
    @DisplayName("목표 수정 성공 - 제목만 수정")
    void updateGoalById_Success_TitleOnly() {
        // given
        Long goalId = 1L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        Goal updatedGoal = Goal.builder()
            .goalId(1L)
            .title("수정된 제목")
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        GoalResponseDTO expectedResponse = GoalResponseDTO.builder()
            .goalId(1L)
            .title("수정된 제목")
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);
        when(goalConverter.toGoalResponseDTO((Goal) any())).thenReturn(expectedResponse);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

        // when
        GoalResponseDTO result = goalService.updateGoalById(goalId, updateRequest, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("목표 수정 성공 - 여러 필드 수정")
    void updateGoalById_Success_MultipleFields() {
        // given
        Long goalId = 1L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .title("수정된 제목")
            .priority(Priority.LOW)
            .startDate(LocalDate.of(2025, 8, 1))
            .endDate(LocalDate.of(2025, 8, 31))
            .build();

        Goal updatedGoal = Goal.builder()
            .goalId(1L)
            .title("수정된 제목")
            .priority(Priority.LOW)
            .startDate(LocalDate.of(2025, 8, 1))
            .endDate(LocalDate.of(2025, 8, 31))
            .isCompleted(false)
            .build();

        GoalResponseDTO expectedResponse = GoalResponseDTO.builder()
            .goalId(1L)
            .title("수정된 제목")
            .priority(Priority.LOW)
            .startDate(LocalDate.of(2025, 8, 1))
            .endDate(LocalDate.of(2025, 8, 31))
            .isCompleted(false)
            .build();

        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
                existingGoal)
            .thenReturn(updatedGoal);
        when(goalConverter.toGoalResponseDTO(updatedGoal)).thenReturn(expectedResponse);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

        // when
        GoalResponseDTO result = goalService.updateGoalById(goalId, updateRequest, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 8, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 31));
    }

    @Test
    @DisplayName("목표 수정 실패 - 존재하지 않는 goalId")
    void updateGoalById_Fail_GoalNotFound() {
        // given
        Long goalId = 999L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenThrow(
            new GoalException(GoalExceptionType.GOAL_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> goalService.updateGoalById(goalId, updateRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("목표 수정 실패 - 유효하지 않은 제목 (빈 문자열)")
    void updateGoalById_Fail_InvalidTitle() {
        // given
        Long goalId = 1L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .title("")
            .build();

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);

        // when & then
        assertThatThrownBy(() -> goalService.updateGoalById(goalId, updateRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_TITLE_EMPTY);
    }

    @Test
    @DisplayName("목표 수정 실패 - 유효하지 않은 날짜 범위")
    void updateGoalById_Fail_InvalidDateRange() {
        // given
        Long goalId = 1L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .startDate(LocalDate.of(2025, 12, 31))
            .endDate(LocalDate.of(2025, 1, 1))
            .build();

        // when
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);

        // when & then
        assertThatThrownBy(() -> goalService.updateGoalById(goalId, updateRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType",
                GoalExceptionType.INVALID_GOAL_DATE_RANGE);
    }

    @Test
    @DisplayName("목표 수정 - Repository에서 예외 발생")
    void updateGoalById_RepositoryException() {
        // given
        Long goalId = 1L;
        GoalUpdateRequestDTO updateRequest = GoalUpdateRequestDTO.builder()
            .title("수정된 제목")
            .build();

        Goal existingGoal = Goal.builder()
            .goalId(1L)
            .title("기존 제목")
            .build();

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.updateGoalById(goalId, updateRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_UPDATE_FAILED);
    }

    // ========== DELETE 테스트 ==========

    @Test
    @DisplayName("목표 삭제 성공")
    void deleteGoal_Success() {
        // given
        Long goalId = 1L;
        Long userId = 1L;

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.existsActiveGoalById(goalId)).thenReturn(true);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);

        // when & then
        assertThatCode(() -> goalService.deleteGoal(userId, goalId))
            .doesNotThrowAnyException();

        verify(goalRepositoryFacade).existsActiveGoalById(goalId);
    }

    @Test
    @DisplayName("목표 삭제 시 연관 회고 비활성화")
    void deleteGoal_DeactivateRetrospectives() {
        // given
        Long goalId = 1L;
        Long userId = 1L;

        List<Retrospective> retrospectives = new ArrayList<>();
        Goal goalWithRetrospectives = Goal.builder()
            .goalId(goalId)
            .title("회고 포함 목표")
            .priority(Priority.MEDIUM)
            .user(user)
            .subGoals(new ArrayList<>())
            .retrospectives(retrospectives)
            .build();

        Retrospective retro1 = Retrospective.builder()
            .retrospectiveId(101L)
            .user(user)
            .goal(goalWithRetrospectives)
            .outcome(RetrospectiveOutcome.COMPLETE_SUCCESS)
            .build();

        Retrospective retro2 = Retrospective.builder()
            .retrospectiveId(102L)
            .user(user)
            .goal(goalWithRetrospectives)
            .outcome(RetrospectiveOutcome.COMPLETE_FAILURE)
            .build();

        retrospectives.add(retro1);
        retrospectives.add(retro2);

        when(userRepositoryFacade.findActiveUserByUserId(userId)).thenReturn(user);
        when(goalRepositoryFacade.existsActiveGoalById(goalId)).thenReturn(true);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            goalWithRetrospectives);

        // when
        goalService.deleteGoal(userId, goalId);

        // then
        assertThat(goalWithRetrospectives.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(retro1.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(retro2.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("목표 삭제 실패 - 존재하지 않는 goalId")
    void deleteGoal_Fail_Active_GoalNotFound() {
        // given
        Long goalId = 999L;
        Long userId = 1L;

        when(goalRepositoryFacade.existsActiveGoalById(goalId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> goalService.deleteGoal(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_NOT_FOUND);

        verify(goalRepositoryFacade).existsActiveGoalById(goalId);
        verify(goalRepositoryFacade, never()).deleteActiveGoal(any());
    }

    @Test
    @DisplayName("목표 삭제 - Repository에서 예외 발생")
    void deleteGoal_RepositoryException() {
        // given
        Long goalId = 1L;
        Long userId = 1L;

        doThrow(new RuntimeException("데이터베이스 오류")).when(goalRepositoryFacade)
            .existsActiveGoalById(goalId);

        // when & then
        assertThatThrownBy(() -> goalService.deleteGoal(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_DELETE_FAILED);
    }

    // ========== GET 테스트 ==========

    @Test
    @DisplayName("단일 목표 조회 성공")
    void getGoalByGoalId_Success() {
        // given
        Long goalId = 1L;
        Long userId = 1L;
        Goal existingGoal = Goal.builder()
            .goalId(1L)
            .title("조회할 목표")
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        GoalResponseDTO expectedResponse = GoalResponseDTO.builder()
            .goalId(1L)
            .title("조회할 목표")
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);
        when(goalConverter.toGoalResponseDTO(existingGoal)).thenReturn(expectedResponse);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

        // when
        GoalResponseDTO result = goalService.getGoalByGoalId(userId, goalId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("조회할 목표");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("단일 목표 조회 실패 - null goalId")
    void getGoalByGoalId_Fail_NullGoalId() {
        // given
        Long goalId = null;
        Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> goalService.getGoalByGoalId(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType",
                GoalExceptionType.GOAL_INVALID_GOAL_ID);
    }

    @Test
    @DisplayName("단일 목표 조회 실패 - 존재하지 않는 goalId")
    void getGoalByGoalId_Fail_GoalNotFound() {
        // given
        Long goalId = 999L;
        Long userId = 1L;

        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(null);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> goalService.getGoalByGoalId(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("단일 목표 조회 - Repository에서 예외 발생")
    void getfindPlanById_RepositoryException() {
        // given
        Long goalId = 1L;
        Long userId = 1L;
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.getGoalByGoalId(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_FOUND_FAILED);
    }

    @Test
    @DisplayName("사용자별 대목표 검색 목록 조회 성공 - 날짜 필터 없음")
    void getGoalsByUser_Success_NoDateFilter() {
        // given
        Long userId = 1L;
        LocalDate searchDate = null;
        String keyword = null;
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> mockGoals = Arrays.asList(
            createMockGoal(1L, "운동하기", LocalDate.now().minusDays(10), LocalDate.now().plusDays(20),
                false),
            createMockGoal(2L, "독서하기", LocalDate.now().minusDays(5), LocalDate.now().plusDays(30),
                false)
        );

        // 정렬된 순서로 expectedResponse 생성 (독서하기가 먼저)
        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(2L, "독서하기"), // ㄷ이 ㅇ보다 앞
            createMockGoalResponseDTO(1L, "운동하기")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            mockGoals);
        when(goalConverter.toGoalResponseDTO(mockGoals)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("독서하기");
        assertThat(result.get(1).getTitle()).isEqualTo("운동하기");

        verify(goalRepositoryFacade).findGoalsWithFilters(userId, keyword, searchDate);
        verify(goalConverter).toGoalSearchRequestDTO(searchDate, keyword, status);
    }

    @Test
    @DisplayName("사용자별 대목표 검색 목록 조회 성공 - 날짜 필터 있음")
    void getGoalsByUser_Success_WithDateFilter() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.of(2024, 3, 15);
        String keyword = null;
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> mockGoals = Arrays.asList(
            createMockGoal(1L, "특정 날짜 활성 목표", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31),
                false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "특정 날짜 활성 목표")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            mockGoals);
        when(goalConverter.toGoalResponseDTO(mockGoals)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("특정 날짜 활성 목표");
    }

    @Test
    @DisplayName("사용자별 대목표 검색 목록 조회 성공 - 빈 결과")
    void getGoalsByUser_Success_EmptyResult() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = "존재하지않는키워드";
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            Collections.emptyList());
        when(goalConverter.toGoalResponseDTO(Collections.emptyList())).thenReturn(
            Collections.emptyList());

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자별 대목표 검색 목록 조회 실패 - Repository에서 예외 발생")
    void getGoalsByUser_Fail_RepositoryException() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = null;
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate))
            .thenThrow(new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.getGoalsByUser(userId, searchDate, keyword, status))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_FOUND_FAILED);
    }

    @Test
    @DisplayName("키워드 검색 성공 - 제목에 키워드 포함된 Goal 반환")
    void getGoalsByUser_Success_KeywordSearch() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = "운동";
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> mockGoals = Arrays.asList(
            createMockGoal(1L, "운동하기", LocalDate.now().minusDays(10), LocalDate.now().plusDays(20),
                false),
            createMockGoal(2L, "헬스장 운동", LocalDate.now().minusDays(5), LocalDate.now().plusDays(15),
                false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "운동하기"),
            createMockGoalResponseDTO(2L, "헬스장 운동")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            mockGoals);
        when(goalConverter.toGoalResponseDTO(mockGoals)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).contains("운동");
        assertThat(result.get(1).getTitle()).contains("운동");
    }

    @Test
    @DisplayName("키워드 검색 성공 - 키워드가 빈 문자열일 때 전체 조회")
    void getGoalsByUser_Success_EmptyKeyword() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = "";
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> mockGoals = Arrays.asList(
            createMockGoal(1L, "목표1", LocalDate.now().minusDays(10), LocalDate.now().plusDays(20),
                false),
            createMockGoal(2L, "목표2", LocalDate.now().minusDays(5), LocalDate.now().plusDays(30),
                false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "목표1"),
            createMockGoalResponseDTO(2L, "목표2")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            mockGoals);
        when(goalConverter.toGoalResponseDTO(mockGoals)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("상태별 필터링 성공 - COMPLETED 상태만 조회")
    void getGoalsByUser_Success_CompletedStatus() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = null;
        String status = "COMPLETED";

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> allGoals = Arrays.asList(
            createMockGoal(1L, "완료된 목표1", LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1), true),
            createMockGoal(2L, "완료된 목표2", LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(10), true),
            createMockGoal(3L, "진행중 목표", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20), false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "완료된 목표1"),
            createMockGoalResponseDTO(2L, "완료된 목표2")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            allGoals);
        when(goalConverter.toGoalResponseDTO((List<Goal>) any())).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        // 완료된 목표만 반환되는지 확인
        verify(goalRepositoryFacade).findGoalsWithFilters(userId, keyword, searchDate);
    }

    @Test
    @DisplayName("상태별 필터링 성공 - IN_PROGRESS 상태만 조회")
    void getGoalsByUser_Success_InProgressStatus() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = null;
        String status = "IN_PROGRESS";

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> allGoals = Arrays.asList(
            createMockGoal(1L, "진행중 목표1", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20), false),
            createMockGoal(2L, "완료된 목표", LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1), true),
            createMockGoal(3L, "기간만료 목표", LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1), false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "진행중 목표1")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            allGoals);
        when(goalConverter.toGoalResponseDTO((List<Goal>) any())).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("진행중 목표1");
    }

    @Test
    @DisplayName("상태별 필터링 성공 - EXPIRED 상태만 조회")
    void getGoalsByUser_Success_ExpiredStatus() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = null;
        String status = "EXPIRED";

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> allGoals = Arrays.asList(
            createMockGoal(1L, "기간만료 목표1", LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1), false),
            createMockGoal(2L, "완료된 목표", LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(1), true),
            createMockGoal(3L, "진행중 목표", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20), false)
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "기간만료 목표1")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            allGoals);
        when(goalConverter.toGoalResponseDTO((List<Goal>) any())).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("기간만료 목표1");
    }

    @Test
    @DisplayName("정렬 검증 - endDate, 한글우선제목, goalId 순으로 정렬")
    void getGoalsByUser_Success_SortingVerification() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.now();
        String keyword = null;
        String status = null;

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> mockGoals = Arrays.asList(
            createMockGoal(3L, "ABC영어", LocalDate.now().minusDays(10), LocalDate.of(2024, 12, 31),
                false),
            createMockGoal(1L, "가나다", LocalDate.now().minusDays(5), LocalDate.of(2024, 6, 30),
                false),
            createMockGoal(2L, "나다라", LocalDate.now().minusDays(3), LocalDate.of(2024, 6, 30),
                false)
        );

        // 정렬된 순서로 ResponseDTO 생성 (endDate -> 한글우선제목 -> goalId)
        List<GoalResponseDTO> sortedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "가나다"), // 2024-06-30, 한글, goalId=1
            createMockGoalResponseDTO(2L, "나다라"), // 2024-06-30, 한글, goalId=2
            createMockGoalResponseDTO(3L, "ABC영어") // 2024-12-31, 영어
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            mockGoals);
        when(goalConverter.toGoalResponseDTO(mockGoals)).thenReturn(sortedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        // 정렬 순서 검증: endDate 우선, 그 다음 한글우선제목, 마지막 goalId
        assertThat(result.get(0).getTitle()).isEqualTo("가나다");
        assertThat(result.get(1).getTitle()).isEqualTo("나다라");
        assertThat(result.get(2).getTitle()).isEqualTo("ABC영어");
    }

    @Test
    @DisplayName("복합 검색 성공 - 날짜, 키워드, 상태 필터 모두 적용")
    void getGoalsByUser_Success_ComplexSearch() {
        // given
        Long userId = 1L;
        LocalDate searchDate = LocalDate.of(2024, 3, 15);
        String keyword = "운동";
        String status = "IN_PROGRESS";

        GoalSearchRequestDTO searchRequest = GoalSearchRequestDTO.builder()
            .searchDate(searchDate)
            .keyword(keyword)
            .status(status)
            .build();

        List<Goal> allGoals = Arrays.asList(
            createMockGoal(1L, "운동하기", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31), false),
            // 조건 만족
            createMockGoal(2L, "운동완료", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31), true),
            // 완료됨
            createMockGoal(3L, "독서하기", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31), false),
            // 키워드 불일치
            createMockGoal(4L, "운동기간만료", LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 28), false)
            // 기간 불일치
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            createMockGoalResponseDTO(1L, "운동하기")
        );

        when(goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status)).thenReturn(
            searchRequest);
        when(goalRepositoryFacade.findGoalsWithFilters(userId, keyword, searchDate)).thenReturn(
            allGoals);
        when(goalConverter.toGoalResponseDTO((List<Goal>) any())).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword,
            status);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("운동하기");

        // Repository 호출 검증
        verify(goalRepositoryFacade).findGoalsWithFilters(userId, keyword, searchDate);
        verify(goalConverter).toGoalSearchRequestDTO(searchDate, keyword, status);
    }

    @Test
    @DisplayName("소목표 생성 성공 테스트")
    void createSubGoals_Success() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        List<SubGoalResponseDTO> subGoalListResponseDTO = List.of(validSubGoalResponseDTO);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.saveSubGoals(List.of(validSubGoal))).thenReturn(
            List.of(validSubGoal));
        when(subGoalConverter.toSubGoalResponseListDTO(any()))
            .thenReturn(subGoalListResponseDTO);
        when(subGoalConverter.toSubGoalEntityList(any(), any())).thenReturn(List.of(validSubGoal));

        // when
        SubGoalCreateResponseDTO result = subGoalService.createSubGoals(1L, 1L,
            validSubGoalCreateRequestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getCreatedSubGoals()).isNotEmpty();
        for (SubGoalResponseDTO subGoalRequestDTO : result.getCreatedSubGoals()) {
            assertThat(subGoalRequestDTO.getSubGoalId()).isEqualTo(1L);
            assertThat(subGoalRequestDTO.getTitle()).isEqualTo("테스트 소목표1");
            assertThat(subGoalRequestDTO.getIsCompleted()).isEqualTo(false);
        }
    }

    @Test
    @DisplayName("소목표 생성 - GoalRepository에서 예외 발생")
    void createSubGoal_GoalsRepositoryException() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(any(), any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, validSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_CREATE_FAILED);
    }

    @Test
    @DisplayName("소목표 생성 - Goal 조회 실패")
    void createSubGoal_GoalsRepositoryNotFound() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(any(), any()))
            .thenThrow(new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND));

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, validSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("소목표 생성 - SubGoalCreateRequest.subGoals()가 비어있는 리스트인 경우")
    void createSubGoals_subGoalsIsEmpty() {
        // given
        SubGoalCreateRequestDTO invalidSubGoalCreateRequestDTO = SubGoalCreateRequestDTO
            .builder()
            .subGoals(new ArrayList<>())
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, invalidSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_CREATE_EMPTY);
    }

    @Test
    @DisplayName("소목표 생성 - SubGoalRequestDTO.title()이 Blank인 경우")
    void createSubGoals_titleIsBlank() {
        // given
        SubGoalRequestDTO invalidSubGoalRequestDTO = SubGoalRequestDTO
            .builder()
            .title("")
            .build();
        SubGoalCreateRequestDTO invalidSubGoalCreateRequestDTO = SubGoalCreateRequestDTO
            .builder()
            .subGoals(List.of(invalidSubGoalRequestDTO))
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, invalidSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_TITLE_EMPTY);
    }

    @Test
    @DisplayName("소목표 생성 - SubGoalRequestDTO.title()이 200자를 초과하는 경우")
    void createSubGoals_titleExceedsMaxLength() {
        // given
        SubGoalRequestDTO invalidSubGoalRequestDTO = SubGoalRequestDTO
            .builder()
            .title(
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")
            .build();
        SubGoalCreateRequestDTO invalidSubGoalCreateRequestDTO = SubGoalCreateRequestDTO
            .builder()
            .subGoals(List.of(invalidSubGoalRequestDTO))
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, invalidSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("소목표 생성 - SubGoalRepository 저장 실패")
    void createSubGoal_SubGoalsRepositoryException() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(subGoalRepositoryFacade.saveSubGoals(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);

        // when & then
        assertThatThrownBy(
            () -> subGoalService.createSubGoals(1L, 1L, validSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_CREATE_FAILED);
    }

    // SubGoal 전체 조회 test code
    @Test
    @DisplayName("소목표 전체 조회 성공")
    void getAllSubGoals_Success() {
        // given
        List<SubGoal> subGoals = new ArrayList<>();
        subGoals.add(validSubGoal);
        subGoals.add(validSubGoal);
        subGoals.add(validSubGoal);
        subGoals.add(validSubGoal);
        List<SubGoalResponseDTO> subGoalResponseDTOs = List.of(
            validSubGoalResponseDTO,
            validSubGoalResponseDTO,
            validSubGoalResponseDTO,
            validSubGoalResponseDTO
        );
    }
} 
