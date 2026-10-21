package com.cssd.eq;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MockDeviceProtocolAdapter implements DeviceProtocolAdapter {
    @Override
    public String protocol() {
        return "mock";
    }

    @Override
    public Map<String, Object> readCycleParams(String deviceId) {
        return Map.of("source", "mock", "tempC", 134, "pressureKpa", 210, "holdMin", 4);
    }
}
