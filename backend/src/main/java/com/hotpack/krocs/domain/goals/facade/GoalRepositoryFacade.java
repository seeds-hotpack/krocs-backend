package com.hotpack.krocs.domain.goals.facade;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.repository.GoalRepository;
import com.hotpack.krocs.domain.user.domain.User;
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

    public List<Goal> findActiveGoalByUserAndDate(User user, LocalDate date) {
        return goalRepository.findGoalByUserIdAndDateAndStatus(user, date, Status.ACTIVE);
    }

    public List<Goal> findAllActiveGoalsByUser(User user) {
        return goalRepository.findAllGoalsByUserAndStatus(user, Status.ACTIVE);
    }

    public Goal findActiveGoalByUserAndGoalId(User user, Long goalId) {
        return goalRepository.findGoalByUserAndGoalIdAndStatus(user, goalId, Status.ACTIVE);
    }

    @Transactional
    public void deleteActiveGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public boolean existsActiveGoalById(Long goalId) {
        return goalRepository.existsGoalByGoalIdAndStatus(goalId, Status.ACTIVE);
    }
}
