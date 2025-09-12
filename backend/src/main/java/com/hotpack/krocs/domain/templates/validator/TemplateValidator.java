package com.hotpack.krocs.domain.templates.validator;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.exception.TemplateException;
import com.hotpack.krocs.domain.templates.exception.TemplateExceptionType;
import com.hotpack.krocs.global.common.entity.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TemplateValidator {

    public void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_TITLE_EMPTY);
        }
        if (title.length() > 200) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_TITLE_TOO_LONG);
        }
    }

    public void validateDuration(Integer duration) {
        if (duration == null || duration <= 0) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_DURATION_INVALID);
        }
    }

    public void validatePriority(String priorityCheck) {
        try {
            Priority.valueOf(priorityCheck.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TemplateException(TemplateExceptionType.TEMPLATE_INVALID_PRIORITY);
        }
    }


    // 이후 리팩토링 시 삭제
    public void validateTemplateCreateDTO(TemplateCreateRequestDTO dto) {
        validateTitle(dto.getTitle());
        validateDuration(dto.getDuration());
    }

    public void validateTemplateBusiness(Template template) {
        validateTitle(template.getTitle());
        validateDuration(template.getDuration());
    }

    public void validateTemplateUpdateDTO(TemplateUpdateRequestDTO dto) {
        if (dto.getTitle() != null) {
            validateTitle(dto.getTitle());
        }
        if (dto.getDuration() != null) {
            validateDuration(dto.getDuration());
        }
        if (dto.getPriority() != null) {
            validatePriority(String.valueOf(dto.getPriority()));
        }
    }
}

