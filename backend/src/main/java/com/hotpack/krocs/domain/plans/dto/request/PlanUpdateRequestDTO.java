package com.hotpack.krocs.domain.plans.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanUpdateRequestDTO {
    private String title;

    @JsonProperty("plan_category")
    private PlanCategory planCategory;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Schema(
        description = "시작 일시",
        pattern = "yyyy-MM-dd'T'HH:mm",
        example = "2025-08-03T15:05"
    )
    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @Schema(
        description = "종료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm",
        example = "2025-08-03T15:05"
    )
    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    @JsonProperty("all_day")
    private Boolean allDay;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
