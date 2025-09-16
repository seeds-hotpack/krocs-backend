package com.hotpack.krocs.domain.templates.facade;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.exception.TemplateException;
import com.hotpack.krocs.domain.templates.exception.TemplateExceptionType;
import com.hotpack.krocs.domain.templates.repository.TemplateRepository;
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
public class TemplateRepositoryFacade {

    private final TemplateRepository templateRepository;

    @Transactional
    public Template save(Template template) {
        return templateRepository.save(template);
    }

    public List<Template> findActiveTemplatesByTitle(String title) {
        return templateRepository.findTemplatesByTitleContainingIgnoreCaseAndStatus(title,
            Status.ACTIVE);
    }

    public List<Template> findAllActiveTemplates() {
        return templateRepository.findAllTemplatesByStatus(Status.ACTIVE);
    }

    public Template findActiveTemplateByTemplateId(Long templateId) {
        Template template = templateRepository.findTemplateByTemplateIdAndStatus(templateId,
            Status.ACTIVE);
        if(template == null){
            throw new TemplateException(TemplateExceptionType.TEMPLATE_NOT_FOUND);
        }
        return template;
    }

    public Template findActiveParentTemplateByTemplateId(Long templateId) {
        Template template = templateRepository.findTemplateByTemplateIdAndStatus(templateId,
            Status.ACTIVE);
        if(template == null){
            throw new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_NOT_FOUND);
        }
        return template;
    }

    public void existsActiveTemplateByTemplateTitle(String title) {
        if (templateRepository.existsTemplateByTitleAndStatus(title, Status.ACTIVE)) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_DUPLICATE_TITLE);
        }
    }

    @Transactional
    public void deleteActiveTemplate(Template template) {
        template.delete();
    }
}
