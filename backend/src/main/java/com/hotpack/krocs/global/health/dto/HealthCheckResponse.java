package com.hotpack.krocs.global.health.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload returned by /health so infra can validate service and Redis status.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResponse {

    private String service;
    private String status;
    private LocalDateTime timestamp;
    private DependencyHealthStatus redis;
}
