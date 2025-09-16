package com.hotpack.krocs.domain.templates.dto.request;

import com.hotpack.krocs.global.common.constant.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SubTemplateRequestDTO {

  @NotBlank(message = "{subtemplate.title.notBlank}")
  @Size(max = ValidationConstants.SUB_TITLE_MAX, message = "{subtemplate.title.size}")
  @Schema(description = "SubTemplate 생성 DTO", example = "퇴근하기")
  private String title;
}
