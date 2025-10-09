package com.hotpack.krocs.domain.stopwatch.controller;

import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchActionRequestDTO;
import com.hotpack.krocs.domain.stopwatch.dto.response.StopwatchTimeResponseDTO;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchException;
import com.hotpack.krocs.domain.stopwatch.exception.StopwatchExceptionType;
import com.hotpack.krocs.domain.stopwatch.service.StopwatchService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals/{goalId}/subgoals/{subgoalId}/stopwatch")
@Validated
@Tag(name = "Stopwatch", description = "Stopwatch 관련 API")
public class StopwatchController {

    private final StopwatchService stopwatchService;

    @Operation(summary = "스톱워치 수정", description = "기존 스톱워치의 정보를 수정합니다.")
    @PatchMapping
    public ApiResponse<StopwatchTimeResponseDTO> controlStopwatch(
            @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
            @PathVariable @Positive(message = "{common.id.positive}") Long subgoalId,
            @Valid @RequestBody StopwatchActionRequestDTO request,
            @Login Long userId
    ) {
        try {
            StopwatchTimeResponseDTO responseDTO = stopwatchService.updateStopwatch(goalId, subgoalId, request, userId);
            return ApiResponse.success(responseDTO);
        } catch (StopwatchException e) {
            throw e;
        } catch (Exception e) {
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_NOT_FOUND);
        }
    }

    @Operation(summary = "특정 스톱워치 상세 조회", description = "특정 스톱워치의 상세 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<StopwatchTimeResponseDTO> getStopwatchTime(
            @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
            @PathVariable @Positive(message = "{common.id.positive}") Long subgoalId,
            @Login Long userId
    ) {
        try {
            StopwatchTimeResponseDTO responseDTO = stopwatchService.getStopwatch(goalId, subgoalId, userId);
            return ApiResponse.success(responseDTO);
        } catch (StopwatchException e) {
            throw e;
        } catch (Exception e) {
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_NOT_FOUND);
        }
    }
}
