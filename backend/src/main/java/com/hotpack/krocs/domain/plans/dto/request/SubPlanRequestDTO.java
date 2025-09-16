package com.hotpack.krocs.domain.plans.dto.request;

import com.hotpack.krocs.global.common.constant.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SubPlanRequestDTO {

    @NotBlank(message = "{subplan.title.notBlank}")
    @Size(max = ValidationConstants.SUB_TITLE_MAX, message = "{subplan.title.size}")
    @Schema(description = "SubPlan 제목", example = "에프킬라 사기")
    private String title;
}

