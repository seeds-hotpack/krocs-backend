package com.hotpack.krocs.domain.plans.facade;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.domain.plans.repository.SubPlanRepository;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubPlanRepositoryFacade {

    private final SubPlanRepository subPlanRepository;

    @Transactional
    public List<SubPlan> saveSubPlans(List<SubPlan> subPlans) {
        return subPlanRepository.saveAll(subPlans);
    }

    public List<SubPlan> findActiveSubPlansByPlan(Plan plan) {
        return subPlanRepository.findSubPlansByPlanAndStatus(plan, Status.ACTIVE);
    }

    public SubPlan findActiveSubPlanBySubPlanId(Long subPlanId) {
        SubPlan subPlan = subPlanRepository.findSubPlansBySubPlanIdAndStatus(subPlanId,
            Status.ACTIVE);
        if (subPlan == null) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_NOT_FOUND);
        }
        return subPlan;
    }

    @Transactional
    public void deleteActiveSubPlanBySubPlanId(Long subPlanId) {
        SubPlan subPlan = findActiveSubPlanBySubPlanId(subPlanId);
        if (subPlan == null) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_NOT_FOUND);
        }

        subPlan.delete();
    }


}