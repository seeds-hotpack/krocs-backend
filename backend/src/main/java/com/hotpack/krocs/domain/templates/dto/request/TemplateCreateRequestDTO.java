package com.hotpack.krocs.domain.templates.dto.request;

import com.hotpack.krocs.global.common.constant.ValidationConstants;
import com.hotpack.krocs.global.common.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TemplateCreateRequestDTO {
    @NotBlank(message = "{template.title.notBlank}")
    @Size(max = ValidationConstants.TITLE_MAX, message = "{template.title.size}")
    private String title;

    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @NotNull(message = "{template.duration.notNull}")
    @Positive(message = "{template.duration.positive}")
    private Integer duration;
}

