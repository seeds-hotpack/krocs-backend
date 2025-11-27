package com.hotpack.krocs.global.health.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependencyHealthStatus {

    private String name;
    private String status;
    private String message;
}
