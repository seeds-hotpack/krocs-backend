package com.hotpack.krocs.domain.retrospectives.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrospectiveCheckResponseDTO {
    @JsonProperty("outcome_type")
    private String outcomeType; // "SUCCESS" or "FAILURE"

    private int completionPercentage;

    private List<FactorDTO> factors;
}