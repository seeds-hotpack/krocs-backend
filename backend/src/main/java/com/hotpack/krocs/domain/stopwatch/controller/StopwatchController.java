package com.hotpack.krocs.domain.stopwatch.controller;

import com.hotpack.krocs.domain.stopwatch.dto.request.StopwatchCreateRequestDTO;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals/{goalId}/subgoals/{subgoalId}/stopwatch")
@Validated
@Tag(name = "Stopwatch", description = "Stopwatch 관련 API")
public class StopwatchController {

    private final StopwatchService stopwatchService;

    @Operation(summary = "스톱워치 생성", description = "스톱워치를 생성합니다.")
    @PostMapping
    public ApiResponse<StopwatchTimeResponseDTO> createStopwatch(
            @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
            @PathVariable @Positive(message = "{common.id.positive}") Long subgoalId,
            @Valid @RequestBody StopwatchCreateRequestDTO request,
            @Login Long userId
    ) {
        try {
            StopwatchTimeResponseDTO responseDTO = stopwatchService.createStopwatch(goalId, subgoalId, request, userId);
            return ApiResponse.success(responseDTO);
        } catch (StopwatchException e) {
            throw e;
        } catch (Exception e) {
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_CREATE_FAILED);
        }
    }

    @Operation(summary = "특정 Subgoal의 모든 스톱워치 조회", description = "특정 Subgoal에 속한 모든 스톱워치 기록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<StopwatchTimeResponseDTO>> getStopwatchTime(
            @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
            @PathVariable @Positive(message = "{common.id.positive}") Long subgoalId,
            @Login Long userId
    ) {
        try {
            List<StopwatchTimeResponseDTO> responseDTO = stopwatchService.getStopwatch(goalId, subgoalId, userId);
            return ApiResponse.success(responseDTO);
        } catch (StopwatchException e) {
            throw e;
        } catch (Exception e) {
            throw new StopwatchException(StopwatchExceptionType.STOPWATCH_FOUND_FAILED);
        }
    }
}
