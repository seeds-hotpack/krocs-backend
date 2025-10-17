package com.hotpack.krocs.domain.stopwatch.converter;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.global.common.entity.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StopwatchConverterTest {

    private StopwatchConverter stopwatchConverter;

    private User user;
    private Goal goal;
    private SubGoal subGoal;
    private StopwatchCreateRequestDTO validCreateRequest;
    private StopwatchLog validStopwatchLog;

    @BeforeEach
    void setUp() {
        stopwatchConverter = new StopwatchConverter();

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
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        validStopwatchLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();
    }

    // ========== toStopwatchLog 테스트 ==========

    @Test
    @DisplayName("StopwatchCreateRequestDTO를 StopwatchLog 엔티티로 변환 성공")
    void toStopwatchLog_Success() {
        // when
        StopwatchLog result = stopwatchConverter.toStopwatchLog(subGoal, validCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSubGoal()).isEqualTo(subGoal);
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 11, 30, 0));
        assertThat(result.getStopwatchLogId()).isNull(); // 새로 생성된 엔티티는 ID가 없음
    }

    @Test
    @DisplayName("StopwatchCreateRequestDTO를 StopwatchLog로 변환 - 최소 시간")
    void toStopwatchLog_MinimumTime() {
        // given
        StopwatchCreateRequestDTO minTimeRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        // when
        StopwatchLog result = stopwatchConverter.toStopwatchLog(subGoal, minTimeRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("00:00:01");
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.getEndDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 1));
    }

    @Test
    @DisplayName("StopwatchCreateRequestDTO를 StopwatchLog로 변환 - 최대 시간 (24시간)")
    void toStopwatchLog_MaximumTime() {
        // given
        StopwatchCreateRequestDTO maxTimeRequest = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        // when
        StopwatchLog result = stopwatchConverter.toStopwatchLog(subGoal, maxTimeRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("24:00:00");
        assertThat(result.getStartDateTime().toLocalDate()).isEqualTo(LocalDate.of(2025, 10, 3));
        assertThat(result.getEndDateTime().toLocalDate()).isEqualTo(LocalDate.of(2025, 10, 4));
    }

    @Test
    @DisplayName("StopwatchCreateRequestDTO를 StopwatchLog로 변환 - 다양한 경과 시간 형식")
    void toStopwatchLog_VariousElapsedTimeFormats() {
        // given
        StopwatchCreateRequestDTO[] requests = {
                StopwatchCreateRequestDTO.builder()
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                        .elapsedTime("00:30:45")
                        .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 30, 45))
                        .build(),
                StopwatchCreateRequestDTO.builder()
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                        .elapsedTime("12:15:30")
                        .completedDateTime(LocalDateTime.of(2025, 10, 3, 22, 15, 30))
                        .build(),
                StopwatchCreateRequestDTO.builder()
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                        .elapsedTime("23:59:59")
                        .completedDateTime(LocalDateTime.of(2025, 10, 4, 9, 59, 59))
                        .build()
        };

        // when & then
        for (StopwatchCreateRequestDTO request : requests) {
            StopwatchLog result = stopwatchConverter.toStopwatchLog(subGoal, request);
            assertThat(result).isNotNull();
            assertThat(result.getElapsedTime()).isEqualTo(request.getElapsedTime());
            assertThat(result.getSubGoal()).isEqualTo(subGoal);
        }
    }

    // ========== toStopwatchTimeResponseDTO 테스트 ==========

    @Test
    @DisplayName("StopwatchLog를 StopwatchTimeResponseDTO로 변환 성공")
    void toStopwatchTimeResponseDTO_Success() {
        // when
        StopwatchTimeResponseDTO result = stopwatchConverter.toStopwatchTimeResponseDTO(validStopwatchLog);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result.getCompletedDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 11, 30, 0));
    }

    @Test
    @DisplayName("StopwatchLog를 StopwatchTimeResponseDTO로 변환 - 최소 시간")
    void toStopwatchTimeResponseDTO_MinimumTime() {
        // given
        StopwatchLog minTimeLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("00:00:01")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 1))
                .build();

        // when
        StopwatchTimeResponseDTO result = stopwatchConverter.toStopwatchTimeResponseDTO(minTimeLog);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("00:00:01");
    }

    @Test
    @DisplayName("StopwatchLog를 StopwatchTimeResponseDTO로 변환 - 최대 시간")
    void toStopwatchTimeResponseDTO_MaximumTime() {
        // given
        StopwatchLog maxTimeLog = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 0, 0, 0))
                .elapsedTime("24:00:00")
                .endDateTime(LocalDateTime.of(2025, 10, 4, 0, 0, 0))
                .build();

        // when
        StopwatchTimeResponseDTO result = stopwatchConverter.toStopwatchTimeResponseDTO(maxTimeLog);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getElapsedTime()).isEqualTo("24:00:00");
    }

    @Test
    @DisplayName("StopwatchLog를 StopwatchTimeResponseDTO로 변환 - null endDateTime")
    void toStopwatchTimeResponseDTO_NullEndDateTime() {
        // given
        StopwatchLog logWithNullEnd = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00")
                .endDateTime(null)
                .build();

        // when
        StopwatchTimeResponseDTO result = stopwatchConverter.toStopwatchTimeResponseDTO(logWithNullEnd);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result.getCompletedDateTime()).isNull();
    }

    @Test
    @DisplayName("StopwatchLog를 StopwatchTimeResponseDTO로 변환 - 다양한 날짜 시간")
    void toStopwatchTimeResponseDTO_VariousDateTimes() {
        // given
        StopwatchLog[] logs = {
                StopwatchLog.builder()
                        .stopwatchLogId(1L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                        .elapsedTime("01:00:00")
                        .endDateTime(LocalDateTime.of(2025, 1, 1, 1, 0, 0))
                        .build(),
                StopwatchLog.builder()
                        .stopwatchLogId(2L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 12, 31, 23, 0, 0))
                        .elapsedTime("02:00:00")
                        .endDateTime(LocalDateTime.of(2026, 1, 1, 1, 0, 0))
                        .build()
        };

        // when & then
        for (StopwatchLog log : logs) {
            StopwatchTimeResponseDTO result = stopwatchConverter.toStopwatchTimeResponseDTO(log);
            assertThat(result).isNotNull();
            assertThat(result.getStartDateTime()).isEqualTo(log.getStartDateTime());
            assertThat(result.getElapsedTime()).isEqualTo(log.getElapsedTime());
            assertThat(result.getCompletedDateTime()).isEqualTo(log.getEndDateTime());
        }
    }

    // ========== toStopwatchTimeResponseDTOList 테스트 ==========

    @Test
    @DisplayName("StopwatchLog 리스트를 StopwatchTimeResponseDTO 리스트로 변환 성공")
    void toStopwatchTimeResponseDTOList_Success() {
        // given
        StopwatchLog log1 = StopwatchLog.builder()
                .stopwatchLogId(1L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0, 0))
                .elapsedTime("00:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 9, 30, 0))
                .build();

        StopwatchLog log2 = StopwatchLog.builder()
                .stopwatchLogId(2L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0, 0))
                .elapsedTime("01:30:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 11, 30, 0))
                .build();

        StopwatchLog log3 = StopwatchLog.builder()
                .stopwatchLogId(3L)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 0, 0))
                .elapsedTime("02:00:00")
                .endDateTime(LocalDateTime.of(2025, 10, 3, 16, 0, 0))
                .build();

        List<StopwatchLog> logs = Arrays.asList(log1, log2, log3);

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(logs);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        assertThat(result.get(0).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 9, 0, 0));
        assertThat(result.get(0).getElapsedTime()).isEqualTo("00:30:00");

        assertThat(result.get(1).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.get(1).getElapsedTime()).isEqualTo("01:30:00");

        assertThat(result.get(2).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 14, 0, 0));
        assertThat(result.get(2).getElapsedTime()).isEqualTo("02:00:00");
    }

    @Test
    @DisplayName("빈 StopwatchLog 리스트를 빈 StopwatchTimeResponseDTO 리스트로 변환")
    void toStopwatchTimeResponseDTOList_EmptyList() {
        // given
        List<StopwatchLog> emptyLogs = Collections.emptyList();

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(emptyLogs);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("단일 StopwatchLog를 포함한 리스트 변환")
    void toStopwatchTimeResponseDTOList_SingleItem() {
        // given
        List<StopwatchLog> singleLog = Collections.singletonList(validStopwatchLog);

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(singleLog);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 10, 0, 0));
        assertThat(result.get(0).getElapsedTime()).isEqualTo("01:30:00");
        assertThat(result.get(0).getCompletedDateTime()).isEqualTo(LocalDateTime.of(2025, 10, 3, 11, 30, 0));
    }

    @Test
    @DisplayName("다양한 경과 시간을 가진 StopwatchLog 리스트 변환")
    void toStopwatchTimeResponseDTOList_VariousElapsedTimes() {
        // given
        List<StopwatchLog> logs = Arrays.asList(
                StopwatchLog.builder()
                        .stopwatchLogId(1L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0))
                        .elapsedTime("00:15:30")
                        .endDateTime(LocalDateTime.of(2025, 10, 3, 9, 15, 30))
                        .build(),
                StopwatchLog.builder()
                        .stopwatchLogId(2L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                        .elapsedTime("05:45:20")
                        .endDateTime(LocalDateTime.of(2025, 10, 3, 15, 45, 20))
                        .build(),
                StopwatchLog.builder()
                        .stopwatchLogId(3L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 16, 0))
                        .elapsedTime("23:59:59")
                        .endDateTime(LocalDateTime.of(2025, 10, 4, 15, 59, 59))
                        .build()
        );

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(logs);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getElapsedTime()).isEqualTo("00:15:30");
        assertThat(result.get(1).getElapsedTime()).isEqualTo("05:45:20");
        assertThat(result.get(2).getElapsedTime()).isEqualTo("23:59:59");
    }

    @Test
    @DisplayName("null endDateTime을 포함한 StopwatchLog 리스트 변환")
    void toStopwatchTimeResponseDTOList_WithNullEndDateTime() {
        // given
        List<StopwatchLog> logs = Arrays.asList(
                StopwatchLog.builder()
                        .stopwatchLogId(1L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0))
                        .elapsedTime("01:00:00")
                        .endDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                        .build(),
                StopwatchLog.builder()
                        .stopwatchLogId(2L)
                        .subGoal(subGoal)
                        .startDateTime(LocalDateTime.of(2025, 10, 3, 11, 0))
                        .elapsedTime("02:00:00")
                        .endDateTime(null) // null endDateTime
                        .build()
        );

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(logs);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCompletedDateTime()).isNotNull();
        assertThat(result.get(1).getCompletedDateTime()).isNull();
    }

    @Test
    @DisplayName("대량의 StopwatchLog 리스트 변환")
    void toStopwatchTimeResponseDTOList_LargeList() {
        // given
        List<StopwatchLog> largeLogs = Arrays.asList(
                createStopwatchLog(1L, "00:10:00"),
                createStopwatchLog(2L, "00:20:00"),
                createStopwatchLog(3L, "00:30:00"),
                createStopwatchLog(4L, "00:40:00"),
                createStopwatchLog(5L, "00:50:00"),
                createStopwatchLog(6L, "01:00:00"),
                createStopwatchLog(7L, "01:10:00"),
                createStopwatchLog(8L, "01:20:00"),
                createStopwatchLog(9L, "01:30:00"),
                createStopwatchLog(10L, "01:40:00")
        );

        // when
        List<StopwatchTimeResponseDTO> result = stopwatchConverter.toStopwatchTimeResponseDTOList(largeLogs);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(10);

        for (int i = 0; i < 10; i++) {
            assertThat(result.get(i)).isNotNull();
            assertThat(result.get(i).getElapsedTime()).isNotBlank();
        }
    }

    // ========== 통합 변환 테스트 ==========

    @Test
    @DisplayName("RequestDTO -> Entity -> ResponseDTO 연속 변환 테스트")
    void fullConversionFlow_Success() {
        // given
        StopwatchCreateRequestDTO request = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 30, 0))
                .elapsedTime("03:15:45")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 17, 45, 45))
                .build();

        // when
        // Step 1: RequestDTO -> Entity
        StopwatchLog log = stopwatchConverter.toStopwatchLog(subGoal, request);

        // Step 2: Entity -> ResponseDTO
        StopwatchTimeResponseDTO response = stopwatchConverter.toStopwatchTimeResponseDTO(log);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStartDateTime()).isEqualTo(request.getStartDateTime());
        assertThat(response.getElapsedTime()).isEqualTo(request.getElapsedTime());
        assertThat(response.getCompletedDateTime()).isEqualTo(request.getCompletedDateTime());
    }

    @Test
    @DisplayName("여러 RequestDTO를 Entity로 변환 후 리스트로 ResponseDTO 변환")
    void multipleConversionFlow_Success() {
        // given
        StopwatchCreateRequestDTO request1 = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 9, 0))
                .elapsedTime("01:00:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .build();

        StopwatchCreateRequestDTO request2 = StopwatchCreateRequestDTO.builder()
                .startDateTime(LocalDateTime.of(2025, 10, 3, 14, 0))
                .elapsedTime("02:30:00")
                .completedDateTime(LocalDateTime.of(2025, 10, 3, 16, 30))
                .build();

        // when
        StopwatchLog log1 = stopwatchConverter.toStopwatchLog(subGoal, request1);
        StopwatchLog log2 = stopwatchConverter.toStopwatchLog(subGoal, request2);

        List<StopwatchLog> logs = Arrays.asList(log1, log2);
        List<StopwatchTimeResponseDTO> responses = stopwatchConverter.toStopwatchTimeResponseDTOList(logs);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getElapsedTime()).isEqualTo("01:00:00");
        assertThat(responses.get(1).getElapsedTime()).isEqualTo("02:30:00");
    }

    // ========== Helper Methods ==========

    private StopwatchLog createStopwatchLog(Long id, String elapsedTime) {
        return StopwatchLog.builder()
                .stopwatchLogId(id)
                .subGoal(subGoal)
                .startDateTime(LocalDateTime.of(2025, 10, 3, 10, 0))
                .elapsedTime(elapsedTime)
                .endDateTime(LocalDateTime.of(2025, 10, 3, 11, 0))
                .build();
    }
}