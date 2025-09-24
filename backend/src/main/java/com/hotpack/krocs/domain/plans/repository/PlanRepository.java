package com.hotpack.krocs.domain.plans.repository;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("SELECT p FROM Plan p WHERE p.user.userId = :userId AND p.startDateTime <= :endOfDay AND p.endDateTime >= :startOfDay AND p.status = :status")
    List<Plan> findPlansByDateRangeAndStatus(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay,
        @Param("userId") Long userId, // userId 조건도 추가
        @Param("status") Status status
    );

    @Query("SELECT p FROM Plan p WHERE p.user.userId = :userId AND p.planId = :planId AND p.status = :status")
    Plan findPlanByIdAndUserIdAndStatus(@Param("planId") Long planId, @Param("userId") Long userId,
        @Param("status") Status status);
}