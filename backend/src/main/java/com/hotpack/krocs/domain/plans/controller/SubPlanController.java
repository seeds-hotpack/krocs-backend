package com.hotpack.krocs.domain.plans.controller;


import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanCreateResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanUpdateResponseDTO;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.domain.plans.service.SubPlanService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class SubPlanController {

    private final SubPlanService subPlanService;

    @Operation(summary = "소계획 생성", description = "소계획을 생성합니다.")
    @PostMapping("/plans/{planId}/subplans")
    public ApiResponse<SubPlanCreateResponseDTO> createSubPlans(
        @PathVariable @Positive(message = "{common.id.positive}") @Parameter(description = "Plan ID", example = "1") Long planId,
        @Valid @RequestBody @Parameter(description = "SubPlans", example = "{\"title\": \"소계획1\"}")
        SubPlanCreateRequestDTO requestDTO) {
        try {
            SubPlanCreateResponseDTO responseDTO = subPlanService.createSubPlans(planId,
                requestDTO);
            return ApiResponse.success(responseDTO);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
        }
    }

    @Operation(summary = "특정 plan 소계획 리스트 조회", description = "특정 plan의 소계획 리스트를 조회합니다.")
    @GetMapping("/{planId}/subplans")
    public ApiResponse<SubPlanListResponseDTO> getSubGoals(
        @PathVariable @Positive(message = "{common.id.positive}") @Parameter(description = "Plan ID", example = "1") Long planId) {
        try {
            SubPlanListResponseDTO response = subPlanService.getAllSubPlans(planId);
            return ApiResponse.success(response);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
        }
    }

    @Operation(summary = "특정 소계획 조회", description = "특정 소계획을 조회합니다")
    @GetMapping("/{planId}/subplans/{subPlanId}")
    public ApiResponse<SubPlanResponseDTO> getSubPlan(
        @PathVariable @Parameter(description = "Plan ID", example = "1") @Positive(message = "{common.id.positive}") Long planId,
        @PathVariable @Parameter(description = "SubPlan ID", example = "23") @Positive(message = "{common.id.positive}") Long subPlanId) {
        try {
            SubPlanResponseDTO response = subPlanService.getSubPlan(planId, subPlanId);
            return ApiResponse.success(response);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_READ_FAILED);
        }
    }

    @Operation(summary = "특정 소계획 수정", description = "특정 소계획을 수정합니다")
    @PatchMapping("/{subPlanId}")
    public ApiResponse<SubPlanUpdateResponseDTO> updateSubPlan(
        @PathVariable @Parameter(description = "SubPlan ID", example = "1") @Positive(message = "{common.id.positive}") Long subPlanId,
        @RequestBody @Parameter(description = "SubPlans", example = "{\"title\": \"소계획1\"}")
        SubPlanUpdateRequestDTO requestDTO) {
        try {
            SubPlanUpdateResponseDTO responseDTO = subPlanService.updateSubPlan(subPlanId,
                requestDTO);
            return ApiResponse.success(responseDTO);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_UPDATE_FAILED);
        }
    }


    @Operation(summary = "특정 소계획 삭제", description = "특정 소계획을 삭제합니다")
    @DeleteMapping("subplans/{subPlanId}")
    public ApiResponse<Void> deleteSubPlan(
        @PathVariable @Parameter(description = "SubPlan ID", example = "1") @Positive(message = "{common.id.positive}") Long subPlanId
    ) {
        try {
            subPlanService.deleteSubPlan(subPlanId);
            return ApiResponse.success();
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_DELETE_FAILED);
        }
    }
}
