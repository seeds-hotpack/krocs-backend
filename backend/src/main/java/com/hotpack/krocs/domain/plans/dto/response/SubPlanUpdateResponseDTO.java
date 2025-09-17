package com.hotpack.krocs.domain.plans.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubPlanUpdateResponseDTO {

    @JsonProperty("sub_plan_id")
    private Long subPlanId;

    @JsonProperty("plan_id")
    private Long planId;

    private String title;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

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

    @Schema(
        description = "완료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime completedAt;
}
