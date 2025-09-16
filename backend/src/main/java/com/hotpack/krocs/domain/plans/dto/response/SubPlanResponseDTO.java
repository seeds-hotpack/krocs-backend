package com.hotpack.krocs.domain.plans.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubPlanResponseDTO {

    @JsonProperty("sub_plan_id")
    private final Long subPlanId;

    private final String title;

    @JsonProperty("is_completed")
    private final Boolean isCompleted;

    @Schema(
        description = "완료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        example = "2025-08-03T15:05:12"
    )
    @JsonProperty("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime completedAt;

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
