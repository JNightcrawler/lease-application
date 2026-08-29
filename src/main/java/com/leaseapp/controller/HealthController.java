package com.leaseapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Returns the current health status of the application")
    @ApiResponse(responseCode = "200", description = "Application is healthy",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "lease-application");
    }
}
