package com.hotpack.krocs.domain.goals.facade;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.repository.GoalRepository;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalRepositoryFacade {

    private final GoalRepository goalRepository;

    @Transactional
    public Goal saveGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    public List<Goal> findActiveGoalByDate(LocalDate date) {
        return goalRepository.findGoalByDateAndStatus(date, Status.ACTIVE);
    }

    public List<Goal> findAllActiveGoals() {
        return goalRepository.findAllGoalsByStatus(Status.ACTIVE);
    }

    public Goal findActiveGoalById(Long goalId) {
        return goalRepository.findGoalByGoalIdAndStatus(goalId, Status.ACTIVE);
    }

    @Transactional
    public void deleteActiveGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public boolean existsActiveGoalById(Long goalId) {
        return goalRepository.existsGoalByGoalIdAndStatus(goalId, Status.ACTIVE);
    }

    public boolean existsActiveGoalByTitleAndGoalIdNot(String title, Long goalId) {
        return goalRepository.existsByTitleAndGoalIdNotAndStatus(title, goalId, Status.ACTIVE);
    }

}
