package com.hotpack.krocs.domain.stopwatch.converter;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import org.springframework.stereotype.Component;

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
        return StopwatchTimeResponseDTO.builder()
                .startDateTime(stopwatchLog.getStartDateTime())
                .elapsedTime(stopwatchLog.getElapsedTime())
                .completedDateTime(stopwatchLog.getEndDateTime())
                .build();
    }
}
