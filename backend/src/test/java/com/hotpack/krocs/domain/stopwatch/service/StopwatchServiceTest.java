package com.hotpack.krocs.domain.stopwatch.service;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.stopwatch.converter.StopwatchConverter;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import com.hotpack.krocs.domain.stopwatch.facade.StopwatchRepositoryFacade;
import com.hotpack.krocs.domain.stopwatch.validator.StopwatchValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.global.common.entity.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StopwatchServiceTest {

    @Mock
    private StopwatchRepositoryFacade stopwatchRepositoryFacade;

    @Mock
    private SubGoalRepositoryFacade subGoalRepositoryFacade;

    @Mock
    private StopwatchValidator stopwatchValidator;

    @Mock
    private StopwatchConverter stopwatchConverter;

    @InjectMocks
    private StopwatchServiceImpl stopwatchService;

    private User user;
    private Goal goal;
    private SubGoal subGoal;
    private StopwatchCreateRequestDTO validCreateRequest;
    private StopwatchLog validStopwatchLog;
    private StopwatchTimeResponseDTO validResponseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .name("테스트 유저")
                .email("test@example.com")
                .accountType(AccountType.LOCAL)
                .build();

        goal = Goal.builder()
                .goalId(1L)
                .title("테스트 목표")
                .priority(Priority.HIGH)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .isCompleted(false)
                .user(user)
                .build();

        subGoal = SubGoal.builder()
                .subGoalId(1L)
                .goal(goal)
                .title("테스트 소목표")
                .isCompleted(false)
                .build();

        validCreateRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30))
                .build();

        validStopwatchLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime("01:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 11, 30))
                .build();

        validResponseDTO = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30))
                .build();
    }

    // ========== CREATE 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 성공 테스트")
    void createStopwatch_Success() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(validCreateRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, validCreateRequest)).thenReturn(validStopwatchLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(validStopwatchLog)).thenReturn(validStopwatchLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(validStopwatchLog)).thenReturn(validResponseDTO);

        // when
        StopwatchTimeResponseDTO result = stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0));
        assertThat(result.getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result.getCompletedDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 11, 30));

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subgoalId);
        verify(stopwatchValidator).validateCreateRequest(validCreateRequest);
        verify(stopwatchConverter).toStopwatchLog(subGoal, validCreateRequest);
        verify(stopwatchRepositoryFacade).saveStopwatchLog(validStopwatchLog);
        verify(stopwatchConverter).toStopwatchTimeResponseDTO(validStopwatchLog);
    }

    @Test
    @DisplayName("스톱워치 생성 실패 - 권한 없음")
    void createStopwatch_Fail_UnauthorizedAccess() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 999L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.UNAUTHORIZED_STOPWATCH_ACCESS);

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
        verify(subGoalRepositoryFacade, never()).findActiveSubGoalBySubGoalId(any());
        verify(stopwatchValidator, never()).validateCreateRequest(any());
    }

    @Test
    @DisplayName("스톱워치 생성 실패 - 유효하지 않은 시간 데이터")
    void createStopwatch_Fail_InvalidTime() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doThrow(new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION))
                .when(stopwatchValidator).validateCreateRequest(validCreateRequest);

        // when & then
        assertThatThrownBy(() -> stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.INVALID_STOPWATCH_ACTION);

        verify(stopwatchValidator).validateCreateRequest(validCreateRequest);
        verify(stopwatchConverter, never()).toStopwatchLog(any(), any());
    }

    @Test
    @DisplayName("스톱워치 생성 실패 - Repository 저장 실패")
    void createStopwatch_Fail_RepositorySaveFailed() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(validCreateRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, validCreateRequest)).thenReturn(validStopwatchLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(validStopwatchLog))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_CREATE_FAILED);

        verify(stopwatchRepositoryFacade).saveStopwatchLog(validStopwatchLog);
    }

    @Test
    @DisplayName("스톱워치 생성 실패 - SubGoal 조회 실패")
    void createStopwatch_Fail_SubGoalNotFound() {
        // given
        Long goalId = 1L;
        Long subgoalId = 999L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId))
                .thenThrow(new RuntimeException("SubGoal을 찾을 수 없습니다"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_CREATE_FAILED);

        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subgoalId);
    }

    @Test
    @DisplayName("스톱워치 생성 성공 - 최소 경과 시간 (1초)")
    void createStopwatch_Success_MinimumElapsedTime() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        StopwatchCreateRequestDTO minTimeRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        StopwatchLog minTimeLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        StopwatchTimeResponseDTO minTimeResponse = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(minTimeRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, minTimeRequest)).thenReturn(minTimeLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(minTimeLog)).thenReturn(minTimeLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(minTimeLog)).thenReturn(minTimeResponse);

        // when
        StopwatchTimeResponseDTO result = stopwatchService.createStopwatch(goalId, subgoalId, minTimeRequest, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("00:00:01");
    }

    @Test
    @DisplayName("스톱워치 생성 성공 - 최대 경과 시간 (24시간)")
    void createStopwatch_Success_MaximumElapsedTime() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        StopwatchCreateRequestDTO maxTimeRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        StopwatchLog maxTimeLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .endDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        StopwatchTimeResponseDTO maxTimeResponse = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(maxTimeRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, maxTimeRequest)).thenReturn(maxTimeLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(maxTimeLog)).thenReturn(maxTimeLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(maxTimeLog)).thenReturn(maxTimeResponse);

        // when
        StopwatchTimeResponseDTO result = stopwatchService.createStopwatch(goalId, subgoalId, maxTimeRequest, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("24:00:00");
    }

    // ========== GET 테스트 ==========

    @Test
    @DisplayName("스톱워치 조회 성공 - 단일 항목")
    void getStopwatch_Success_SingleItem() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        List<StopwatchLog> logs = Collections.singletonList(validStopwatchLog);
        List<StopwatchTimeResponseDTO> responseDTOs = Collections.singletonList(validResponseDTO);

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        when(stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal)).thenReturn(logs);
        when(stopwatchConverter.toStopwatchTimeResponseDTOList(logs)).thenReturn(responseDTOs);

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchService.getStopwatch(goalId, subgoalId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0));
        assertThat(result.get(0).getElapsedTime()).isEqualTo("01:30:00");

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subgoalId);
        verify(stopwatchRepositoryFacade).findAllStopwatchLogs(subGoal);
        verify(stopwatchConverter).toStopwatchTimeResponseDTOList(logs);
    }

    @Test
    @DisplayName("스톱워치 조회 성공 - 여러 항목")
    void getStopwatch_Success_MultipleItems() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        StopwatchLog log1 = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0))
                .elapsedTime("00:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 9, 30))
                .build();

        StopwatchLog log2 = StopwatchLog.builder()
                .stopwatchLogId(2L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime("01:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 11, 30))
                .build();

        List<StopwatchLog> logs = Arrays.asList(log1, log2);

        StopwatchTimeResponseDTO response1 = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0))
                .elapsedTime("00:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 9, 30))
                .build();

        StopwatchTimeResponseDTO response2 = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30))
                .build();

        List<StopwatchTimeResponseDTO> responseDTOs = Arrays.asList(response1, response2);

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        when(stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal)).thenReturn(logs);
        when(stopwatchConverter.toStopwatchTimeResponseDTOList(logs)).thenReturn(responseDTOs);

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchService.getStopwatch(goalId, subgoalId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getElapsedTime()).isEqualTo("00:30:00");
        assertThat(result.get(1).getElapsedTime()).isEqualTo("01:30:00");
    }

    @Test
    @DisplayName("스톱워치 조회 성공 - 빈 목록")
    void getStopwatch_Success_EmptyList() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        List<StopwatchLog> emptyLogs = Collections.emptyList();
        List<StopwatchTimeResponseDTO> emptyResponses = Collections.emptyList();

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        when(stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal)).thenReturn(emptyLogs);
        when(stopwatchConverter.toStopwatchTimeResponseDTOList(emptyLogs)).thenReturn(emptyResponses);

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchService.getStopwatch(goalId, subgoalId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(stopwatchRepositoryFacade).findAllStopwatchLogs(subGoal);
    }

    @Test
    @DisplayName("스톱워치 조회 실패 - 권한 없음")
    void getStopwatch_Fail_UnauthorizedAccess() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 999L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> stopwatchService.getStopwatch(goalId, subgoalId, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.UNAUTHORIZED_STOPWATCH_ACCESS);

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
        verify(subGoalRepositoryFacade, never()).findActiveSubGoalBySubGoalId(any());
    }

    @Test
    @DisplayName("스톱워치 조회 실패 - SubGoal 조회 실패")
    void getStopwatch_Fail_SubGoalNotFound() {
        // given
        Long goalId = 1L;
        Long subgoalId = 999L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId))
                .thenThrow(new RuntimeException("SubGoal을 찾을 수 없습니다"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.getStopwatch(goalId, subgoalId, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_FOUND_FAILED);

        verify(subGoalRepositoryFacade).findActiveSubGoalBySubGoalId(subgoalId);
    }

    @Test
    @DisplayName("스톱워치 조회 실패 - Repository에서 예외 발생")
    void getStopwatch_Fail_RepositoryException() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        when(stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.getStopwatch(goalId, subgoalId, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_FOUND_FAILED);

        verify(stopwatchRepositoryFacade).findAllStopwatchLogs(subGoal);
    }

    // ========== VALIDATION 테스트 ==========

    @Test
    @DisplayName("사용자 접근 권한 검증 성공")
    void validateUserAccess_Success() {
        // given
        Long userId = 1L;
        Long goalId = 1L;
        Long subgoalId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);

        // when & then
        stopwatchService.validateUserAccess(userId, goalId, subgoalId);

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
    }

    @Test
    @DisplayName("사용자 접근 권한 검증 실패 - 권한 없음")
    void validateUserAccess_Fail_Unauthorized() {
        // given
        Long userId = 999L;
        Long goalId = 1L;
        Long subgoalId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> stopwatchService.validateUserAccess(userId, goalId, subgoalId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.UNAUTHORIZED_STOPWATCH_ACCESS);

        verify(subGoalRepositoryFacade).existsValidSubGoal(userId, goalId, subgoalId);
    }

    // ========== EDGE CASE 테스트 ==========

    @Test
    @DisplayName("스톱워치 생성 - Converter에서 예외 발생")
    void createStopwatch_Fail_ConverterException() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(validCreateRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, validCreateRequest))
                .thenThrow(new RuntimeException("변환 오류"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_CREATE_FAILED);

        verify(stopwatchConverter).toStopwatchLog(subGoal, validCreateRequest);
    }

    @Test
    @DisplayName("스톱워치 조회 - Converter에서 예외 발생")
    void getStopwatch_Fail_ConverterException() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        List<StopwatchLog> logs = Collections.singletonList(validStopwatchLog);

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        when(stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal)).thenReturn(logs);
        when(stopwatchConverter.toStopwatchTimeResponseDTOList(logs))
                .thenThrow(new RuntimeException("변환 오류"));

        // when & then
        assertThatThrownBy(() -> stopwatchService.getStopwatch(goalId, subgoalId, userId))
                .isInstanceOf(StopwatchException.class)
                .hasFieldOrPropertyWithValue("stopwatchExceptionType",
                        StopwatchExceptionType.STOPWATCH_FOUND_FAILED);

        verify(stopwatchConverter).toStopwatchTimeResponseDTOList(logs);
    }

    @Test
    @DisplayName("스톱워치 생성 - 다양한 시간대 테스트")
    void createStopwatch_Success_DifferentTimeZones() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        // 자정을 넘는 경우
        StopwatchCreateRequestDTO midnightRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 23, 30))
                .elapsedTime("01:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 30))
                .build();

        StopwatchLog midnightLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 23, 30))
                .elapsedTime("01:00:00")
                .endDateTime(LocalDateTime.of(2025, 10, 4, 0, 30))
                .build();

        StopwatchTimeResponseDTO midnightResponse = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 23, 30))
                .elapsedTime("01:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 30))
                .build();

        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(midnightRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, midnightRequest)).thenReturn(midnightLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(midnightLog)).thenReturn(midnightLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(midnightLog)).thenReturn(midnightResponse);

        // when
        StopwatchTimeResponseDTO result = stopwatchService.createStopwatch(goalId, subgoalId, midnightRequest, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDateTime().toLocalDate()).isEqualTo(LocalDate.of(2025, 10, 3));
        assertThat(result.getCompletedDateTime().toLocalDate()).isEqualTo(LocalDate.of(2025, 10, 4));
    }

    @Test
    @DisplayName("스톱워치 생성 - 같은 SubGoal에 여러 스톱워치 생성")
    void createStopwatch_Success_MultipleLogs() {
        // given
        Long goalId = 1L;
        Long subgoalId = 1L;
        Long userId = 1L;

        // 첫 번째 스톱워치
        when(subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId)).thenReturn(true);
        when(subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId)).thenReturn(subGoal);
        doNothing().when(stopwatchValidator).validateCreateRequest(validCreateRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, validCreateRequest)).thenReturn(validStopwatchLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(validStopwatchLog)).thenReturn(validStopwatchLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(validStopwatchLog)).thenReturn(validResponseDTO);

        // when
        StopwatchTimeResponseDTO result1 = stopwatchService.createStopwatch(goalId, subgoalId, validCreateRequest, userId);

        // 두 번째 스톱워치
        StopwatchCreateRequestDTO secondRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 16, 0))
                .build();

        StopwatchLog secondLog = StopwatchLog.builder()
                .stopwatchLogId(2L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 0))
                .elapsedTime("02:00:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 16, 0))
                .build();

        StopwatchTimeResponseDTO secondResponse = StopwatchTimeResponseDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 0))
                .elapsedTime("02:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 16, 0))
                .build();

        doNothing().when(stopwatchValidator).validateCreateRequest(secondRequest);
        when(stopwatchConverter.toStopwatchLog(subGoal, secondRequest)).thenReturn(secondLog);
        when(stopwatchRepositoryFacade.saveStopwatchLog(secondLog)).thenReturn(secondLog);
        when(stopwatchConverter.toStopwatchTimeResponseDTO(secondLog)).thenReturn(secondResponse);

        StopwatchTimeResponseDTO result2 = stopwatchService.createStopwatch(goalId, subgoalId, secondRequest, userId);

        // then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result2.getElapsedTime()).isEqualTo("02:00:00");

        verify(stopwatchRepositoryFacade, times(2)).saveStopwatchLog(any());
    }
}