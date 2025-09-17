package com.hotpack.krocs.domain.plans.converter;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanResponseDTO;
import com.hotpack.krocs.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanConverter {

    private final SubPlanConverter subPlanConverter;

    public Plan toEntity(PlanCreateRequestDTO requestDTO) {
        LocalDateTime startDateTime = requestDTO.getStartDateTime();
        LocalDateTime endDateTime = requestDTO.getEndDateTime();

        if (Boolean.TRUE.equals(requestDTO.getAllDay())) {
            if (startDateTime != null) {
                startDateTime = startDateTime.toLocalDate().atStartOfDay();
            }

            if (endDateTime != null) {
                endDateTime = endDateTime.toLocalDate().atTime(23, 59, 59);
            }
        }

        return Plan.builder()
            .title(requestDTO.getTitle())
            .planCategory(requestDTO.getPlanCategory())
            .color(requestDTO.getColor())
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .allDay(requestDTO.getAllDay())
            .isCompleted(false)
            .build();
    }

    public Plan toEntity(PlanCreateRequestDTO requestDTO, User user) {
        LocalDateTime startDateTime = requestDTO.getStartDateTime();
        LocalDateTime endDateTime = requestDTO.getEndDateTime();

        if (Boolean.TRUE.equals(requestDTO.getAllDay())) {
            if (startDateTime != null) {
                startDateTime = startDateTime.toLocalDate().atStartOfDay();
            }

            if (endDateTime != null) {
                endDateTime = endDateTime.toLocalDate().atTime(23, 59, 59);
            }
        }

        return Plan.builder()
            .user(user)
            .planCategory(requestDTO.getPlanCategory())
            .color(requestDTO.getColor())
            .title(requestDTO.getTitle())
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .allDay(requestDTO.getAllDay())
            .isCompleted(false)
            .build();
    }

    public PlanResponseDTO toEntity(Plan plan) {
        List<SubPlanResponseDTO> subPlanResponseDTOs = plan.getSubPlans() != null ?
            plan.getSubPlans().stream()
                .map(subPlanConverter::toSubPlanResponseDTO)
                .collect(Collectors.toList()) :
            List.of();

        PlanResponseDTO.PlanResponseDTOBuilder builder = PlanResponseDTO.builder()
            .planId(plan.getPlanId())
            .title(plan.getTitle())
            .planCategory(plan.getPlanCategory())
            .color(plan.getColor())
            .subPlans(subPlanResponseDTOs)
            .startDateTime(plan.getStartDateTime())
            .endDateTime(plan.getEndDateTime())
            .allDay(plan.getAllDay())
            .isCompleted(plan.getIsCompleted())
            .completedAt(plan.getCompletedAt())
            .createdAt(plan.getCreatedAt())
            .updatedAt(plan.getUpdatedAt());

        return builder.build();
    }

    public List<PlanResponseDTO> toListPlanResponseDTO(List<Plan> plans) {
        return plans.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    public PlanUpdateRequestDTO toUpdatePlanRequestDTO(PlanUpdateRequestDTO request, Boolean allDay,
        LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return PlanUpdateRequestDTO.builder()
            .title(request.getTitle())
            .planCategory(request.getPlanCategory())
            .color(request.getColor())
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .allDay(allDay)
            .isCompleted(request.getIsCompleted())
            .build();
    }
}