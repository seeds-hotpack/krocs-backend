package com.hotpack.krocs.domain.plans.service;


import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanCreateResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanUpdateResponseDTO;

public interface SubPlanService {

    SubPlanCreateResponseDTO createSubPlans(Long planId, Long userId,
        SubPlanCreateRequestDTO subPlanCreateRequestDTO);

    SubPlanListResponseDTO getAllSubPlans(Long planId, Long userId);

    SubPlanUpdateResponseDTO updateSubPlan(Long subPlanId, Long planId, Long userId,
        SubPlanUpdateRequestDTO requestDTO);

    void deleteSubPlan(Long subPlanId, Long planId, Long userId);
}

