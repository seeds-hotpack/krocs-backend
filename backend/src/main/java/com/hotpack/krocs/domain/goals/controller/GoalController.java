package com.hotpack.krocs.domain.goals.controller;

import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
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

    @Operation(summary = "소목표 생성", description = "소목표를 생성합니다.")
    @PostMapping("/{goalId}/subgoals")
    public ApiResponse<SubGoalCreateResponseDTO> createSubGoals(
        @Login Long userId,
        @PathVariable @Parameter(description = "Goal ID", example = "1") @Positive(message = "{common.id.positive}")
        Long goalId,
        @Valid @RequestBody @Parameter(description = "SubGoals", example = "{\"title\": \"소목표1\"}")
        SubGoalCreateRequestDTO subGoalCreateRequestDTO) {
        try {
            // todo
            SubGoalCreateResponseDTO responseDTO = goalService.createSubGoals(userId, goalId,
                subGoalCreateRequestDTO);

            return ApiResponse.success(responseDTO);
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_CREATE_FAILED);
        }
    }

    @GetMapping("/{goalId}/subgoals")
    public ApiResponse<SubGoalListResponseDTO> getSubGoals(
        @Login Long userId,
        @PathVariable @Parameter(description = "Goal ID", example = "1") @Positive(message = "{common.id.positive}")
        Long goalId
    ) {
        SubGoalListResponseDTO response = goalService.getAllSubGoals(userId, goalId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{goalId}/subgoals/{subGoalId}")
    public ApiResponse<SubGoalResponseDTO> getSubGoal(
        @Login Long userId,
        @PathVariable @Parameter(description = "Goal ID", example = "1") @Positive(message = "{common.id.positive}")
        Long goalId,
        @PathVariable @Parameter(description = "SubGoal ID", example = "23") @Positive(message = "{common.id.positive}")
        Long subGoalId
    ) {
        SubGoalResponseDTO response = goalService.getSubGoal(userId, goalId, subGoalId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "대목표 목록 조회", description = "사용자의 대목표 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<GoalResponseDTO>> getGoal(
        @Login Long userId, @RequestParam(required = false) LocalDate date
    ) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            List<GoalResponseDTO> responseDTO = goalService.getGoalByUser(userId, date);
            return ApiResponse.success(responseDTO);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Operation(summary = "특정 목표 상세 조회", description = "특정 목표의 상세 정보를 조회합니다."
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

    @Operation(summary = "목표 수정", description = "기존 목표의 정보를 수정합니다."
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

    @Operation(summary = "목표 삭제", description = "기존 목표를 삭제합니다."
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
