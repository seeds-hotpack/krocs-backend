package com.hotpack.krocs.domain.templates.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.global.common.entity.Priority;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TemplateResponseDTO {
    private Long templateId;

    private String title;

    private Priority priority;

    private Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;

    private List<SubTemplateResponseDTO> subTemplates;

}
