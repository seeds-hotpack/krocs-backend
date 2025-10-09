package com.hotpack.krocs.domain.stopwatch.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StopwatchTimeResponseDTO {
    private String time; // "hh:mm:ss" 형식
}