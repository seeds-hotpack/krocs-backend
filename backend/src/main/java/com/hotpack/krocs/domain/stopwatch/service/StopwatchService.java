package com.hotpack.krocs.domain.stopwatch.service;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;

import java.util.List;

public interface StopwatchService {
    StopwatchTimeResponseDTO createStopwatch(Long goalId, Long subgoalId, StopwatchCreateRequestDTO request, Long userId);

    List<StopwatchTimeResponseDTO> getStopwatch(Long goalId, Long subgoalId, Long userId);
}
