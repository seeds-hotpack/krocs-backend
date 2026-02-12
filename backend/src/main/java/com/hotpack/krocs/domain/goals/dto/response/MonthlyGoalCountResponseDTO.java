package com.hotpack.krocs.domain.goals.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyGoalCountResponseDTO {

    private Integer year;
    private Integer month;

    @JsonProperty("daily_goals")
    private List<DailyGoalCountDTO> dailyGoals;
}
