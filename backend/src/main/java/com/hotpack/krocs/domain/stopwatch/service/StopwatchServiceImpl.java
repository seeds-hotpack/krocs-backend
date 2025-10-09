package com.hotpack.krocs.domain.stopwatch.service;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchActionRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopwatchServiceImpl implements StopwatchService {
    @Override
    public StopwatchTimeResponseDTO updateStopwatch(Long goalId, Long subgoalId, StopwatchActionRequestDTO request, Long userId) {
        return null;
    }

    @Override
    public StopwatchTimeResponseDTO getStopwatch(Long goalId, Long subgoalId, Long userId) {
        return null;
    }
}
