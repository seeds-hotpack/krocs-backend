package com.hotpack.krocs.domain.plans.controller;

import com.hotpack.krocs.domain.auth.dto.UserSession;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.service.PlanService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plans")
@Tag(name = "Plan", description = "Plan 관련 API")
@Validated
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "일정 생성", description = "새로운 일정을 생성합니다.")
    @PostMapping
    public ApiResponse<PlanResponseDTO> createPlan(
        @Valid @RequestBody PlanCreateRequestDTO requestDTO,
        @Login UserSession user
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            PlanResponseDTO responseDTO = planService.createPlan(requestDTO, userId);
            return ApiResponse.success(responseDTO);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanException(PlanExceptionType.PLAN_CREATION_FAILED);
        }
    }

    @Operation(summary = "범위로 일정 조회", description = "범위로 일정을 조회합니다.")
    @GetMapping
    public ApiResponse<PlanListResponseDTO> getPlans(
        @Login UserSession user,
        @RequestParam(required = false) LocalDate date
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;

            PlanListResponseDTO response = planService.getPlans(date, userId);
            return ApiResponse.success(response);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }

    @Operation(summary = "특정 일정 조회", description = "특정 일정을 조회합니다.")
    @GetMapping("/{planId}")
    public ApiResponse<PlanResponseDTO> getPlanById(
        @PathVariable @Parameter(description = "Plan ID", example = "1") @Positive(message = "{common.id.positive}")
        Long planId,
        @Login UserSession user
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            PlanResponseDTO response = planService.getPlanById(planId, userId);
            return ApiResponse.success(response);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }

    @Operation(summary = "일정 수정", description = "기존 일정의 정보를 수정합니다."
    )
    @PatchMapping("/{planId}")
    public ApiResponse<PlanResponseDTO> updatePlanById(
        @PathVariable @Positive(message = "{common.id.positive}") Long planId,
        @Valid @RequestBody PlanUpdateRequestDTO request,
        @Login UserSession user) {

        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            PlanResponseDTO responseDTO = planService.updatePlanById(planId, request, userId);

            return ApiResponse.success(responseDTO);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanException(PlanExceptionType.PLAN_UPDATE_FAILED);
        }
    }

    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다")
    @DeleteMapping("/{planId}")
    public ApiResponse<Void> deletePlan(
        @PathVariable @Positive(message = "{common.id.positive}") @Parameter(description = "Plan ID", example = "1") Long planId,
        @Login UserSession user
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            planService.deletePlan(planId, userId);
            return ApiResponse.success();
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanException(PlanExceptionType.PLAN_DELETE_FAILED);
        }
    }
}
