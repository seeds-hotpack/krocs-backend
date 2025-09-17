package com.hotpack.krocs.domain.templates.converter;

import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;
import com.hotpack.krocs.global.common.entity.Priority;
import com.hotpack.krocs.domain.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TemplateConverter {

  public Template toEntity(TemplateCreateRequestDTO requestDTO) {

    Priority priority = requestDTO.getPriority();
    if (requestDTO.getPriority() == null) {
      priority = Priority.MEDIUM;
    }
    return Template.builder()
        .title(requestDTO.getTitle())
        .priority(priority)
        .duration(requestDTO.getDuration())
        .subTemplates(List.of())
        .build();
  }

  public Template toEntity(TemplateCreateRequestDTO requestDTO, User user) {
    Priority priority = requestDTO.getPriority();
    if (requestDTO.getPriority() == null) {
      priority = Priority.MEDIUM;
    }
    return Template.builder()
        .user(user)
        .title(requestDTO.getTitle())
        .priority(priority)
        .duration(requestDTO.getDuration())
        .subTemplates(List.of())
        .build();
  }

  public TemplateCreateResponseDTO toCreateResponseDTO(Template template) {
    List<SubTemplateResponseDTO> subTemplateDTOs;

    if (template.getSubTemplates() != null) {
      subTemplateDTOs = template.getSubTemplates().stream()
          .map(this::toSubTemplateResponseDTO)
          .collect(Collectors.toList());
    } else {
      subTemplateDTOs = List.of();
    }

    return TemplateCreateResponseDTO.builder()
        .templateId(template.getTemplateId())
        .title(template.getTitle())
        .priority(template.getPriority())
        .duration(template.getDuration())
        .subTemplates(subTemplateDTOs)
        .createdAt(template.getCreatedAt())
        .updatedAt(template.getUpdatedAt())
        .build();
  }

  private SubTemplateResponseDTO toSubTemplateResponseDTO(SubTemplate subTemplate) {
    return SubTemplateResponseDTO.builder()
        .subTemplateId(subTemplate.getSubTemplateId())
        .title(subTemplate.getTitle())
        .build();
  }


  public TemplateResponseDTO toTemplateResponseDTO(Template template) {
    return TemplateResponseDTO.builder()
        .templateId(template.getTemplateId())
        .title(template.getTitle())
        .priority(template.getPriority())
        .duration(template.getDuration())
        .createdAt(template.getCreatedAt())
        .updatedAt(template.getUpdatedAt())
        .subTemplates(template.getSubTemplates().stream()
            .map(this::toSubTemplateResponseDTO)
            .toList())
        .build();
  }
}