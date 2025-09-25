package com.hotpack.krocs.domain.goals.repository;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.user.domain.User;
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

    @Query("SELECT g FROM Goal g WHERE :date BETWEEN g.startDate AND g.endDate AND g.status = :status AND g.user = :user")
    List<Goal> findGoalByUserIdAndDateAndStatus(@Param("user") User user,
        @Param("date") LocalDate date,
        @Param("status") Status status);

    Goal findGoalByUserAndGoalIdAndStatus(User user, Long goalId, Status status);

    Goal findGoalByGoalId(Long goalId);

    @Query(value = "SELECT * FROM goals g WHERE g.status = 'ACTIVE' " +
            "AND (:userId IS NULL OR g.user_id = :userId) " +
            "AND (:keyword IS NULL OR LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (CAST(:searchDate AS DATE) IS NULL OR (g.start_date <= CAST(:searchDate AS DATE) AND g.end_date >= CAST(:searchDate AS DATE)))",
            nativeQuery = true)
    List<Goal> findGoalsWithFilters(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("searchDate") LocalDate searchDate
    );

    boolean existsGoalByGoalIdAndStatus(Long goalId, Status status);

    boolean existsGoalByTitleAndStatus(String title, Status status);

    boolean existsByTitleAndGoalIdNot(String title, Long goalId);

    boolean existsByTitleAndGoalIdNotAndStatus(String title, Long goalId, Status status);

    List<Goal> findAllGoalsByUserAndStatus(User user, Status status);
}
