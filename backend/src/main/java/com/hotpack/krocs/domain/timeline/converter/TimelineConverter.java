package com.hotpack.krocs.domain.timeline.converter;

import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.plans.domain.Color;
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

    private TimelineItemDTO convertPlanToTimelineItem(PlanResponseDTO plan) {
        return TimelineItemDTO.builder()
                .id(plan.getPlanId())
                .title(plan.getTitle())
                .isCompleted(plan.getIsCompleted())
                .startDateTime(plan.getStartDateTime())
                .endDateTime(plan.getEndDateTime())
                // Plan 전용 필드들
                .color(plan.getColor())
                .allDay(plan.getAllDay())
                .planCategory(plan.getPlanCategory())
                .goalId(plan.getGoalId())
                .subGoalId(plan.getSubGoalId())
                .completedAt(plan.getCompletedAt())
                .build();
    }

    private TimelineItemDTO convertSubGoalToTimelineItem(SubGoalResponseDTO subGoal) {
        Color goalColor = getGoalColor(subGoal.getGoalId());

        return TimelineItemDTO.builder()
                .id(subGoal.getSubGoalId())
                .title(subGoal.getTitle())
                .isCompleted(subGoal.getIsCompleted())
                .startDateTime(subGoal.getStartDateTime())
                .endDateTime(subGoal.getEndDateTime())
                .color(goalColor)
                .build();
    }

    private Color getGoalColor(Long goalId) {
        // GoalService를 통해 Goal 조회 후 color 반환
        GoalResponseDTO goal = goalService.getGoalById(goalId);
        return goal.getColor();
    }
}
