package com.hotpack.krocs.domain.goals.converter;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubGoalConverter {

    public SubGoal toSubGoalEntity(Goal goal, SubGoalRequestDTO requestDTO) {
        return SubGoal.builder()
            .goal(goal)
            .title(requestDTO.getTitle())
            .isTimeSelected(requestDTO.getIsTimeSelected())
            .startDateTime(requestDTO.getStartDateTime())
            .endDateTime(requestDTO.getEndDateTime())
            .build();
    }

    public List<SubGoal> toSubGoalEntityList(Goal goal,
        SubGoalCreateRequestDTO subGoalCreateRequestDTO) {
        List<SubGoal> subGoals = new ArrayList<>();
        for (SubGoalRequestDTO subGoalRequestDTO : subGoalCreateRequestDTO.getSubGoals()) {
            subGoals.add(toSubGoalEntity(goal, subGoalRequestDTO));
        }
        return subGoals;
    }

    public SubGoalResponseDTO toSubGoalResponseDTO(SubGoal subGoal) {
        return SubGoalResponseDTO.builder()
            .subGoalId(subGoal.getSubGoalId())
            .title(subGoal.getTitle())
            .color(subGoal.getGoal().getColor())
            .isCompleted(subGoal.getIsCompleted())
            .isTimeSelected(subGoal.isTimeSelected())
            .startDateTime(subGoal.getStartDateTime())
            .endDateTime(subGoal.getEndDateTime())
            .build();
    }

    public static SubGoalUpdateResponseDTO toSubGoalUpdateResponseDTO(SubGoal subGoal) {
        return SubGoalUpdateResponseDTO.builder()
            .subGoalId(subGoal.getSubGoalId())
            .goalId(subGoal.getGoal().getGoalId())
            .title(subGoal.getTitle())
            .color(subGoal.getGoal().getColor())
            .isCompleted(subGoal.getIsCompleted())
            .isTimeSelected(subGoal.isTimeSelected())
            .startDateTime(subGoal.getStartDateTime())
            .endDateTime(subGoal.getEndDateTime())
            .createdAt(subGoal.getCreatedAt())
            .updatedAt(subGoal.getUpdatedAt())
            .build();
    }

    public List<SubGoalResponseDTO> toSubGoalResponseListDTO(List<SubGoal> subGoals) {
        List<SubGoalResponseDTO> subGoalResponseDTOs = new ArrayList<>();
        for (SubGoal subGoal : subGoals) {
            subGoalResponseDTOs.add(toSubGoalResponseDTO(subGoal));
        }
        return subGoalResponseDTOs;
    }
}
