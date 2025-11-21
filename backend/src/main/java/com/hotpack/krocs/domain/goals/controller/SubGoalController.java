package com.hotpack.krocs.domain.goals.controller;

import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.service.SubGoalService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SubGoal", description = "SubGoal 관련 API")
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Validated
public class SubGoalController {

    private final SubGoalService subGoalService;

    @Operation(summary = "소목표 생성", description = "소목표를 생성합니다.")
    @PostMapping("/{goalId}/subgoals")
    public ApiResponse<SubGoalCreateResponseDTO> createSubGoals(
        @Login Long userId,
        @PathVariable @Parameter(description = "Goal ID", example = "1") @Positive(message = "{common.id.positive}")
        Long goalId,
        @Valid @RequestBody @Parameter(description = "SubGoals", example = "{\"title\": \"소목표1\"}")
        SubGoalCreateRequestDTO subGoalCreateRequestDTO) {
        try {
            SubGoalCreateResponseDTO responseDTO = subGoalService.createSubGoals(userId, goalId,
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
        SubGoalListResponseDTO response = subGoalService.getAllSubGoals(userId, goalId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{goalId}/subgoals/{subGoalId}")
    public ApiResponse<SubGoalUpdateResponseDTO> updateSubGoal(
        @Login Long userId,
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @PathVariable @Positive(message = "{common.id.positive}") Long subGoalId,
        @Valid @RequestBody SubGoalUpdateRequestDTO request
    ) {
        try {
            SubGoalUpdateResponseDTO responseDTO = subGoalService.updateSubGoal(userId, goalId,
                subGoalId, request);
            return ApiResponse.success(responseDTO);
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
        }
    }

    @DeleteMapping("/{goalId}/subgoals/{subGoalId}")
    public ApiResponse<Void> deleteSubGoal(
        @Login Long userId,
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @PathVariable @Positive(message = "{common.id.positive}") Long subGoalId
    ) {
        try {
            subGoalService.deleteSubGoal(userId, goalId, subGoalId);
            return ApiResponse.success();
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
        }
    }

}
