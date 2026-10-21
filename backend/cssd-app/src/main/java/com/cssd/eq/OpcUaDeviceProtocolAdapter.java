package com.cssd.eq;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpcUaDeviceProtocolAdapter implements DeviceProtocolAdapter {
    @Override
    public String protocol() {
        return "opcua";
    }

    @Override
    public Map<String, Object> readCycleParams(String deviceId) {
        return Map.of("source", "opcua", "implemented", false);
    }
}
