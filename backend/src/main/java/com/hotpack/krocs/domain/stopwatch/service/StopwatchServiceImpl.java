package com.hotpack.krocs.domain.stopwatch.service;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.stopwatch.converter.StopwatchConverter;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import com.hotpack.krocs.domain.stopwatch.facade.StopwatchRepositoryFacade;
import com.hotpack.krocs.domain.stopwatch.validator.StopwatchValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopwatchServiceImpl implements StopwatchService {

    private final StopwatchRepositoryFacade stopwatchRepositoryFacade;
    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final StopwatchValidator stopwatchValidator;
    private final StopwatchConverter stopwatchConverter;

    @Override
    @Transactional
    public StopwatchTimeResponseDTO createStopwatch(Long goalId, Long subgoalId, StopwatchCreateRequestDTO request, Long userId) {
        try {
            boolean isValid = subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId);
            if (!isValid) {
                throw new StopwatchException(StopwatchExceptionType.STOPWATCH_CREATE_FAILED);
            }

            SubGoal subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId);

            stopwatchValidator.validateCreateRequest(request);

            StopwatchLog stopwatchLog = stopwatchConverter.toStopwatchLog(subGoal, request);
            StopwatchLog savedLog = stopwatchRepositoryFacade.saveStopwatchLog(stopwatchLog);

            return stopwatchConverter.toStopwatchTimeResponseDTO(savedLog);
        } catch (SubGoalException | StopwatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("스톱워치 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_CREATE_FAILED);
        }
    }

    @Override
    public List<StopwatchTimeResponseDTO> getStopwatch(Long goalId, Long subgoalId, Long userId) {
        try {

            boolean isValid = subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId);
            if (!isValid) {
                throw new StopwatchException(StopwatchExceptionType.STOPWATCH_FOUND_FAILED);
            }
            SubGoal subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subgoalId);

            List<StopwatchLog> logs = stopwatchRepositoryFacade.findAllStopwatchLogs(subGoal);

            return stopwatchConverter.toStopwatchTimeResponseDTOList(logs);
        } catch (SubGoalException | StopwatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("스톱워치 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_FOUND_FAILED);
        }
    }

    public void validateUserAccess(Long userId, Long goalId, Long subgoalId) {
        boolean hasAccess = subGoalRepositoryFacade.existsValidSubGoal(userId, goalId, subgoalId);

        if (!hasAccess) {
            throw new StopwatchException(StopwatchExceptionType.UNAUTHORIZED_STOPWATCH_ACCESS);
        }
    }
}
