package com.hotpack.krocs.domain.goals.facade;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.repository.SubGoalRepository;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubGoalRepositoryFacade {

    private final SubGoalRepository subGoalRepository;

    @Transactional
    public List<SubGoal> saveSubGoals(List<SubGoal> subGoals) {
        return subGoalRepository.saveAll(subGoals);
    }

    public List<SubGoal> findActiveSubGoalsByGoal(Goal goal) {
        return subGoalRepository.findSubGoalsByGoalAndStatus(goal, Status.ACTIVE);
    }

    public SubGoal findActiveSubGoalBySubGoalId(Long subGoalId) {
        SubGoal subGoal = subGoalRepository.findSubGoalsBySubGoalIdAndStatus(subGoalId,
            Status.ACTIVE);
        if (subGoal == null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_FOUND);
        }

        return subGoal;
    }

    @Transactional
    public void deleteActiveSubGoalBySubGoalId(Long subGoalId) {
        SubGoal subGoal = findActiveSubGoalBySubGoalId(subGoalId);
        if (subGoal == null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_FOUND);
        }
        subGoal.delete();
    }

    public Goal findActiveGoalBySubGoalId(Long subGoalId) {
        SubGoal subGoal = subGoalRepository.findSubGoalsBySubGoalIdAndStatus(subGoalId,
            Status.ACTIVE);
        return subGoal.getGoal();
    }

    public boolean existsValidSubGoal(Long userId, Long goalId,
        Long subGoalId) {
        return subGoalRepository.existsValidSubGoal(
            userId, goalId, subGoalId, Status.ACTIVE);
    }
}
