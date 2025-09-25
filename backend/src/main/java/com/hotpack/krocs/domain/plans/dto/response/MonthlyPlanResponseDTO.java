package com.hotpack.krocs.domain.plans.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlyPlanResponseDTO {
    private Integer year;
    private Integer month;

    @JsonProperty("daily_plans")
    private List<DailyPlanSummaryDTO> dailyPlans;
}