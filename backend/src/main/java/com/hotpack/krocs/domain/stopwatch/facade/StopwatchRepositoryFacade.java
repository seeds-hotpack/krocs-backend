package com.hotpack.krocs.domain.stopwatch.facade;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.repository.StopwatchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopwatchRepositoryFacade {

    private final StopwatchLogRepository stopwatchLogRepository;

    @Transactional
    public StopwatchLog saveStopwatchLog(StopwatchLog stopwatchLog) {
        return stopwatchLogRepository.save(stopwatchLog);
    }

    public List<StopwatchLog> findAllStopwatchLogs(SubGoal subGoal) {
        return stopwatchLogRepository.findAllBySubGoal(subGoal);
    }
}
