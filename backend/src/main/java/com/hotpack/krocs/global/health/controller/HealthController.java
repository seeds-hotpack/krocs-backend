package com.hotpack.krocs.global.health.controller;

import com.hotpack.krocs.global.common.response.ApiResponse;
import com.hotpack.krocs.global.health.dto.HealthCheckResponse;
import com.hotpack.krocs.global.health.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "애플리케이션 상태 확인 API")
public class HealthController {

    private final String applicationName;
    private final HealthCheckService healthCheckService;

    public HealthController(
        @Value("${spring.application.name:krocs-backend}") String applicationName,
        HealthCheckService healthCheckService
    ) {
        this.applicationName = applicationName;
        this.healthCheckService = healthCheckService;
    }

    @Operation(summary = "헬스 체크", description = "애플리케이션과 Redis가 정상 동작 중인지 확인합니다.")
    @GetMapping("/health")
    public ApiResponse<HealthCheckResponse> health() {
        return ApiResponse.success(healthCheckService.check(applicationName));
    }
}
