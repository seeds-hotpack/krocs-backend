package com.hotpack.krocs.domain.plans.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SubPlanCreateRequestDTO {

    @JsonProperty("sub_plans")
    @Schema(description = "SubPlan 리스트")
    @NotEmpty(message = "{subplan.list.notEmpty}")
    @Valid
    private List<SubPlanRequestDTO> subPlans;
}