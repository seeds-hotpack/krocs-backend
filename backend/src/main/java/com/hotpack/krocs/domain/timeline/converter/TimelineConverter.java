package com.hotpack.krocs.domain.timeline.converter;

import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineItemDTO;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimelineConverter {

    public TimelineResponseDTO convertTimelineResponseDTO(List<TimelineItemDTO> plans, List<TimelineItemDTO> subgoals) {
        return TimelineResponseDTO.builder()
                .plans(plans)
                .subgoals(subgoals)
                .totalCount(plans.size() + subgoals.size())
                .planCount(plans.size())
                .subgoalCount(subgoals.size())
                .build();
    }

    public TimelineItemDTO convertPlanToTimelineItem(PlanResponseDTO plan) {
        return TimelineItemDTO.builder()
                .id(plan.getPlanId())
                .title(plan.getTitle())
                .color(plan.getColor())
                .isCompleted(plan.getIsCompleted())
                .startDateTime(plan.getStartDateTime())
                .endDateTime(plan.getEndDateTime())
                .allDay(plan.getAllDay())
                .planCategory(plan.getPlanCategory())
                .build();
    }

    public TimelineItemDTO convertSubGoalToTimelineItem(SubGoalResponseDTO subGoal) {
        return TimelineItemDTO.builder()
                .id(subGoal.getSubGoalId())
                .title(subGoal.getTitle())
                .color(subGoal.getColor())
                .isCompleted(subGoal.getIsCompleted())
                .startDateTime(subGoal.getStartDateTime())
                .endDateTime(subGoal.getEndDateTime())
                .build();
    }
}
