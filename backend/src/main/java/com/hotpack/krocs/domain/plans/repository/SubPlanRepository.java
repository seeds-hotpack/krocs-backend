package com.hotpack.krocs.domain.plans.repository;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubPlanRepository extends JpaRepository<SubPlan, Long> {

    List<SubPlan> findSubPlansByPlanAndStatus(Plan plan, Status status);

    SubPlan findSubPlansBySubPlanIdAndStatus(Long subPlanId, Status status);
    
}
