package com.cssd.eq;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SerialDeviceProtocolAdapter implements DeviceProtocolAdapter {
    @Override
    public String protocol() {
        return "serial";
    }

    @Override
    public Map<String, Object> readCycleParams(String deviceId) {
        return Map.of("source", "serial", "implemented", false);
    }
}
