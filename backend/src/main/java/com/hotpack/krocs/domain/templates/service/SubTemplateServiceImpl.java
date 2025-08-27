package com.hotpack.krocs.domain.templates.service;

import com.hotpack.krocs.domain.templates.converter.SubTemplateConverter;
import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateDeleteResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.facade.SubTemplateRepositoryFacade;
import com.hotpack.krocs.domain.templates.facade.TemplateRepositoryFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubTemplateServiceImpl implements SubTemplateService {

    private final TemplateRepositoryFacade templateRepositoryFacade;
    private final SubTemplateRepositoryFacade subTemplateRepositoryFacade;

    private final SubTemplateConverter subTemplateConverter;

    @Override
    @Transactional
    public SubTemplateCreateResponseDTO createSubTemplates(Long templateId,
        SubTemplateCreateRequestDTO requestDTO,
        Long userId) {
        try {
            if (templateId == null) {
                throw new SubTemplateException(
                    SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_ID_IS_NULL);
            }
            Template template = templateRepositoryFacade.findActiveTemplateByTemplateId(templateId);
            if (template == null) {
                throw new SubTemplateException(
                    SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_NOT_FOUND);
            }
            List<SubTemplate> subTemplates = subTemplateConverter.toEntityList(template,
                requestDTO);
            List<SubTemplate> createdSubTemplates = subTemplateRepositoryFacade.saveAll(
                subTemplates);
            return subTemplateConverter.toCreateResponseDTO(createdSubTemplates);

        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("서브 템플릿 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_CREATION_FAILED);
        }
    }

    @Override
    public List<SubTemplateResponseDTO> getSubTemplates(Long templateId) {
        try {
            if (templateId == null) {
                throw new SubTemplateException(
                    SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_ID_IS_NULL);
            }

            Template template = templateRepositoryFacade.findActiveTemplateByTemplateId(templateId);

            List<SubTemplate> subTemplates = subTemplateRepositoryFacade.findBySubTemplate(
                template);

            return subTemplateConverter.toListResponseDTO(subTemplates);
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("서브 템플릿 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_FOUND_FAILED);
        }
    }

    @Override
    @Transactional
    public SubTemplateDeleteResponseDTO deleteSubTemplate(Long subTemplateId) {
        try {
            if (subTemplateId == null) {
                throw new SubTemplateException(
                    SubTemplateExceptionType.SUB_TEMPLATE_SUB_TEMPLATE_ID_IS_NULL);
            }

            Long deletedSubTemplateId = subTemplateRepositoryFacade.deleteBySubTemplateId(
                subTemplateId);

            return SubTemplateDeleteResponseDTO.builder()
                .subTemplateId(deletedSubTemplateId)
                .build();
        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("서브 템플릿 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    public SubTemplateResponseDTO updateSubTemplate(Long subTemplateId,
        SubTemplateUpdateRequestDTO requestDTO) {
        try {
            if (subTemplateId == null) {
                throw new SubTemplateException(
                    SubTemplateExceptionType.SUB_TEMPLATE_SUB_TEMPLATE_ID_IS_NULL);
            }

            SubTemplate updatedSubTemplate = subTemplateRepositoryFacade.updateBySubTemplateId(
                subTemplateId, requestDTO);

            return subTemplateConverter.toResponseDTO(updatedSubTemplate);

        } catch (SubTemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_UPDATE_FAILED);
        }
    }
}
