package com.hotpack.krocs.domain.plans.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanResponseDTO {

    @JsonProperty("plan_id")
    private Long planId;

    @JsonProperty("sub_plans")
    private List<SubPlanResponseDTO> subPlans;

    private String title;

    @JsonProperty("plan_category")
    private PlanCategory planCategory;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Color color = Color.BLUE;

    @Schema(
        description = "시작 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @Schema(
        description = "종료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    @JsonProperty("all_day")
    private Boolean allDay;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @Schema(
        description = "완료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(
        description = "생성 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(
        description = "수정 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
