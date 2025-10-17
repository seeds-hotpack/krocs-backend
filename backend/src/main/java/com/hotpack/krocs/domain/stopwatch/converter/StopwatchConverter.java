package com.hotpack.krocs.domain.stopwatch.converter;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StopwatchConverter {

    public StopwatchLog toStopwatchLog(SubGoal subGoal, StopwatchCreateRequestDTO request) {
        return StopwatchLog.builder()
                .subGoal(subGoal)
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getCompletedDateTime())
                .elapsedTime(request.getElapsedTime())
                .build();
    }

    public StopwatchTimeResponseDTO toStopwatchTimeResponseDTO(StopwatchLog stopwatchLog) {
        validateStopwatchLog(stopwatchLog);

        return StopwatchTimeResponseDTO.builder()
                .startDateTime(stopwatchLog.getStartDateTime())
                .elapsedTime(stopwatchLog.getElapsedTime())
                .completedDateTime(stopwatchLog.getEndDateTime())
                .build();
    }

    public List<StopwatchTimeResponseDTO> toStopwatchTimeResponseDTOList(List<StopwatchLog> stopwatchLogs) {
        return stopwatchLogs.stream()
                .map(this::toStopwatchTimeResponseDTO)
                .collect(Collectors.toList());
    }

    private void validateStopwatchLog(StopwatchLog stopwatchLog) {
        if (stopwatchLog == null) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        if (stopwatchLog.getSubGoal() == null) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        if (stopwatchLog.getStartDateTime() == null) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        if (stopwatchLog.getEndDateTime() == null) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        if (stopwatchLog.getElapsedTime() == null || stopwatchLog.getElapsedTime().isEmpty()) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }
    }
}
