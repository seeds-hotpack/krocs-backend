package com.hotpack.krocs.domain.timeline.controller;

import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;
import com.hotpack.krocs.domain.timeline.exception.TimelineException;
import com.hotpack.krocs.domain.timeline.exception.TimelineExceptionType;
import com.hotpack.krocs.domain.timeline.service.TimelineService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/timeline")
@Tag(name = "Timeline", description = "Timeline 관련 API")
@Validated
public class TimelineController {

    private final TimelineService timelineService;

    @Operation(summary = "타임라인 조회", description = "타임라인을 조회합니다.")
    @GetMapping
    public ApiResponse<TimelineResponseDTO> getTimeline(
            @Login Long userId,
            @RequestParam(required = false) @Parameter(description = "시작일", example = "2025-01-01")
            LocalDate startDate,
            @RequestParam(required = false) @Parameter(description = "종료일", example = "2025-01-01")
            LocalDate endDate,
            @RequestParam(defaultValue = "plan,subgoal") List<String> types
    ) {
        try {
            TimelineResponseDTO result = timelineService.getTimeline(startDate, endDate, types, userId);
            return ApiResponse.success(result);
        } catch (TimelineException e) {
            throw e;
        } catch (Exception e) {
            throw new TimelineException(TimelineExceptionType.TIMELINE_FOUND_FAILED);
        }
    }
}
