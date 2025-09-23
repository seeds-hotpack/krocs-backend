package com.hotpack.krocs.domain.plans.service;

import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.MonthlyPlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PlanService {
    PlanResponseDTO createPlan(PlanCreateRequestDTO requestDTO, Long userId);

    PlanListResponseDTO getPlans(LocalDate date, Long userId);

    PlanResponseDTO getPlanById(Long planId, Long userId);

    PlanResponseDTO updatePlanById(Long planId, PlanUpdateRequestDTO request, Long userId);

    void deletePlan(Long planId, Long userId);

    MonthlyPlanResponseDTO getMonthlyPlans(int year, int month, Long userId);
}
