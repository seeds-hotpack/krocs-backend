package com.hotpack.krocs.domain.goals.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SubGoalRequestDTO {

    @NotBlank(message = "{subgoal.title.notBlank}")
    @Size(max = ValidationConstants.SUB_TITLE_MAX, message = "{subgoal.title.size}")
    @Schema(description = "SubGoal 제목", example = "Liquibase 이슈 해결")
    private String title;

    @JsonProperty("is_time_selected")
    @Schema(description = "SubGoal 시간 추가 여부", example = "true")
    private Boolean isTimeSelected;

    @JsonProperty("start_date_time")
    @Schema(description = "SubGoal 시작 시간", example = "2025-10-03T10:03")
    private LocalDateTime startDateTime;

    @JsonProperty("end_date_time")
    @Schema(description = "SubGoal 종료 시간", example = "2025-10-03T10:30")
    private LocalDateTime endDateTime;
}
