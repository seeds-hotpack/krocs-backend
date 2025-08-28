package com.hotpack.krocs.domain.goals.facade;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.repository.GoalRepository;
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

  public Goal findGoalById(Long id) {
    return goalRepository.findById(id)
        .orElseThrow(() -> new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND));
  }

  public List<Goal> findGoalByDate(LocalDate date) {
    return goalRepository.findByDate(date);
  }

  public List<Goal> findAllGoals() {
    return goalRepository.findAll();
  }

  public Goal findGoalByGoalId(Long goalId) {
    return goalRepository.findGoalByGoalId(goalId);
  }

  public Goal findById(Long goalId) {
    return goalRepository.findGoalByGoalId(goalId);
  }

  @Transactional
  public void deleteGoal(Long goalId) {
    goalRepository.deleteById(goalId);
  }

  public boolean existsById(Long goalId) {
    return goalRepository.existsById(goalId);
  }

  public boolean existsByTitle(String title) {
    return goalRepository.existsByTitle(title);
  }
}
