package com.hotpack.krocs.domain.goals.controller;

import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.service.SubGoalService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subgoals")
@RequiredArgsConstructor
@Validated
public class SubGoalController {

  private final SubGoalService subGoalService;


  @PatchMapping("/{subGoalId}")
  public ApiResponse<SubGoalUpdateResponseDTO> updateSubGoal(
      @PathVariable @Positive(message = "{common.id.positive}") Long subGoalId,
      @Valid @RequestBody SubGoalUpdateRequestDTO request
  ) {
    try {
      SubGoalUpdateResponseDTO responseDTO = subGoalService.updateSubGoal(subGoalId, request);
      return ApiResponse.success(responseDTO);
    } catch (SubGoalException e) {
      throw e;
    } catch (Exception e) {
      throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
    }
  }

  @DeleteMapping("/{subGoalId}")
  public ApiResponse<Void> deleteSubGoal(
      @PathVariable @Positive(message = "{common.id.positive}") Long subGoalId
  ) {
    try {
      subGoalService.deleteSubGoal(subGoalId);
      return ApiResponse.success();
    } catch (SubGoalException e) {
      throw e;
    } catch (Exception e) {
      throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
    }
  }

}
