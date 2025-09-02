package com.hotpack.krocs.domain.goals.repository;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("SELECT g FROM Goal g WHERE :date BETWEEN g.startDate AND g.endDate")
    List<Goal> findByDate(@Param("date") LocalDate date);

    @Query("SELECT g FROM Goal g WHERE :date BETWEEN g.startDate AND g.endDate AND g.status = :status")
    List<Goal> findGoalByDateAndStatus(@Param("date") LocalDate date,
        @Param("status") Status status);

    Goal findGoalByGoalIdAndStatus(Long goalId, Status status);

    Goal findGoalByGoalId(Long goalId);

    boolean existsGoalByGoalIdAndStatus(Long goalId, Status status);

    boolean existsGoalByTitleAndStatus(String title, Status status);

    boolean existsByTitleAndGoalIdNot(String title, Long goalId);

    boolean existsByTitleAndGoalIdNotAndStatus(String title, Long goalId, Status status);

    List<Goal> findAllGoalsByStatus(Status status);
}
