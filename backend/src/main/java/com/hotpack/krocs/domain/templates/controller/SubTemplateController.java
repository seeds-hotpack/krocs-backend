package com.hotpack.krocs.domain.templates.controller;

import com.hotpack.krocs.domain.templates.dto.request.SubTemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateDeleteResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.service.SubTemplateService;
import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.security.annotation.Login;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
@Validated
public class SubTemplateController {

    private final SubTemplateService subTemplateService;

    @Operation(summary = "서브 템플릿 생성", description = "템플릿에 종속된 서브 템플릿을 생성합니다.")
    @PostMapping("/{templateId}/subtemplates")
    public ApiResponse<SubTemplateCreateResponseDTO> saveSubTemplates(
        @Login Long userId,
        @Valid @RequestBody SubTemplateCreateRequestDTO requestDTO,
        @PathVariable(value = "templateId") @Positive(message = "{common.id.positive}") Long templateId
    ) {
        try {
            SubTemplateCreateResponseDTO responseDTO = subTemplateService.createSubTemplates(
                templateId, requestDTO, userId);
            return ApiResponse.success(responseDTO);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_CREATION_FAILED);
        }
    }

    @Operation(summary = "서브 템플릿 전체 조회", description = "템플릿에 종속된 모든 서브 템플릿을 조회합니다.")
    @GetMapping("/{templateId}/subtemplates")
    public ApiResponse<List<SubTemplateResponseDTO>> getSubTemplates(
        @Login Long userId,
        @PathVariable(value = "templateId") @Positive(message = "{common.id.positive}") Long templateId
    ) {
        try {
            List<SubTemplateResponseDTO> responseDTOs = subTemplateService.getSubTemplates(
                templateId, userId);
            return ApiResponse.success(responseDTOs);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_FOUND_FAILED);
        }
    }


    @Operation(summary = "서브 템플릿 삭제", description = "subTemplateId를 조회하여 서브템플릿을 삭제합니다.")
    @DeleteMapping("{templateId}/subtemplates/{subTemplateId}")
    public ApiResponse<SubTemplateDeleteResponseDTO> deleteSubTemplate(
        @Login Long userId,
        @PathVariable("templateId") @Positive(message = "{common.id.positive}") Long templateId,
        @PathVariable("subTemplateId") @Positive(message = "{common.id.positive}") Long subTemplateId) {
        try {
            SubTemplateDeleteResponseDTO responseDTO = subTemplateService.deleteSubTemplate(
                subTemplateId, templateId, userId);

            return ApiResponse.success(responseDTO);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_DELETE_FAILED);
        }
    }

    @Operation(summary = "서브 템플릿 수정", description = "subTemplateId를 조회하여 서브템플릿을 수정합니다.")
    @PatchMapping("{templateId}/subtemplates/{subTemplateId}")
    public ApiResponse<SubTemplateResponseDTO> updateSubTemplate(
        @Login Long userId,
        @PathVariable("templateId") @Positive(message = "{common.id.positive}") Long templateId,
        @PathVariable("subTemplateId") @Positive(message = "{common.id.positive}") Long subTemplateId,
        @RequestBody SubTemplateUpdateRequestDTO request
    ) {
        try {
            SubTemplateResponseDTO responseDTO = subTemplateService.updateSubTemplate(subTemplateId,
                templateId, userId, request);
            return ApiResponse.success(responseDTO);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_UPDATE_FAILED);
        }
    }
}
