package com.hotpack.krocs.domain.stopwatch.service;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchActionRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;

public interface StopwatchService {
    StopwatchTimeResponseDTO updateStopwatch(Long goalId, Long subgoalId, StopwatchActionRequestDTO request, Long userId);

    StopwatchTimeResponseDTO getStopwatch(Long goalId, Long subgoalId, Long userId);
}
