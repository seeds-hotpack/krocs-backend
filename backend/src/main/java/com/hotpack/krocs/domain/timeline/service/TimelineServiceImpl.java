package com.hotpack.krocs.domain.timeline.service;

import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.service.SubGoalService;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.service.PlanService;
import com.hotpack.krocs.domain.timeline.converter.TimelineConverter;
import com.hotpack.krocs.domain.timeline.domain.TimelineType;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineItemDTO;
import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineServiceImpl implements TimelineService {

    private final PlanService planService;
    private final SubGoalService subGoalService;
    private final TimelineConverter timelineConverter;

    @Override
    public TimelineResponseDTO getTimeline(LocalDate startDate, LocalDate endDate, List<String> types, Long userId) {
        Set<TimelineType> timelineTypes = types.stream()
                .map(TimelineType::fromValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<TimelineItemDTO> plans = new ArrayList<>();
        List<TimelineItemDTO> subgoals = new ArrayList<>();

        if (timelineTypes.contains(TimelineType.PLAN)) {
            List<PlanResponseDTO> planDtos = planService.getPlansInDateRange(startDate, endDate, userId);
            plans = planDtos.stream()
                    .map(timelineConverter::convertPlanToTimelineItem)
                    .collect(Collectors.toList());
        }

        if (timelineTypes.contains(TimelineType.SUBGOAL)) {
            List<SubGoalResponseDTO> subGoalDtos = subGoalService.getSubGoalsInDateRange(startDate, endDate, userId);
            subgoals = subGoalDtos.stream()
                    .map(timelineConverter::convertSubGoalToTimelineItem)
                    .collect(Collectors.toList());
        }

        return timelineConverter.convertTimelineResponseDTO(plans, subgoals);
    }
}
