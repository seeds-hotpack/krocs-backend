package com.hotpack.krocs.domain.goals.repository;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {

    List<SubGoal> findSubGoalsByGoalAndStatus(Goal goal, Status status);

    SubGoal findSubGoalsBySubGoalIdAndStatus(Long subGoalId, Status status);

}
