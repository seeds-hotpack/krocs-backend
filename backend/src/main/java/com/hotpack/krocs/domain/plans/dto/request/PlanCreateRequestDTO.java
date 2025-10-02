package com.hotpack.krocs.domain.plans.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCreateRequestDTO {

    @NotBlank(message = "{plan.title.notBlank}")
    @Size(max = ValidationConstants.TITLE_MAX, message = "{plan.title.size}")
    private String title;

    @JsonProperty("plan_category")
    @Builder.Default
    private PlanCategory planCategory = PlanCategory.ETC;

    @Builder.Default
    private Color color = Color.PLAN_BLUE;

    @Schema(
        description = "시작 일시",
        pattern = "yyyy-MM-dd'T'HH:mm",
        example = "2025-08-03T15:05"
    )
    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "{plan.startDate.notNull}")
    private LocalDateTime startDateTime;

    @Schema(
        description = "종료 일시",
        pattern = "yyyy-MM-dd'T'HH:mm",
        example = "2025-08-03T15:05"
    )
    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "{plan.endDate.notNull}")
    private LocalDateTime endDateTime;

    @JsonProperty("all_day")
    @Builder.Default
    private Boolean allDay = false;
}
