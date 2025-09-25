package com.hotpack.krocs.domain.goals.repository;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {

    List<SubGoal> findSubGoalsByGoalAndStatus(Goal goal, Status status);

    SubGoal findSubGoalsBySubGoalIdAndStatus(Long subGoalId, Status status);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM SubGoal s
        WHERE s.subGoalId = :subGoalId
          AND s.goal.goalId = :goalId
          AND s.goal.user.userId = :userId
          AND s.status = :status
        """)
    boolean existsValidSubGoal(
        @Param("userId") Long userId,
        @Param("goalId") Long goalId,
        @Param("subGoalId") Long subGoalId,
        @Param("status") Status status);

}
