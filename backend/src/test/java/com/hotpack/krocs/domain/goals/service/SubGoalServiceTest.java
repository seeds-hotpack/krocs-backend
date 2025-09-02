package com.hotpack.krocs.domain.goals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.global.common.entity.Priority;
import java.time.LocalDate;
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
    private SubGoalConverter subGoalConverter;

    @InjectMocks
    private SubGoalServiceImpl subGoalService;

    private SubGoalUpdateRequestDTO validSubGoalUpdateRequestDTO;
    private SubGoal validSubGoal;

    @BeforeEach
    void setUp() {
        validSubGoalUpdateRequestDTO = SubGoalUpdateRequestDTO.builder()
            .title("테스트 변경 소목표 제목")
            .isCompleted(true)
            .build();

        Goal validGoal = Goal.builder()
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

        // when
        SubGoalUpdateResponseDTO responseDTO = subGoalService.updateSubGoal(1L,
            validSubGoalUpdateRequestDTO);

        // then
        assertThat(responseDTO.getTitle()).isEqualTo("테스트 변경 소목표 제목");
        assertThat(responseDTO.getIsCompleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("소목표 수정 - subGoalId가 null인 경우")
    void updateSubGoal_subGoalIdIsNull() {
        // when & then
        assertThatThrownBy(() -> subGoalService.updateSubGoal(null, validSubGoalUpdateRequestDTO))
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

        // when & then
        assertThatThrownBy(() -> subGoalService.updateSubGoal(1L, invalidRequestDTO))
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

        // when & then
        assertThatThrownBy(() -> subGoalService.updateSubGoal(1L, validSubGoalUpdateRequestDTO))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
    }

    @Test
    @DisplayName("소목표 삭제 성공")
    void deleteSubGoal_Success() {
        // given
        Long subGoalId = 1L;

        // when
        subGoalService.deleteSubGoal(subGoalId);

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

        // when & then
        assertThatThrownBy(() -> subGoalService.deleteSubGoal(1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_NOT_FOUND);

    }

    @Test
    @DisplayName("소목표 삭제 - 예상치 못한 예외 발생")
    void deleteSubGoal_UnknownException() {
        doThrow(new RuntimeException())
            .when(subGoalRepositoryFacade)
            .deleteActiveSubGoalBySubGoalId(any());

        // when & then
        assertThatThrownBy(() -> subGoalService.deleteSubGoal(1L))
            .isInstanceOf(SubGoalException.class)
            .hasFieldOrPropertyWithValue("subGoalExceptionType",
                SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
    }

}