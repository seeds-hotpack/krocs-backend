package com.hotpack.krocs.domain.retrospectives.controller;

import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCheckResponseDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import com.hotpack.krocs.domain.retrospectives.service.RetrospectiveService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "회고", description = "회고 관련 API")
@RestController
@RequestMapping("/api/v1/retrospectives")
@RequiredArgsConstructor
@Validated
public class RetrospectiveController {

    private final RetrospectiveService retrospectiveService;

    @Operation(summary = "회고 시작 정보 조회", description = "대목표 완료 시, 달성률에 따라 성공/실패 여부와 선택할 회고 요인 목록을 조회합니다.")
    @GetMapping("/goals/{goalId}/check")
    public ApiResponse<RetrospectiveCheckResponseDTO> checkRetrospective(
        @Parameter(hidden = true) @Login Long userId,
        @Parameter(description = "목표 ID", example = "1")
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId
    ) {
        try {
            RetrospectiveCheckResponseDTO response = retrospectiveService.checkRetrospective(userId, goalId);
            return ApiResponse.success(response);
        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_CHECK_FAILED);
        }
    }

    @Operation(summary = "대목표 완료 및 회고 제출", description = "대목표를 완료/재시도 처리하고 회고를 생성합니다.")
    @PostMapping("/goals/{goalId}")
    public ApiResponse<RetrospectiveCreateResponseDTO> createRetrospective(
        @Parameter(hidden = true) @Login Long userId,
        @Parameter(description = "목표 ID", example = "1")
        @PathVariable @Positive(message = "{common.id.positive}") Long goalId,
        @Valid @RequestBody RetrospectiveCreateRequestDTO requestDTO
    ) {
        try {
            RetrospectiveCreateResponseDTO response = retrospectiveService.createRetrospective(userId, goalId, requestDTO);
            return ApiResponse.success(response);
        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_CREATION_FAILED);
        }
    }
}