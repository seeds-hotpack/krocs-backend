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
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.domain.enums.UserRole;
import com.hotpack.krocs.global.common.entity.Priority;
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
    private SubGoalConverter subGoalConverter;

    @InjectMocks
    private GoalServiceImpl goalService;

    private GoalCreateRequestDTO validRequestDTO;
    private Goal validGoal;
    private GoalCreateResponseDTO validResponseDTO;

    @Mock
    private GoalConverter goalConverter;

    //    private GoalServiceImpl goalService;
    private GoalValidator goalValidator = new GoalValidator();

    private Goal existingGoal;

    private User user;

    private SubGoalCreateRequestDTO validSubGoalCreateRequestDTO;
    private SubGoal validSubGoal;
    private SubGoalResponseDTO validSubGoalResponseDTO;
    private SubGoalRequestDTO validSubGoalRequestDTO;

    @BeforeEach
    void setUp() {

        user = User.builder()
            .name("박성열")
            .email("qkrtjdduf@example.com")
            .accountType(AccountType.LOCAL)
            .role(UserRole.USER)
            .password("암호화된비밀번호")
            .build();

        goalService = new GoalServiceImpl(subGoalRepositoryFacade, subGoalConverter,
            goalRepositoryFacade, goalConverter, goalValidator);

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

        validSubGoalResponseDTO = SubGoalResponseDTO.builder()
            .subGoalId(validSubGoal.getSubGoalId())
            .title(validSubGoal.getTitle())
            .isCompleted(validSubGoal.getIsCompleted())
            .build();

        existingGoal = Goal.builder()
            .goalId(1L)
            .title("기존 제목")  // 원래 제목
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();
    }

    // ========== CREATE 테스트 ==========

    @Test
    @DisplayName("대목표 생성 성공 테스트")
    void createGoal_Success() {
        // given
        when(goalRepositoryFacade.existsActiveGoalByTitle(validRequestDTO.getTitle())).thenReturn(
            false);
//        when(goalConverter.toEntity(validRequestDTO)).thenReturn(validGoal);
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toCreateResponseDTO(validGoal)).thenReturn(validResponseDTO);
        when(goalConverter.toEntity(eq(validRequestDTO), any(User.class))).thenReturn(
            validGoal);

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
        when(goalRepositoryFacade.existsActiveGoalByTitle(validRequestDTO.getTitle())).thenReturn(
            false);
        when(goalConverter.toEntity(eq(validRequestDTO), any(User.class))).thenReturn(
            validGoal);
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toCreateResponseDTO(any(Goal.class))).thenReturn(validResponseDTO);

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
            .userId(user.getUserId())
            .build();

        // when(goalConverter.toEntity(minimalRequest)).thenReturn(minimalGoal);
        // when(goalRepositoryFacade.saveGoal(minimalGoal)).thenReturn(minimalGoal);
        // when(goalConverter.toCreateResponseDTO(minimalGoal)).thenReturn(minimalResponse);
        when(goalConverter.toCreateResponseDTO(any(Goal.class))).thenReturn(minimalResponse);
        when(goalRepositoryFacade.saveGoal(validGoal)).thenReturn(validGoal);
        when(goalConverter.toEntity(eq(minimalRequest), any(User.class))).thenReturn(
            validGoal);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);
        when(goalRepositoryFacade.existsActiveGoalByTitleAndGoalIdNot("수정된 제목", goalId)).thenReturn(
            false);
        when(goalConverter.toGoalResponseDTO((Goal) any())).thenReturn(expectedResponse);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal)
            .thenReturn(updatedGoal);
        when(goalRepositoryFacade.existsActiveGoalByTitleAndGoalIdNot("수정된 제목", goalId)).thenReturn(
            false);
        when(goalConverter.toGoalResponseDTO(updatedGoal)).thenReturn(expectedResponse);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenThrow(
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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);

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
        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);
        when(goalRepositoryFacade.existsActiveGoalByTitleAndGoalIdNot(any(), any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.updateGoalById(goalId, updateRequest, 1L))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_UPDATE_FAILED);
    }

    // ========== DELETE 테스트 ==========

    @Test
    @DisplayName("목표 삭제 성공")
    void deleteActiveGoal_Success() {
        // given
        Long goalId = 1L;
        Long userId = 1L;

        when(goalRepositoryFacade.existsActiveGoalById(goalId)).thenReturn(true);
        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);

        // when & then
        assertThatCode(() -> goalService.deleteActiveGoal(userId, goalId))
            .doesNotThrowAnyException();

        verify(goalRepositoryFacade).existsActiveGoalById(goalId);
    }

    @Test
    @DisplayName("목표 삭제 실패 - 존재하지 않는 goalId")
    void deleteGoal_Fail_Active_GoalNotFound() {
        // given
        Long goalId = 999L;
        Long userId = 1L;

        when(goalRepositoryFacade.existsActiveGoalById(goalId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> goalService.deleteActiveGoal(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_NOT_FOUND);

        verify(goalRepositoryFacade).existsActiveGoalById(goalId);
        verify(goalRepositoryFacade, never()).deleteActiveGoal(any());
    }

    @Test
    @DisplayName("목표 삭제 - Repository에서 예외 발생")
    void deleteActiveGoal_RepositoryException() {
        // given
        Long goalId = 1L;
        Long userId = 1L;

        doThrow(new RuntimeException("데이터베이스 오류")).when(goalRepositoryFacade)
            .existsActiveGoalById(goalId);

        // when & then
        assertThatThrownBy(() -> goalService.deleteActiveGoal(userId, goalId))
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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(existingGoal);
        when(goalConverter.toGoalResponseDTO(existingGoal)).thenReturn(expectedResponse);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenReturn(null);

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

        when(goalRepositoryFacade.findActiveGoalById(goalId)).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.getGoalByGoalId(userId, goalId))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_FOUND_FAILED);
    }

    @Test
    @DisplayName("사용자별 목표 목록 조회 성공 - 날짜 필터 없음")
    void getGoalByUser_Success_NoDateFilter() {
        // given
        Long userId = 1L;
        LocalDate date = null;

        List<Goal> goalList = Arrays.asList(
            Goal.builder().goalId(1L).title("목표1").build(),
            Goal.builder().goalId(2L).title("목표2").build()
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            GoalResponseDTO.builder().goalId(1L).title("목표1").build(),
            GoalResponseDTO.builder().goalId(2L).title("목표2").build()
        );

        when(goalRepositoryFacade.findAllActiveGoals()).thenReturn(goalList);
        when(goalConverter.toGoalResponseDTO(goalList)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalByUser(userId, date);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("목표1");
        assertThat(result.get(1).getTitle()).isEqualTo("목표2");
    }

    @Test
    @DisplayName("사용자별 목표 목록 조회 성공 - 날짜 필터 있음")
    void getGoalByUser_Success_WithDateFilter() {
        // given
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 7, 25);

        List<Goal> goalList = Arrays.asList(
            Goal.builder().goalId(1L).title("현재 진행 목표").build()
        );

        List<GoalResponseDTO> expectedResponse = Arrays.asList(
            GoalResponseDTO.builder().goalId(1L).title("현재 진행 목표").build()
        );

        when(goalRepositoryFacade.findActiveGoalByDate(date)).thenReturn(goalList);
        when(goalConverter.toGoalResponseDTO(goalList)).thenReturn(expectedResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalByUser(userId, date);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("현재 진행 목표");
    }

    @Test
    @DisplayName("사용자별 목표 목록 조회 성공 - 빈 결과")
    void getGoalByUser_Success_EmptyResult() {
        // given
        Long userId = 1L;
        LocalDate date = null;
        List<Goal> emptyGoalList = Collections.emptyList();
        List<GoalResponseDTO> emptyResponse = Collections.emptyList();

        when(goalRepositoryFacade.findAllActiveGoals()).thenReturn(emptyGoalList);
        when(goalConverter.toGoalResponseDTO(emptyGoalList)).thenReturn(emptyResponse);

        // when
        List<GoalResponseDTO> result = goalService.getGoalByUser(userId, date);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자별 목표 목록 조회 - Repository에서 예외 발생")
    void getGoalByUser_RepositoryException() {
        // given
        Long userId = 1L;
        LocalDate date = null;

        when(goalRepositoryFacade.findAllActiveGoals()).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.getGoalByUser(userId, date))
            .isInstanceOf(GoalException.class)
            .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_FOUND_FAILED);
    }

    @Test
    @DisplayName("소목표 생성 성공 테스트")
    void createSubGoals_Success() {
        // given
        List<SubGoalResponseDTO> subGoalListResponseDTO = List.of(validSubGoalResponseDTO);
        ;
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.saveSubGoals(List.of(validSubGoal))).thenReturn(
            List.of(validSubGoal));
        when(subGoalConverter.toSubGoalResponseListDTO(any()))
            .thenReturn(subGoalListResponseDTO);
        when(subGoalConverter.toSubGoalEntityList(any(), any())).thenReturn(List.of(validSubGoal));

        // when
        SubGoalCreateResponseDTO result = goalService.createSubGoals(1L,
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
        when(goalRepositoryFacade.findActiveGoalById(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> goalService.createSubGoals(1L, validSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_CREATE_FAILED);
    }

    @Test
    @DisplayName("소목표 생성 - Goal 조회 실패")
    void createSubGoal_GoalsRepositoryNotFound() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(any()))
            .thenThrow(new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> goalService.createSubGoals(1L, validSubGoalCreateRequestDTO))
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
        assertThatThrownBy(() -> goalService.createSubGoals(1L, invalidSubGoalCreateRequestDTO))
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
        assertThatThrownBy(() -> goalService.createSubGoals(1L, invalidSubGoalCreateRequestDTO))
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
        assertThatThrownBy(() -> goalService.createSubGoals(1L, invalidSubGoalCreateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("소목표 생성 - SubGoalRepository 저장 실패")
    void createSubGoal_SubGoalsRepositoryException() {
        // given
        when(subGoalRepositoryFacade.saveSubGoals(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);

        // when & then
        assertThatThrownBy(() -> goalService.createSubGoals(1L, validSubGoalCreateRequestDTO))
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

        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(subGoals);
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalConverter.toSubGoalResponseListDTO(any())).thenReturn(subGoalResponseDTOs);
        // when
        SubGoalListResponseDTO subGoalListResponseDTO = goalService.getAllSubGoals(1L);

        // then
        assertThat(subGoalListResponseDTO.getSubGoals().size()).isEqualTo(4);
        assertThat(subGoalListResponseDTO.getSubGoals().getFirst().getSubGoalId()).isEqualTo(1L);
        assertThat(subGoalListResponseDTO.getSubGoals().getFirst().getIsCompleted()).isEqualTo(
            false);
        assertThat(subGoalListResponseDTO.getSubGoals().getFirst().getTitle()).isEqualTo(
            "테스트 소목표1");
    }

    @Test
    @DisplayName("소목표 전체 조회 - goalId가 null인 경우")
    void getAllSubGoals_goalIdIsNull() {
        // when & then
        assertThatThrownBy(() -> goalService.getAllSubGoals(null))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
    }

    @Test
    @DisplayName("소목표 전체 조회 - SubGoalRepository에서 조회 중 예상치 못한 오류가 발생하는 경우")
    void getAllSubGoals_SubGoalRepositoryException() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(any())).thenThrow(
            new RuntimeException());

        // when & then
        assertThatThrownBy(() -> goalService.getAllSubGoals(1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_READ_FAILED);
    }

    @Test
    @DisplayName("소목표 전체 조회 - 조회된 SubGoal이 한 건도 없는 경우")
    void getAllSubGoals_SubGoalIsNull() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(any())).thenReturn(new ArrayList<>());

        // when & then
        assertThatThrownBy(() -> goalService.getAllSubGoals(1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("소목표 단건 조회 성공")
    void getSubGoal_Success() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(
            List.of(validSubGoal));
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(1L)).thenReturn(validSubGoal);

        // when
        SubGoalResponseDTO response = goalService.getSubGoal(1L, 1L);

        // then
        assertThat(response).isEqualTo(subGoalConverter.toSubGoalResponseDTO(validSubGoal));
    }

    @Test
    @DisplayName("소목표 단건 조회 - subGoalId가 null인 경우")
    void getSubGoal_subGoalIdIsNull() {
        // when & then
        assertThatThrownBy(() -> goalService.getSubGoal(1L, null))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
    }

    @Test
    @DisplayName("소목표 단건 조회 - SubGoalRepository 조회 결과 없는 경우")
    void getSubGoal_subGoalRepositoryResultIsNull() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(
            List.of(validSubGoal));
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(1L)).thenThrow(
            new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> goalService.getSubGoal(1L, 1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("소목표 단건 조회 - 소목표가 해당 목표에 속하지 않음")
    void getSubGoal_SubGoalNotBelongToGoal() {
        // given
        when(goalRepositoryFacade.findActiveGoalById(1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(
            new ArrayList<>());
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(1L)).thenReturn(validSubGoal);

        // when & then
        assertThatThrownBy(() -> goalService.getSubGoal(1L, 1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_NOT_BELONG_TO_GOAL);
    }
} 