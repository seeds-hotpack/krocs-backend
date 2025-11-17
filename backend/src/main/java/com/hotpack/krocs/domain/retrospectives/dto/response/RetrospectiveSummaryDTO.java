package com.hotpack.krocs.domain.retrospectives.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RetrospectiveSummaryDTO {

    private final Long retrospectiveId;
    private final Long goalId;
    private final String goalName;
    private final RetrospectiveOutcome outcome;
    private final String content;
    private final List<String> factors;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;
}
