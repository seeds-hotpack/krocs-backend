package com.hotpack.krocs.domain.plans.facade;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.repository.PlanRepository;
import com.hotpack.krocs.global.common.entity.Status;

import java.time.LocalDate;
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

    public Plan findActivePlanByPlanIdAndUserId(Long planId, Long userId) {
        return planRepository.findPlanByIdAndUserIdAndStatus(planId, userId,
            Status.ACTIVE);
    }

    public List<Plan> findActivePlansByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay,
        Long userId) {
        return planRepository.findPlansByDateRangeAndStatus(startOfDay, endOfDay, userId,
            Status.ACTIVE);
    }
    
    public List<Plan> findActivePlansByDate(LocalDate date, Long userId) {
        return planRepository.findPlansByDateAndStatus(date, userId, Status.ACTIVE);
    }

    @Transactional
    public void deleteActivePlanByPlanId(Long planId, Long userId) {
        Plan plan = findActivePlanByPlanIdAndUserId(planId, userId);
        if (plan == null) {
            throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
        }

        plan.delete();
    }

    public List<Plan> findActivePlansByMonth(int year, int month, Long userId) {
        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        return planRepository.findPlansByMonthAndStatus(
                startOfMonth, endOfMonth, userId, Status.ACTIVE
        );
    }
}
