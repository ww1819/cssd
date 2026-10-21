package com.cssd.web;

import com.cssd.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public ApiResult<Map<String, String>> health() {
        return ApiResult.ok(Map.of("status", "UP"));
    }
}
