package com.hotpack.krocs.domain.templates.facade;

import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.repository.SubTemplateRepository;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubTemplateRepositoryFacade {

    private final SubTemplateRepository subTemplateRepository;

    @Transactional
    public List<SubTemplate> saveAll(List<SubTemplate> subTemplates) {
        return subTemplateRepository.saveAll(subTemplates);
    }

    public List<SubTemplate> findActiveSubTemplatesByTemplate(Template template) {
        return subTemplateRepository.findSubTemplatesByTemplateAndStatus(template, Status.ACTIVE);
    }

    @Transactional
    public Long deleteActiveSubTemplateBySubTemplateId(Long subTemplateId) {
        SubTemplate subTemplate = subTemplateRepository.findSubTemplateBySubTemplateIdAndStatus(
            subTemplateId, Status.ACTIVE);
        if (subTemplate == null) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_NOT_FOUND);
        }

        subTemplate.delete();

        return subTemplateId;
    }

    @Transactional
    public SubTemplate updateActiveSubTemplateBySubTemplateId(Long subTemplateId,
        SubTemplateUpdateRequestDTO requestDTO) {
        SubTemplate subTemplate = subTemplateRepository.findSubTemplateBySubTemplateIdAndStatus(
            subTemplateId, Status.ACTIVE);
        if (subTemplate == null) {
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_NOT_FOUND);
        }

        subTemplate.updateFrom(requestDTO);

        return subTemplate;
    }
}