package com.hotpack.krocs.domain.retrospectives.controller;

import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.AllFactorsResponseDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(summary = "전체 회고 요인 목록 조회", description = "회고 시 선택 가능한 모든 성공/실패 요인 목록을 조회합니다.")
    @GetMapping("/factors")
    public ApiResponse<AllFactorsResponseDTO> getAllFactors( @Parameter(hidden = true) @Login Long userId ) {
        try {
            AllFactorsResponseDTO response = retrospectiveService.getFactors();
            return ApiResponse.success(response);
        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_GETFACTORS_FAILED);
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

    @Operation(summary = "대목표 회고 삭제", description = "특정 대목표에 작성된 회고를 삭제합니다.")
    @DeleteMapping("/goals/{goalId}/{retroId}")
    public ApiResponse<Void> deleteRetrospective(
        @Parameter(hidden = true) @Login Long userId,
        @Parameter(description = "회고를 삭제할 목표 ID", example = "1")
        @PathVariable @Positive Long goalId,
        @Parameter(description = "삭제할 회고 ID", example = "1")
        @PathVariable @Positive Long retroId
    ) {
        try {
            retrospectiveService.deleteRetrospective(userId, goalId, retroId);
            return ApiResponse.success(null);
        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_DELETE_FAILED);
        }
    }


}