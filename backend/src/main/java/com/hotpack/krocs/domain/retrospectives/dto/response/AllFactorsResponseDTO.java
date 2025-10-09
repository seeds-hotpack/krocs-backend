package com.hotpack.krocs.domain.retrospectives.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllFactorsResponseDTO {
    private List<FactorDTO> successFactors;
    private List<FactorDTO> failureFactors;
}
