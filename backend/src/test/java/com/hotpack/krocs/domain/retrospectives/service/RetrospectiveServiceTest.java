package com.hotpack.krocs.domain.retrospectives.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.retrospectives.converter.RetrospectiveConverter;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveMyPageResponseDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveStatisticsDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveSummaryDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import com.hotpack.krocs.domain.retrospectives.facade.RetrospectiveRepositoryFacade;
import com.hotpack.krocs.domain.retrospectives.validator.RetrospectiveValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("RetrospectiveServiceImpl 단위 테스트")
class RetrospectiveServiceTest {

    @InjectMocks
    private RetrospectiveServiceImpl retrospectiveService;

    @Mock
    private GoalRepositoryFacade goalRepository;
    @Mock
    private UserRepositoryFacade userRepository;

    @Mock
    private RetrospectiveRepositoryFacade retrospectiveRepositoryFacade;
    @Mock
    private RetrospectiveValidator retrospectiveValidator;
    @Mock
    private RetrospectiveConverter retrospectiveConverter;

    private User validUser;
    private Goal validGoal;
    private Long userId = 1L;
    private Long goalId = 10L;
    private Long retroId = 100L;

    @BeforeEach
    void setUp() {
        validUser = User.builder().userId(userId).build();
        validGoal = Goal.builder().goalId(goalId).user(validUser).build();
        Retrospective validRetro = Retrospective.builder()
            .retrospectiveId(retroId)
            .user(validUser)
            .goal(validGoal)
            .build();
    }

    @Nested
    @DisplayName("createRetrospective 성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("정상 흐름: 검증 통과 후 회고 저장 및 응답 반환")
        void createRetrospective_Success() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of("CLEAR_PLAN"))
                .build();

            Retrospective mockRetro = Retrospective.builder().build();
            RetrospectiveCreateResponseDTO expectedResponse = RetrospectiveCreateResponseDTO.builder()
                .retrospectiveId(123L).build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            when(retrospectiveConverter.toEntity(request, validUser, validGoal)).thenReturn(
                mockRetro);
            when(retrospectiveRepositoryFacade.saveRetrospective(mockRetro)).thenReturn(mockRetro);
            when(retrospectiveConverter.toCreateResponseDTO(mockRetro)).thenReturn(
                expectedResponse);

            // when
            RetrospectiveCreateResponseDTO result = retrospectiveService.createRetrospective(userId,
                goalId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRetrospectiveId()).isEqualTo(123L);
            verify(retrospectiveValidator).validateCreate(request);
            verify(retrospectiveRepositoryFacade).saveRetrospective(mockRetro);
        }

        @Test
        @DisplayName("COMPLETE_FAILURE: 회고 생성 후 목표를 완료 처리한다")
        void createRetrospective_Success_CompleteFailure() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_FAILURE")
                .factors(List.of("NO_PLAN"))
                .build();

            Retrospective mockRetro = Retrospective.builder().build();
            RetrospectiveCreateResponseDTO expectedResponse = RetrospectiveCreateResponseDTO.builder()
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            when(retrospectiveConverter.toEntity(request, validUser, validGoal)).thenReturn(
                mockRetro);
            when(retrospectiveRepositoryFacade.saveRetrospective(mockRetro)).thenReturn(mockRetro);
            when(retrospectiveConverter.toCreateResponseDTO(mockRetro)).thenReturn(
                expectedResponse);

            // when
            retrospectiveService.createRetrospective(userId, goalId, request);

            // then
            verify(retrospectiveValidator).validateCreate(request);
            verify(retrospectiveRepositoryFacade).saveRetrospective(mockRetro);
        }

        @Test
        @DisplayName("RETRY_FAILURE: 회고 생성 후 재도전")
        void createRetrospective_Success_RetryFailure() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("RETRY_FAILURE")
                .factors(List.of("BURNOUT"))
                .build();

            Retrospective mockRetro = Retrospective.builder().build();
            RetrospectiveCreateResponseDTO expectedResponse = RetrospectiveCreateResponseDTO.builder()
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            when(retrospectiveConverter.toEntity(request, validUser, validGoal)).thenReturn(
                mockRetro);
            when(retrospectiveRepositoryFacade.saveRetrospective(mockRetro)).thenReturn(mockRetro);
            when(retrospectiveConverter.toCreateResponseDTO(mockRetro)).thenReturn(
                expectedResponse);

            // when
            retrospectiveService.createRetrospective(userId, goalId, request);

            // then
            verify(retrospectiveValidator).validateCreate(request);
            verify(retrospectiveRepositoryFacade).saveRetrospective(mockRetro);
        }
    }

    @Nested
    @DisplayName("createRetrospective 실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("사용자 없음: RETRO_USER_NOT_FOUND 예외 발생")
        void createRetrospective_Fail_UserNotFound() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder().build();
            when(userRepository.findActiveUserByUserId(userId)).thenReturn(null);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_USER_NOT_FOUND);
        }

        @Test
        @DisplayName("목표 없음: RETRO_GOAL_NOT_FOUND 예외 발생")
        void createRetrospective_Fail_GoalNotFound() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder().build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(null);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_GOAL_NOT_FOUND);
        }

        @Test
        @DisplayName("검증 실패: Validator에서 예외 발생 시 그대로 전달")
        void createRetrospective_Fail_ValidatorThrows() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of("NO_PLAN"))
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            doThrow(new RetrospectiveException(
                RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME))
                .when(retrospectiveValidator).validateCreate(request);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME);
        }

        @Test
        @DisplayName("저장 실패: Repository에서 예외 발생 시 RETRO_CREATION_FAILED 발생")
        void createRetrospective_Fail_RepositorySaveFails() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of("CLEAR_PLAN"))
                .build();

            Retrospective mockRetro = Retrospective.builder().build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            when(retrospectiveConverter.toEntity(request, validUser, validGoal)).thenReturn(
                mockRetro);
            when(retrospectiveRepositoryFacade.saveRetrospective(mockRetro))
                .thenThrow(new RuntimeException("DB Connection Error"));

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_CREATION_FAILED);
        }

        @Test
        @DisplayName("검증 실패: Factor 리스트가 비어있을 경우")
        void createRetrospective_Fail_FactorListIsEmpty() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of())
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);

            doThrow(
                new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_FACTORS_SIZE))
                .when(retrospectiveValidator).validateCreate(request);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_INVALID_FACTORS_SIZE);
        }

        @Test
        @DisplayName("검증 실패: Outcome과 Factor 타입이 일치하지 않을 경우")
        void createRetrospective_Fail_FactorsMismatchOutcome() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS") // 성공 결과
                .factors(List.of("NO_PLAN")) // 실패 요인
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);

            doThrow(new RetrospectiveException(
                RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME))
                .when(retrospectiveValidator).validateCreate(request);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.createRetrospective(userId, goalId, request));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME);
        }
    }

    @Nested
    @DisplayName("deleteRetrospective 테스트")
    class DeleteCases {

        @Test
        @DisplayName("성공: 회고를 정상적으로 삭제한다")
        void deleteRetrospective_Success() {
            // given
            Retrospective mockRetro = Retrospective.builder().build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(
                validGoal);
            when(retrospectiveRepositoryFacade.findActiveRetrospectiveByUserAndRetrospectiveId(
                validUser, mockRetro.getRetrospectiveId()))
                .thenReturn(mockRetro);

            // when
            assertThatCode(() -> retrospectiveService.deleteRetrospective(userId, goalId,
                mockRetro.getRetrospectiveId()))
                .doesNotThrowAnyException();

            // then
            verify(retrospectiveValidator).validateDelete(validGoal, userId);
            verify(retrospectiveRepositoryFacade).delete(mockRetro);
        }

        @Test
        @DisplayName("실패: Goal을 찾을 수 없는 경우 RETRO_GOAL_NOT_FOUND 예외 발생")
        void deleteRetrospective_Fail_GoalNotFound() {
            // given
            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(null);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.deleteRetrospective(userId, goalId, retroId));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_GOAL_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 회고 삭제 시도 시 RETRO_NOT_FOUND 예외 발생")
        void deleteRetrospective_Fail_WhenUserIsNotOwner() {
            // given
            Long otherUserId = 999L;
            User otherUser = User.builder().userId(otherUserId).build();

            when(userRepository.findActiveUserByUserId(otherUserId)).thenReturn(otherUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(otherUser, goalId)).thenReturn(
                validGoal);

            doThrow(new RetrospectiveException(RetrospectiveExceptionType.RETRO_NOT_FOUND))
                .when(retrospectiveValidator).validateDelete(validGoal, otherUserId);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.deleteRetrospective(otherUserId, goalId, retroId));

            assertThat(ex.getErrorCode()).isEqualTo(RetrospectiveExceptionType.RETRO_NOT_FOUND);
        }


        @Test
        @DisplayName("실패: 삭제할 회고(Retrospective)를 찾을 수 없는 경우")
        void deleteRetrospective_Fail_RetrospectiveNotFound() {
            // given
            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(validGoal);

            when(retrospectiveRepositoryFacade.findActiveRetrospectiveByUserAndRetrospectiveId(validUser, 99L))
                .thenThrow(new RetrospectiveException(RetrospectiveExceptionType.RETRO_NOT_FOUND));

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.deleteRetrospective(userId, goalId, 99L));

            assertThat(ex.getErrorCode()).isEqualTo(RetrospectiveExceptionType.RETRO_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 삭제 과정에서 DB 오류 발생 시 RETRO_DELETE_FAILED 반환")
        void deleteRetrospective_Fail_WhenDatabaseErrorOnDelete() {
            // given
            Retrospective mockRetro = Retrospective.builder().goal(validGoal).build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(goalRepository.findActiveGoalByUserAndGoalId(validUser, goalId)).thenReturn(validGoal);
            when(retrospectiveRepositoryFacade.findActiveRetrospectiveByUserAndRetrospectiveId(validUser, retroId))
                .thenReturn(mockRetro);

            doThrow(new RuntimeException("DB 오류"))
                .when(retrospectiveRepositoryFacade).delete(mockRetro);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.deleteRetrospective(userId, goalId, retroId));

            assertThat(ex.getErrorCode()).isEqualTo(RetrospectiveExceptionType.RETRO_DELETE_FAILED);
        }
    }

    @Nested
    @DisplayName("getMyPageRetrospectives 테스트")
    class GetMyPageRetrospectives {

        private final Pageable pageable = PageRequest.of(0, 20);

        @Test
        @DisplayName("성공: outcome 미지정 시 전체 회고 목록과 통계 반환")
        void getMyPage_withoutOutcome_returnsAllRetros() {
            // given
            Retrospective retrospective = Retrospective.builder().retrospectiveId(1L).build();
            Page<Retrospective> retroPage = new PageImpl<>(List.of(retrospective), pageable, 1);

            RetrospectiveSummaryDTO summaryDTO = RetrospectiveSummaryDTO.builder()
                .retrospectiveId(1L)
                .goalId(goalId)
                .goalName("My Goal")
                .outcome(RetrospectiveOutcome.COMPLETE_SUCCESS)
                .content("content")
                .factors(List.of("CLEAR_PLAN"))
                .createdAt(null)
                .build();

            RetrospectiveStatisticsDTO statisticsDTO = RetrospectiveStatisticsDTO.builder()
                .topSuccessFactors(Collections.emptyList())
                .topFailureFactors(Collections.emptyList())
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(retrospectiveRepositoryFacade.findRetrospectivesByUser(validUser, pageable)).thenReturn(
                retroPage);
            when(retrospectiveConverter.toRetrospectiveSummaryDTO(any(Retrospective.class))).thenReturn(
                summaryDTO);
            when(retrospectiveRepositoryFacade.findAllActiveRetrospectivesByUser(validUser)).thenReturn(
                List.of(retrospective));
            when(retrospectiveConverter.toStatisticsDTO(anyList())).thenReturn(statisticsDTO);

            // when
            RetrospectiveMyPageResponseDTO response = retrospectiveService.getMyPageRetrospectives(
                userId, null, pageable);

            // then
            assertThat(response.getRetrospectives().getContent()).containsExactly(summaryDTO);
            assertThat(response.getStatistics()).isEqualTo(statisticsDTO);
            verify(retrospectiveRepositoryFacade).findRetrospectivesByUser(validUser, pageable);
            verify(retrospectiveRepositoryFacade, never()).findRetrospectivesByUserAndOutcome(any(),
                any(), any());
        }

        @Test
        @DisplayName("성공: outcome 파라미터 지정 시 해당 결과만 조회")
        void getMyPage_withOutcome_filtersByOutcome() {
            // given
            Pageable pageable = PageRequest.of(0, 5);
            Retrospective retrospective = Retrospective.builder().retrospectiveId(2L).build();
            Page<Retrospective> retroPage = new PageImpl<>(List.of(retrospective), pageable, 1);

            RetrospectiveSummaryDTO summaryDTO = RetrospectiveSummaryDTO.builder()
                .retrospectiveId(2L)
                .goalId(goalId)
                .goalName("Filtered Goal")
                .outcome(RetrospectiveOutcome.COMPLETE_SUCCESS)
                .content(null)
                .factors(List.of("CLEAR_PLAN"))
                .createdAt(null)
                .build();

            RetrospectiveStatisticsDTO statisticsDTO = RetrospectiveStatisticsDTO.builder()
                .topSuccessFactors(Collections.emptyList())
                .topFailureFactors(Collections.emptyList())
                .build();

            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);
            when(retrospectiveRepositoryFacade.findRetrospectivesByUserAndOutcome(validUser,
                RetrospectiveOutcome.COMPLETE_SUCCESS, pageable)).thenReturn(retroPage);
            when(retrospectiveConverter.toRetrospectiveSummaryDTO(any(Retrospective.class))).thenReturn(
                summaryDTO);
            when(retrospectiveRepositoryFacade.findAllActiveRetrospectivesByUser(validUser)).thenReturn(
                List.of(retrospective));
            when(retrospectiveConverter.toStatisticsDTO(anyList())).thenReturn(statisticsDTO);

            // when
            RetrospectiveMyPageResponseDTO response = retrospectiveService.getMyPageRetrospectives(
                userId, "COMPLETE_SUCCESS", pageable);

            // then
            assertThat(response.getRetrospectives().getContent()).containsExactly(summaryDTO);
            verify(retrospectiveRepositoryFacade).findRetrospectivesByUserAndOutcome(validUser,
                RetrospectiveOutcome.COMPLETE_SUCCESS, pageable);
            verify(retrospectiveRepositoryFacade, never()).findRetrospectivesByUser(validUser,
                pageable);
        }

        @Test
        @DisplayName("실패: outcome 값이 잘못되면 RETRO_INVALID_OUTCOME_KEY 발생")
        void getMyPage_invalidOutcome_throwsException() {
            // given
            when(userRepository.findActiveUserByUserId(userId)).thenReturn(validUser);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.getMyPageRetrospectives(userId, "INVALID", pageable));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_INVALID_OUTCOME_KEY);
        }

        @Test
        @DisplayName("실패: 사용자 정보를 찾지 못하면 RETRO_USER_NOT_FOUND 발생")
        void getMyPage_userNotFound() {
            // given
            when(userRepository.findActiveUserByUserId(userId)).thenReturn(null);

            // when & then
            RetrospectiveException ex = assertThrows(RetrospectiveException.class,
                () -> retrospectiveService.getMyPageRetrospectives(userId, null, pageable));

            assertThat(ex.getErrorCode()).isEqualTo(
                RetrospectiveExceptionType.RETRO_USER_NOT_FOUND);
        }
    }
}
