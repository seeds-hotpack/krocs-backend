package com.hotpack.krocs.domain.goals.controller;

import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.MonthlyGoalCountResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.service.GoalService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@Tag(name = "Goal", description = "Goal 관련 API")
@Validated
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "대목표 생성", description = "새로운 대목표를 생성합니다.")
    @PostMapping
    public ApiResponse<GoalCreateResponseDTO> createGoal(
        @Valid @RequestBody GoalCreateRequestDTO requestDTO,
        @Login Long userId
    ) {
        try {
            GoalCreateResponseDTO responseDTO = goalService.createGoal(requestDTO, userId);
            return ApiResponse.success(responseDTO);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_CREATION_FAILED);
        }
    }

    @Operation(summary = "대목표 목록 조회", description = "사용자의 대목표 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<GoalResponseDTO>> getGoal(
        @Login Long userId,
            @RequestParam(required = false) @Parameter(description = "검색일", example = "2025-01-01")
            LocalDate searchDate,
            @RequestParam(required = false) @Parameter(description = "제목 키워드", example = "운동")
            String keyword,
            @RequestParam(required = false) @Parameter(description = "상태 필터 (IN_PROGRESS/COMPLETED/EXPIRED)", example = "IN_PROGRESS")
            String status
    ) {
        try {
            List<GoalResponseDTO> result = goalService.getGoalsByUser(userId, searchDate, keyword, status);
            return ApiResponse.success(result);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Operation(summary = "월별 목표 개수 조회", description = "특정 년월의 날짜별 목표 개수를 조회합니다.")
    @GetMapping("/monthly/count")
    public ApiResponse<MonthlyGoalCountResponseDTO> getMonthlyGoalCounts(
            @Login Long userId,
            @RequestParam @Parameter(description = "년도", example = "2025") Integer year,
            @RequestParam @Parameter(description = "월", example = "9") Integer month
    ) {
        try {
            MonthlyGoalCountResponseDTO result = goalService.getMonthlyGoalCounts(year, month, userId);
            return ApiResponse.success(result);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Operation(summary = "특정 대목표 상세 조회", description = "특정 대목표의 상세 정보를 조회합니다."
    )
    @GetMapping("/{goalId}")
    public ApiResponse<GoalResponseDTO> getGoalById(
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @Login Long userId) {
        try {
            GoalResponseDTO responseDTO = goalService.getGoalByGoalId(userId, goalId);
            return ApiResponse.success(responseDTO);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Operation(summary = "대목표 수정", description = "기존 대목표의 정보를 수정합니다."
    )
    @PatchMapping("/{goalId}")
    public ApiResponse<GoalResponseDTO> updateGoalById(
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @Valid @RequestBody GoalUpdateRequestDTO request,
        @Login Long userId) {
        try {
            GoalResponseDTO responseDTO = goalService.updateGoalById(goalId, request, userId);

            return ApiResponse.success(responseDTO);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_UPDATE_FAILED);
        }
    }

    @Operation(summary = "대목표 삭제", description = "기존 대목표를 삭제합니다."
    )
    @DeleteMapping("/{goalId}")
    public ApiResponse<Void> deleteGoal(
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @Login Long userId) {
        try {
            goalService.deleteGoal(userId, goalId);
            return ApiResponse.success();
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_DELETE_FAILED);
        }
    }
}
