package com.hotpack.krocs.domain.templates.controller;


import com.hotpack.krocs.domain.auth.dto.UserSession;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.exception.TemplateException;
import com.hotpack.krocs.domain.templates.exception.TemplateExceptionType;
import com.hotpack.krocs.domain.templates.service.SubTemplateService;
import com.hotpack.krocs.domain.templates.service.TemplateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
@Validated
public class TemplateController {

    private final TemplateService templateService;
    private final SubTemplateService subTemplateService;

    @Operation(summary = "템플릿 생성", description = "title, priority을 설정해 템플릿을 생성합니다.")
    @PostMapping
    public ApiResponse<TemplateCreateResponseDTO> createTemplate(
        @Valid @RequestBody TemplateCreateRequestDTO requestDTO,
        @Login UserSession user
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            TemplateCreateResponseDTO responseDTO = templateService.createTemplate(requestDTO,
                userId);
            return ApiResponse.success(responseDTO);

        } catch (TemplateException e) {
            throw e;

        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_CREATION_FAILED);
        }
    }

    @Operation(summary = "템플릿 전체 조회 및 검색", description = "사용자 ID 기반으로 템플릿을 조회하며, title 키워드로 부분 검색이 가능합니다.")
    @GetMapping
    public ApiResponse<List<TemplateResponseDTO>> getTemplates(
        @Login UserSession user,
        @RequestParam(required = false) String title) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            List<TemplateResponseDTO> responseDTO = templateService.getTemplatesByUserAndTitle(
                userId,
                title);
            return ApiResponse.success(responseDTO);

        } catch (TemplateException e) {
            throw e;

        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_FOUND_FAILED);
        }
    }


    @Operation(summary = "템플릿 수정", description = "탬플릿을 id 기준으로 수정합니다.")
    @PatchMapping("/{template_id}")
    public ApiResponse<TemplateResponseDTO> updateTemplates(
        @PathVariable(value = "template_id") @Positive(message = "{common.id.positive}") Long templateId,
        @Login UserSession user,
        @RequestBody TemplateUpdateRequestDTO requestDTO) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            TemplateResponseDTO responseDTO = templateService.updateTemplate(templateId, userId,
                requestDTO);
            return ApiResponse.success(responseDTO);

        } catch (TemplateException e) {
            throw e;

        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_UPDATE_FAILED);
        }
    }

    @Operation(summary = "템플릿 삭제", description = "템플릿을 탬플릿ID로 삭제합니다.")
    @DeleteMapping("/{template_id}")
    public ApiResponse<Void> deleteTemplate(
        @PathVariable(value = "template_id") @Positive(message = "{common.id.positive}") Long templateId,
        @Login UserSession user) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            templateService.deleteTemplate(templateId, userId);
            return ApiResponse.success(null);

        } catch (TemplateException e) {
            throw e;

        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_DELETE_FAILED);
        }
    }

    //============== SubTemplate ==============

    @Operation(summary = "서브 템플릿 생성", description = "템플릿에 종속된 서브 템플릿을 생성합니다.")
    @PostMapping("/{templateId}/subtemplates")
    public ApiResponse<SubTemplateCreateResponseDTO> saveSubTemplates(
        @Valid @RequestBody SubTemplateCreateRequestDTO requestDTO,
        @PathVariable(value = "templateId") @Positive(message = "{common.id.positive}") Long templateId,
        @Login UserSession user
    ) {
        try {
            Long userId = user != null ? Long.valueOf(user.getUserId()) : null;
            SubTemplateCreateResponseDTO responseDTO = subTemplateService.createSubTemplates(
                templateId,
                requestDTO, userId);
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
        @PathVariable(value = "templateId") @Positive(message = "{common.id.positive}") Long templateId,
        @Login UserSession user
    ) {
        try {
            List<SubTemplateResponseDTO> responseDTOs = subTemplateService.getSubTemplates(
                templateId);
            return ApiResponse.success(responseDTOs);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_FOUND_FAILED);
        }
    }

}
