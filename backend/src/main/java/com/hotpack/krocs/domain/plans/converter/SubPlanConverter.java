package com.hotpack.krocs.domain.plans.converter;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanUpdateResponseDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubPlanConverter {

    public SubPlan toEntity(Plan plan, SubPlanRequestDTO requestDTO) {
        return SubPlan.builder()
            .plan(plan)
            .title(requestDTO.getTitle())
            .build();
    }

    public List<SubPlan> toEntityList(Plan plan,
        SubPlanCreateRequestDTO subPlanCreateRequestDTO) {
        return subPlanCreateRequestDTO.getSubPlans().stream()
            .map(subPlanRequestDTO -> toEntity(plan, subPlanRequestDTO))
            .collect(Collectors.toList());
    }

    public SubPlanResponseDTO toSubPlanResponseDTO(SubPlan subPlan) {
        return SubPlanResponseDTO.builder()
            .subPlanId(subPlan.getSubPlanId())
            .title(subPlan.getTitle())
            .isCompleted(subPlan.getIsCompleted())
            .completedAt(subPlan.getCompletedAt())
            .createdAt(subPlan.getCreatedAt())
            .updatedAt(subPlan.getUpdatedAt())
            .build();
    }

    public List<SubPlanResponseDTO> toSubPlanResponseListDTO(List<SubPlan> subPlans) {
        return subPlans.stream()
            .map(this::toSubPlanResponseDTO)
            .collect(Collectors.toList());
    }

    public SubPlanUpdateResponseDTO toSubPlanUpdateResponseDTO(SubPlan subPlan) {
        return SubPlanUpdateResponseDTO.builder()
            .subPlanId(subPlan.getSubPlanId())
            .planId(subPlan.getPlan().getPlanId())
            .title(subPlan.getTitle())
            .isCompleted(subPlan.getIsCompleted())
            .completedAt(subPlan.getCompletedAt())
            .createdAt(subPlan.getCreatedAt())
            .updatedAt(subPlan.getUpdatedAt())
            .build();
    }
}