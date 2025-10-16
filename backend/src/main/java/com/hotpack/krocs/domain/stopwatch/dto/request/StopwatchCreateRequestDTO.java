package com.hotpack.krocs.domain.stopwatch.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StopwatchCreateRequestDTO {

    @NotNull(message = "{stopwatch.date.startRequired}")
    @JsonProperty("start_date_time")
    @Schema(description = "Stopwatch 시작 시간", example = "2025-10-03T10:03")
    private LocalDateTime startDateTime;

    @JsonProperty("elapsed_time")
    @NotNull(message = "{stopwatch.date.elapsedRequired}")
    @Schema(description = "Stopwatch 경과 시간 (HH:MM:SS 형식)", example = "01:23:45")
    private String elapsedTime;

    @NotNull(message = "{stopwatch.date.completedRequired}")
    @JsonProperty("completed_date_time")
    @Schema(description = "Stopwatch 완료 시간", example = "2025-10-03T10:30")
    private LocalDateTime completedDateTime;
}
