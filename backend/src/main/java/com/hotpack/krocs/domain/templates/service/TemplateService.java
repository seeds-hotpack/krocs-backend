package com.hotpack.krocs.domain.templates.service;

import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;

import com.hotpack.krocs.global.common.response.PageResponseDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TemplateService {

    TemplateCreateResponseDTO createTemplate(TemplateCreateRequestDTO requestDTO, Long userId);

    PageResponseDTO<TemplateResponseDTO> getTemplatesByUserAndTitle(Long userId, String title,
        Pageable pageable);

    TemplateResponseDTO updateTemplate(Long templateId, Long userId, TemplateUpdateRequestDTO requestDTO);

    void deleteTemplate(Long templateId, Long userId);

}
