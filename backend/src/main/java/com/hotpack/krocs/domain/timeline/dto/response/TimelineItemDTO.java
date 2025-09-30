package com.hotpack.krocs.domain.timeline.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimelineItemDTO {

    private Long id;
    private String title;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Builder.Default
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

    // === Plan 전용 필드 ===
    @JsonProperty("all_day")
    private Boolean allDay;

    @JsonProperty("plan_category")
    private PlanCategory planCategory;
}
