package com.hotpack.krocs.domain.templates.facade;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.repository.TemplateRepository;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Template> findActiveTemplatesByTitleAndUserId(String title, Long userId, Pageable pageable) {
        return templateRepository.findTemplatesByTitleContainingIgnoreCaseAndUser_userIdAndStatus(
            title, userId, Status.ACTIVE, pageable);
    }

    public Page<Template> findAllActiveTemplatesAndUserId(Long userId, Pageable pageable) {
        return templateRepository.findAllTemplatesByUser_UserIdAndStatus(userId, Status.ACTIVE, pageable);
    }

    public List<Template> findActiveTemplatesByTitleAndUserId(String title, Long userId) {
        return templateRepository.findTemplatesByTitleContainingIgnoreCaseAndUser_userIdAndStatus(
            title, userId, Status.ACTIVE);
    }

    public List<Template> findAllActiveTemplatesAndUserId(Long userId) {
        return templateRepository.findAllTemplatesByUser_UserIdAndStatus(userId, Status.ACTIVE);
    }

    public Template findActiveTemplateByTemplateIdAndUserId(Long templateId, Long userId) {
        return templateRepository.findTemplateByTemplateIdAndUser_UserIdAndStatus(templateId,
            userId, Status.ACTIVE);
    }

    public Template findActiveParentTemplateByTemplateIdAndUserId(Long templateId, Long userId) {
        return templateRepository.findTemplateByTemplateIdAndUser_UserIdAndStatus(
            templateId, userId, Status.ACTIVE);
    }

    @Transactional
    public void deleteActiveTemplate(Template template) {
        template.delete();
    }
}
