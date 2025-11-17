package com.hotpack.krocs.domain.retrospectives.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RetrospectiveStatisticsDTO {

    private final List<RetrospectiveFactorStatisticsDTO> topSuccessFactors;
    private final List<RetrospectiveFactorStatisticsDTO> topFailureFactors;
}
