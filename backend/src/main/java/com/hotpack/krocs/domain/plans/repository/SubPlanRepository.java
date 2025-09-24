package com.hotpack.krocs.domain.plans.repository;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubPlanRepository extends JpaRepository<SubPlan, Long> {

    List<SubPlan> findSubPlansByPlanAndStatus(Plan plan, Status status);

    SubPlan findSubPlansBySubPlanIdAndStatus(Long subPlanId, Status status);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM sub_plans s
            JOIN plans p ON s.plan_id = p.plan_id
            JOIN users u ON p.user_id = u.user_id
            WHERE s.sub_plan_id = :subPlanId
              AND p.goal_id = :planId
              AND u.user_id = :userId
              AND s.status = :status
        )
        """, nativeQuery = true)
    boolean existsValidSubPlan(
        @Param("userId") Long userId,
        @Param("planId") Long planId,
        @Param("subPlanId") Long subPlanId,
        @Param("status") Status status);

}
