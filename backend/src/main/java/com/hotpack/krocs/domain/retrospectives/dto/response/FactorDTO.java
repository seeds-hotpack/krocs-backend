package com.hotpack.krocs.domain.retrospectives.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FactorDTO {
    private String key;
    private String description;
}
