package com.hotpack.krocs.domain.goals.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubGoalResponseDTO {

    private final Long subGoalId;

    private final String title;


    @JsonProperty("is_completed")
    private final Boolean isCompleted;

    @JsonProperty("is_time_selected")
    private final Boolean isTimeSelected;

    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime startDateTime;

    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime endDateTime;
}