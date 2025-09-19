package com.hotpack.krocs.domain.goals.repository;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubGoalRepository extends JpaRepository<SubGoal, Long> {

    List<SubGoal> findSubGoalsByGoalAndStatus(Goal goal, Status status);

    SubGoal findSubGoalsBySubGoalIdAndStatus(Long subGoalId, Status status);

    @Query(value = """
        SELECT EXISTS (
            SELECT s
            FROM sub_goal s
            JOIN goals g ON s.goal_id = g.goal_id
            JOIN users u ON g.user_id = u.user_id
            WHERE s.sub_goal_id = :subGoalId
              AND g.goal_id = :goalId
              AND u.user_id = :userId
              AND s.status = :status
        )
        """, nativeQuery = true)
    boolean existsValidSubGoal(Long userId,
        Long goalId, Long subGoalId, Status status);

}
