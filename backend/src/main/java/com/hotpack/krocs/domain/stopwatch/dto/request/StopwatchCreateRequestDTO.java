package com.hotpack.krocs.domain.stopwatch.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StopwatchCreateRequestDTO {

    @NotBlank(message = "{stopwatch.action.notBlank}")
    private String action; // "start", "stop", "completed", "clear"

    @NotNull(message = "{stopwatch.date.startRequired}")
    @JsonProperty("start_date_time")
    @Schema(description = "Stopwatch 시작 시간", example = "2025-10-03T10:03")
    private LocalDateTime startDateTime;

    @JsonProperty("stop_date_time")
    @Schema(description = "Stopwatch 중지 시간", example = "2025-10-03T10:03")
    private LocalDateTime stopDateTime;

    @NotNull(message = "{stopwatch.date.completedRequired}")
    @JsonProperty("completed_date_time")
    @Schema(description = "Stopwatch 완료 시간", example = "2025-10-03T10:30")
    private LocalDateTime completedDateTime;

    @NotNull(message = "{stopwatch.date.clearRequired}")
    @JsonProperty("clear_date_time")
    @Schema(description = "Stopwatch 정리 시간", example = "2025-10-03T10:30")
    private LocalDateTime clearDateTime;
}
