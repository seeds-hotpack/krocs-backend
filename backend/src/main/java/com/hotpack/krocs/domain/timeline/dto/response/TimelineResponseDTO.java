package com.hotpack.krocs.domain.timeline.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TimelineResponseDTO {

    @JsonProperty("plans")
    private List<TimelineItemDTO> plans;

    @JsonProperty("subgoals")
    private List<TimelineItemDTO> subgoals;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("plan_count")
    private Integer planCount;

    @JsonProperty("subgoal_count")
    private Integer subgoalCount;
}
