package com.hotpack.krocs.domain.templates.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SubTemplateCreateRequestDTO {

  @Schema(description = "SubTemplate 리스트")
  @NotEmpty(message = "{subtemplate.list.notEmpty}")
  @Valid
  List<SubTemplateRequestDTO> subTemplates;
}
