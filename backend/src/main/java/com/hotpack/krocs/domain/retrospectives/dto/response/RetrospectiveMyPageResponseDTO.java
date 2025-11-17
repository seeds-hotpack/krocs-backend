package com.hotpack.krocs.domain.retrospectives.dto.response;

import com.hotpack.krocs.global.common.response.PageResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RetrospectiveMyPageResponseDTO {

    private final RetrospectiveStatisticsDTO statistics;
    private final PageResponseDTO<RetrospectiveSummaryDTO> retrospectives;
}
