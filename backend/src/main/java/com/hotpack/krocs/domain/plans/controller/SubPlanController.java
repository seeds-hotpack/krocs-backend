package com.hotpack.krocs.domain.plans.controller;


import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanCreateResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanUpdateResponseDTO;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.domain.plans.service.SubPlanService;
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

@Tag(name = "SubPlan", description = "SubPlan 관련 API")
@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Validated
public class SubPlanController {

    private final SubPlanService subPlanService;

    @Operation(summary = "소계획 생성", description = "소계획을 생성합니다.")
    @PostMapping("/{planId}/subplans")
    public ApiResponse<SubPlanCreateResponseDTO> createSubPlans(
        @Login Long userId,
        @PathVariable @Positive(message = "{common.id.positive}") @Parameter(description = "Plan ID", example = "1") Long planId,
        @Valid @RequestBody @Parameter(description = "SubPlans", example = "{\"title\": \"소계획1\"}")
        SubPlanCreateRequestDTO requestDTO) {
        try {
            SubPlanCreateResponseDTO responseDTO = subPlanService.createSubPlans(planId, userId,
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
        @Login Long userId,
        @PathVariable @Positive(message = "{common.id.positive}") @Parameter(description = "Plan ID", example = "1") Long planId) {
        try {
            SubPlanListResponseDTO response = subPlanService.getAllSubPlans(planId, userId);
            return ApiResponse.success(response);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
        }
    }

    @Operation(summary = "특정 소계획 수정", description = "특정 소계획을 수정합니다")
    @PatchMapping("/{planId}/subplans/{subPlanId}")
    public ApiResponse<SubPlanUpdateResponseDTO> updateSubPlan(
        @Login Long userId,
        @PathVariable @Parameter(description = "Plan ID", example = "1") @Positive(message = "{common.id.positive}") Long planId,
        @PathVariable @Parameter(description = "SubPlan ID", example = "1") @Positive(message = "{common.id.positive}") Long subPlanId,
        @RequestBody @Parameter(description = "SubPlans", example = "{\"title\": \"소계획1\"}")
        SubPlanUpdateRequestDTO requestDTO) {
        try {
            SubPlanUpdateResponseDTO responseDTO = subPlanService.updateSubPlan(subPlanId, planId,
                userId, requestDTO);
            return ApiResponse.success(responseDTO);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_UPDATE_FAILED);
        }
    }


    @Operation(summary = "특정 소계획 삭제", description = "특정 소계획을 삭제합니다")
    @DeleteMapping("/{planId}/subplans/{subPlanId}")
    public ApiResponse<Void> deleteSubPlan(
        @Login Long userId,
        @PathVariable @Parameter(description = "Plan ID", example = "1") @Positive(message = "{common.id.positive}") Long planId,
        @PathVariable @Parameter(description = "SubPlan ID", example = "1") @Positive(message = "{common.id.positive}") Long subPlanId
    ) {
        try {
            subPlanService.deleteSubPlan(subPlanId, planId, userId);
            return ApiResponse.success();
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_DELETE_FAILED);
        }
    }
}
