package com.hotpack.krocs.domain.goals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.entity.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class SubGoalServiceTest {

    @Mock
    private SubGoalRepositoryFacade subGoalRepositoryFacade;
    @Mock
    private GoalRepositoryFacade goalRepositoryFacade;
    @Mock
    private UserRepositoryFacade userRepositoryFacade;
    @Mock
    private SubGoalConverter subGoalConverter;

    @InjectMocks
    private SubGoalServiceImpl subGoalService;

    private SubGoalUpdateRequestDTO validSubGoalUpdateRequestDTO;
    private SubGoal validSubGoal;
    private SubGoalResponseDTO validSubGoalResponseDTO;
    private SubGoalCreateRequestDTO validSubGoalCreateRequestDTO;
    private SubGoalRequestDTO validSubGoalRequestDTO;
    private User user;
    private Goal validGoal;
    private Goal existingGoal;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .name("박성열")
            .email("qkrtjdduf@example.com")
            .accountType(AccountType.LOCAL)
            .build();

        validSubGoalRequestDTO = SubGoalRequestDTO.builder()
            .title("테스트 소목표1")
            .build();

        validSubGoalCreateRequestDTO = SubGoalCreateRequestDTO.builder()
            .subGoals(List.of(validSubGoalRequestDTO))
            .build();

        validSubGoalUpdateRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("테스트 변경 소목표 제목")
            .isCompleted(true)
            .isTimeSelected(true)
            .startDateTime(LocalDateTime.of(2025, 9, 25, 10, 9))
            .endDateTime(LocalDateTime.of(2025, 10, 25, 10, 9))
            .build();

        validGoal = Goal.builder()
            .goalId(1L)
            .title("테스트 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(365))
            .isCompleted(false)
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


    @Test
    @DisplayName("소목표 생성 성공 테스트")
    void createSubGoals_Success() {
        // given
        List<SubGoalResponseDTO> subGoalListResponseDTO = List.of(validSubGoalResponseDTO);
        ;
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.saveSubGoals(List.of(validSubGoal))).thenReturn(
            List.of(validSubGoal));
        when(subGoalConverter.toSubGoalResponseListDTO(any()))
            .thenReturn(subGoalListResponseDTO);
        when(subGoalConverter.toSubGoalEntityList(any(), any())).thenReturn(List.of(validSubGoal));

        // when
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
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
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenThrow(
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
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L))
            .thenThrow(new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND));
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

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
        when(subGoalRepositoryFacade.saveSubGoals(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);

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

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(subGoals);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);
        when(subGoalConverter.toSubGoalResponseListDTO(any())).thenReturn(subGoalResponseDTOs);
        // when
        SubGoalListResponseDTO subGoalListResponseDTO = subGoalService.getAllSubGoals(1L, 1L);

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
        assertThatThrownBy(() -> subGoalService.getAllSubGoals(1L, null))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
    }

    @Test
    @DisplayName("소목표 전체 조회 - SubGoalRepository에서 조회 중 예상치 못한 오류가 발생하는 경우")
    void getAllSubGoals_SubGoalRepositoryException() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, 1L)).thenReturn(validGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(any())).thenThrow(
            new RuntimeException());

        // when & then
        assertThatThrownBy(() -> subGoalService.getAllSubGoals(1L, 1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_READ_FAILED);
    }

    @Test
    @DisplayName("소목표 전체 조회 성공 - 해당하는 소목표가 없을 때 빈 리스트 반환")
    void getAllSubGoals_whenNoSubGoalsExist_returnsEmptyList() {
        // given
        Long goalId = 1L;

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId)).thenReturn(
            existingGoal);
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(existingGoal)).thenReturn(
            Collections.emptyList());
        when(subGoalConverter.toSubGoalResponseListDTO(Collections.emptyList())).thenReturn(
            Collections.emptyList());

        // when
        SubGoalListResponseDTO response = subGoalService.getAllSubGoals(1L, goalId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSubGoals()).isNotNull();
        assertThat(response.getSubGoals()).isEmpty();
    }

    @Test
    @DisplayName("소목표 수정 성공")
    void updateSubGoal_Success() {
        SubGoal updatedSubGoal = SubGoal.builder()
            .subGoalId(validSubGoal.getSubGoalId())
            .goal(validSubGoal.getGoal())
            .isCompleted(validSubGoalUpdateRequestDTO.getIsCompleted())
            .title(validSubGoalUpdateRequestDTO.getTitle())
            .build();

        // given
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(1L)).thenReturn(validSubGoal);
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        // when
        SubGoalUpdateResponseDTO responseDTO = subGoalService.updateSubGoal(1L, 1L, 1L,
            validSubGoalUpdateRequestDTO);

        // then
        assertThat(responseDTO.getTitle()).isEqualTo("테스트 변경 소목표 제목");
        assertThat(responseDTO.getIsCompleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("소목표 수정 - subGoalId가 null인 경우")
    void updateSubGoal_subGoalIdIsNull() {
        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, null, validSubGoalUpdateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
    }

    @Test
    @DisplayName("소목표 수정 - requestDTO.title()이 200자를 초과하는 경우")
    void updateSubGoal_titleIsTooLong() {
        // given
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title(
                "더하면열글자가되어요".repeat(21))
            .isCompleted(false)
            .build();
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("소목표 수정 - 업데이트 로직 수행 중 예상치 못한 오류 발생")
    void updateSubGoal_UnknownException() {
        // given
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(any())).thenThrow(
            new RuntimeException());
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, validSubGoalUpdateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
    }
    
    @Test
    @DisplayName("소목표 수정 - requestDTO의 isTimeSelected가 null 일때")
    void updateSubGoal_isTimeSelectedIsNull() {
        // given
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("test")
            .isCompleted(false)
            .isTimeSelected(null)
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_IS_TIME_SELECTED_IS_NULL);
    }

    @Test
    @DisplayName("소목표 수정 - requestDTO의 isTimeSelected가 false 이면서 startDateTime이 not null 일때")
    void updateSubGoal_isTimeSelectedIsFalseAndStartDateTimeIsNotNull() {
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("test")
            .isCompleted(false)
            .isTimeSelected(false)
            .startDateTime(LocalDateTime.of(2025, 9, 25, 10, 9))
            .endDateTime(LocalDateTime.of(2025, 10, 25, 10, 9))
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_START_DATETIME_INVALID);
    }

    @Test
    @DisplayName("소목표 수정 - requestDTO의 isTimeSelected가 false 이면서 endDateTime이 not null 일때")
    void updateSubGoal_isTimeSelectedIsFalseAndEndDateTimeIsNotNull() {
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("test")
            .isCompleted(false)
            .isTimeSelected(false)
            .endDateTime(LocalDateTime.of(2025, 10, 25, 10, 9))
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_END_DATETIME_INVALID);
    }

    @Test
    @DisplayName("소목표 수정 - requestDTO의 isTimeSelected가 true 이면서 startDateTime이 null 일때")
    void updateSubGoal_isTimeSelectedIsTrueAndStartDateTimeIsNull() {
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("test")
            .isCompleted(false)
            .isTimeSelected(true)
            .startDateTime(null)
            .endDateTime(LocalDateTime.of(2025, 10, 25, 10, 9))
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_START_DATETIME_IS_NULL);
    }

    @Test
    @DisplayName("소목표 수정 - requestDTO의 isTimeSelected가 true 이면서 startDateTime이 null 일때")
    void updateSubGoal_isTimeSelectedIsTrueAndEndDateTimeIsNull() {
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);
        SubGoalUpdateRequestDTO invalidRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("test")
            .isCompleted(false)
            .isTimeSelected(true)
            .startDateTime(LocalDateTime.of(2025, 10, 25, 10, 9))
            .endDateTime(null)
            .build();

        // when & then
        assertThatThrownBy(
            () -> subGoalService.updateSubGoal(1L, 1L, 1L, invalidRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_END_DATETIME_IS_NULL);
    }

    @Test
    @DisplayName("소목표 삭제 성공")
    void deleteSubGoal_Success() {
        // given
        Long subGoalId = 1L;
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        // when
        subGoalService.deleteSubGoal(1L, 1L, subGoalId);

        // then
        verify(subGoalRepositoryFacade).deleteActiveSubGoalBySubGoalId(subGoalId);
    }

    @Test
    @DisplayName("소목표 삭제 - 조회 실패")
    void deleteSubGoal_ReadFailure() {
        // given
        doThrow(new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_FOUND))
            .when(subGoalRepositoryFacade)
            .deleteActiveSubGoalBySubGoalId(any());
        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> subGoalService.deleteSubGoal(1L, 1L, 1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_NOT_FOUND);

    }

    @Test
    @DisplayName("소목표 삭제 - 예상치 못한 예외 발생")
    void deleteSubGoal_UnknownException() {

        when(subGoalRepositoryFacade.existsValidSubGoal(1L, 1L, 1L)).thenReturn(true);

        doThrow(new RuntimeException())
            .when(subGoalRepositoryFacade)
            .deleteActiveSubGoalBySubGoalId(any());

        // when & then
        assertThatThrownBy(() -> subGoalService.deleteSubGoal(1L, 1L, 1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
    }

    @Test
    @DisplayName("날짜 범위 소목표 조회 성공")
    void getSubGoalsInDateRange_Success() {
        // given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        SubGoal subGoal1 = SubGoal.builder()
                .subGoalId(1L)
                .goal(validGoal)
                .title("9월 초 소목표")
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 9, 5, 9, 0))
                .endDateTime(LocalDateTime.of(2025, 9, 5, 10, 0))
                .isTimeSelected(true)
                .build();

        SubGoal subGoal2 = SubGoal.builder()
                .subGoalId(2L)
                .goal(validGoal)
                .title("9월 중순 소목표")
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 9, 15, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 9, 15, 16, 0))
                .isTimeSelected(true)
                .build();

        List<SubGoal> subGoals = List.of(subGoal1, subGoal2);
        List<SubGoalResponseDTO> expectedResponse = List.of(
                SubGoalResponseDTO.builder()
                        .subGoalId(1L)
                        .title("9월 초 소목표")
                        .isCompleted(false)
                        .startDateTime(LocalDateTime.of(2025, 9, 5, 9, 0))
                        .endDateTime(LocalDateTime.of(2025, 9, 5, 10, 0))
                        .isTimeSelected(true)
                        .build(),
                SubGoalResponseDTO.builder()
                        .subGoalId(2L)
                        .title("9월 중순 소목표")
                        .isCompleted(false)
                        .startDateTime(LocalDateTime.of(2025, 9, 15, 14, 0))
                        .endDateTime(LocalDateTime.of(2025, 9, 15, 16, 0))
                        .isTimeSelected(true)
                        .build()
        );

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findAllActiveGoalsByUser(user)).thenReturn(List.of(validGoal));
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(subGoals);
        when(subGoalConverter.toSubGoalResponseListDTO(subGoals)).thenReturn(expectedResponse);

        // when
        List<SubGoalResponseDTO> result = subGoalService.getSubGoalsInDateRange(startDate, endDate, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("9월 초 소목표");
        assertThat(result.get(1).getTitle()).isEqualTo("9월 중순 소목표");

        verify(userRepositoryFacade).findActiveUserByUserId(1L);
        verify(goalRepositoryFacade).findAllActiveGoalsByUser(user);
        verify(subGoalRepositoryFacade).findActiveSubGoalsByGoal(validGoal);
    }

    @Test
    @DisplayName("날짜 범위 소목표 조회 성공 - 시간이 null인 소목표는 제외")
    void getSubGoalsInDateRange_Success_ExcludeNullDateTime() {
        // given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        SubGoal subGoalWithTime = SubGoal.builder()
                .subGoalId(1L)
                .goal(validGoal)
                .title("시간이 있는 소목표")
                .isCompleted(false)
                .startDateTime(LocalDateTime.of(2025, 9, 15, 14, 0))
                .endDateTime(LocalDateTime.of(2025, 9, 15, 16, 0))
                .isTimeSelected(true)
                .build();

        SubGoal subGoalWithoutTime = SubGoal.builder()
                .subGoalId(2L)
                .goal(validGoal)
                .title("시간이 없는 소목표")
                .isCompleted(false)
                .startDateTime(null)
                .endDateTime(null)
                .isTimeSelected(false)
                .build();

        List<SubGoal> allSubGoals = List.of(subGoalWithTime, subGoalWithoutTime);
        List<SubGoalResponseDTO> expectedResponse = List.of(
                SubGoalResponseDTO.builder()
                        .subGoalId(1L)
                        .title("시간이 있는 소목표")
                        .isCompleted(false)
                        .startDateTime(LocalDateTime.of(2025, 9, 15, 14, 0))
                        .endDateTime(LocalDateTime.of(2025, 9, 15, 16, 0))
                        .isTimeSelected(true)
                        .build()
        );

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findAllActiveGoalsByUser(user)).thenReturn(List.of(validGoal));
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(allSubGoals);
        when(subGoalConverter.toSubGoalResponseListDTO(List.of(subGoalWithTime))).thenReturn(expectedResponse);

        // when
        List<SubGoalResponseDTO> result = subGoalService.getSubGoalsInDateRange(startDate, endDate, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("시간이 있는 소목표");
    }

    @Test
    @DisplayName("날짜 범위 소목표 조회 성공 - 빈 리스트 반환")
    void getSubGoalsInDateRange_Success_EmptyList() {
        // given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findAllActiveGoalsByUser(user)).thenReturn(List.of(validGoal));
        when(subGoalRepositoryFacade.findActiveSubGoalsByGoal(validGoal)).thenReturn(Collections.emptyList());
        when(subGoalConverter.toSubGoalResponseListDTO(Collections.emptyList())).thenReturn(Collections.emptyList());

        // when
        List<SubGoalResponseDTO> result = subGoalService.getSubGoalsInDateRange(startDate, endDate, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("날짜 범위 소목표 조회 실패 - 사용자를 찾을 수 없음")
    void getSubGoalsInDateRange_Fail_UserNotFound() {
        // given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> subGoalService.getSubGoalsInDateRange(startDate, endDate, 1L))
                .isInstanceOf(GoalException.class)
                .hasFieldOrPropertyWithValue("goalExceptionType", GoalExceptionType.GOAL_USER_NOT_FOUND);

        verify(userRepositoryFacade).findActiveUserByUserId(1L);
        verify(goalRepositoryFacade, never()).findAllActiveGoalsByUser(any());
    }

    @Test
    @DisplayName("날짜 범위 소목표 조회 실패 - Repository에서 예외 발생")
    void getSubGoalsInDateRange_Fail_RepositoryException() {
        // given
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(goalRepositoryFacade.findAllActiveGoalsByUser(user)).thenThrow(new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> subGoalService.getSubGoalsInDateRange(startDate, endDate, 1L))
                .isInstanceOf(SubGoalException.class)
                .hasFieldOrPropertyWithValue("subGoalExceptionType", SubGoalExceptionType.SUB_GOAL_READ_FAILED);
    }
}