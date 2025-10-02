package com.hotpack.krocs.domain.goals.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

import com.hotpack.krocs.global.common.entity.Color;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubGoalUpdateResponseDTO {

    @JsonProperty("sub_goal_id")
    private Long subGoalId;

    @JsonProperty("goal_id")
    private Long goalId;

    private String title;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    private Color color;

    @JsonProperty("is_time_selected")
    private final Boolean isTimeSelected;

    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime startDateTime;

    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime endDateTime;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
