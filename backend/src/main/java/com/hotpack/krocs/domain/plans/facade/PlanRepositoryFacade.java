package com.hotpack.krocs.domain.plans.facade;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.domain.plans.repository.PlanRepository;
import com.hotpack.krocs.global.common.entity.Status;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanRepositoryFacade {

    private final PlanRepository planRepository;

    @Transactional
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    public Plan findActivePlanById(Long id) {
        Plan plan = planRepository.findPlanByPlanIdAndStatus(id, Status.ACTIVE);
        if (plan == null) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_PLAN_NOT_FOUND);
        }

        return plan;
    }
    
    public List<Plan> findActivePlansByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay,
        Long userId) {
        return planRepository.findPlansByDateRangeAndStatus(startOfDay, endOfDay, userId,
            Status.ACTIVE);
    }

    @Transactional
    public void deleteActivePlanByPlanId(Long planId) {
        Plan plan = findActivePlanById(planId);
        if (plan == null) {
            throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
        }

        plan.delete();
    }

}
