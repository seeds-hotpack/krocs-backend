package com.hotpack.krocs.domain.retrospectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrospectiveCreateRequestDTO {
    @Schema(description = "사용자의 최종 행동 (COMPLETE, RETRY)", example = "COMPLETE_SUCCESS")
    @NotBlank(message = "{retro.outcome.notBlank}")
    private String outcome;

    @Schema(description = "회고 요인(Key) 목록", example = "[\"CLEAR_PLAN\", \"STEADY_EXECUTION\", \"ETC\"]")
    private List<String> factors;

    @Schema(description = "'기타' 사유를 직접 작성한 내용", example = "이번 목표는 계획대로 잘 진행되었습니다.")
    private String content;
}
