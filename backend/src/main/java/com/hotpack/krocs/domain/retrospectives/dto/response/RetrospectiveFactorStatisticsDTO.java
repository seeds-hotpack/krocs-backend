package com.hotpack.krocs.domain.retrospectives.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RetrospectiveFactorStatisticsDTO {

    private final String factor;
    private final String description;
    private final long count;
}
