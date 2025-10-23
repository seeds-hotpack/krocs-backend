package com.hotpack.krocs.domain.retrospectives.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RetrospectiveCreateResponseDTO {

    @JsonProperty("retrospective_id")
    private Long retrospectiveId;

    @JsonProperty("goal_id")
    private Long goalId;

    private RetrospectiveOutcome outcome;

    private List<FactorDTO> factors;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;
}
