package com.hotpack.krocs.domain.templates.service;

import com.hotpack.krocs.domain.templates.dto.request.SubTemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateDeleteResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import java.util.List;

public interface SubTemplateService {

    SubTemplateCreateResponseDTO createSubTemplates(Long templateId,
        SubTemplateCreateRequestDTO requestDTO, Long userId);

    List<SubTemplateResponseDTO> getSubTemplates(Long templateId, Long userId);

    SubTemplateDeleteResponseDTO deleteSubTemplate(Long subTemplateId, Long templateId,
        Long userId);

    SubTemplateResponseDTO updateSubTemplate(Long subTemplateId, Long templateId, Long userId,
        SubTemplateUpdateRequestDTO requestDTO);
}
