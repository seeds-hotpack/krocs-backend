package com.hotpack.krocs.domain.templates.service;


import com.hotpack.krocs.domain.templates.converter.TemplateConverter;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.TemplateException;
import com.hotpack.krocs.domain.templates.exception.TemplateExceptionType;
import com.hotpack.krocs.domain.templates.facade.TemplateRepositoryFacade;
import com.hotpack.krocs.domain.templates.validator.TemplateValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepositoryFacade templateRepositoryFacade;
    private final UserRepositoryFacade userRepositoryFacade;
    private final TemplateConverter templateConverter;
    private final TemplateValidator templateValidator;


    @Override
    @Transactional
    public TemplateCreateResponseDTO createTemplate(TemplateCreateRequestDTO requestDTO,
        Long userId) {
        try {
            templateValidator.validateTemplateCreateDTO(requestDTO);

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new TemplateException(TemplateExceptionType.TEMPLATE_USER_NOT_FOUND);
            }
            Template template = templateConverter.toEntity(requestDTO, user);
            // templateValidator.validateTemplateBusiness(template); 유효성 검사 적용 이후
            Template savedTemplate = templateRepositoryFacade.save(template);

            return templateConverter.toCreateResponseDTO(savedTemplate);
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_CREATION_FAILED);
        }
    }

    @Override
    public List<TemplateResponseDTO> getTemplatesByUserAndTitle(Long userId, String title) {
        try {

            List<Template> templates;
            if (StringUtils.hasText(title) && title.length() <= ValidationConstants.TITLE_MAX) {
                templates = templateRepositoryFacade.findActiveTemplatesByTitleAndUserId(title,
                    userId);
            } else {
                templates = templateRepositoryFacade.findAllActiveTemplatesAndUserId(userId);
            }

            return templates.stream()
                .map(templateConverter::toTemplateResponseDTO)
                .toList();
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_FOUND_FAILED);
        }
    }

    @Override
    @Transactional
    public TemplateResponseDTO updateTemplate(Long templateId, Long userId,
        TemplateUpdateRequestDTO requestDTO) {
        try {
            templateValidator.validateTemplateUpdateDTO(requestDTO);

            Template template = templateRepositoryFacade.findActiveTemplateByTemplateIdAndUserId(
                templateId, userId);
            if (template == null) {
                throw new TemplateException(TemplateExceptionType.TEMPLATE_NOT_FOUND);
            }

            template.updateFrom(requestDTO);
            Template updatedTemplate = templateRepositoryFacade.findActiveTemplateByTemplateIdAndUserId(
                templateId, userId);

            return templateConverter.toTemplateResponseDTO(updatedTemplate);
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteTemplate(Long templateId, Long userId) {
        try {
            Template template = templateRepositoryFacade.findActiveTemplateByTemplateIdAndUserId(
                templateId, userId);
            if (template == null) {
                throw new TemplateException(TemplateExceptionType.TEMPLATE_NOT_FOUND);
            }

            templateRepositoryFacade.deleteActiveTemplate(template);
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_DELETE_FAILED);
        }
    }
}
