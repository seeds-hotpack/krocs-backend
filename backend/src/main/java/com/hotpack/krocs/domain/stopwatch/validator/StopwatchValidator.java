package com.hotpack.krocs.domain.stopwatch.validator;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StopwatchValidator {

    public void validateCreateRequest(StopwatchCreateRequestDTO request) {
        LocalDateTime startDateTime = request.getStartDateTime();
        LocalDateTime completedDateTime = request.getCompletedDateTime();
        String elapsedTime = request.getElapsedTime();

        if (startDateTime.isAfter(completedDateTime)) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        int elapsedSeconds = convertTimeToSeconds(elapsedTime);

        if (elapsedSeconds <= 0) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        int MAX_ELAPSED_SECONDS = 24 * 60 * 60;
        if (elapsedSeconds > MAX_ELAPSED_SECONDS) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }
    }

    private int convertTimeToSeconds(String elapsedTime) {
        if (elapsedTime == null || elapsedTime.isEmpty()) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }

        try {
            String[] timeParts = elapsedTime.split(":");
            if (timeParts.length != 3) {
                throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
            }

            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            // 시간 범위 검증
            if (hours < 0 || minutes < 0 || minutes >= 60 || seconds < 0 || seconds >= 60) {
                throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
            }

            return hours * 3600 + minutes * 60 + seconds;
        } catch (NumberFormatException e) {
            throw new StopwatchException(StopwatchExceptionType.INVALID_STOPWATCH_ACTION);
        }
    }
}
