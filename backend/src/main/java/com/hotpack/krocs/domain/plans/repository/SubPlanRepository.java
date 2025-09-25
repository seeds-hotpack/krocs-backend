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
    
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM SubPlan s
        WHERE s.subPlanId = :subPlanId
          AND s.plan.planId = :planId
          AND s.plan.user.userId = :userId
          AND s.status = :status
        """)
    boolean existsValidSubPlan(
        @Param("userId") Long userId,
        @Param("planId") Long planId,
        @Param("subPlanId") Long subPlanId,
        @Param("status") Status status);

}
