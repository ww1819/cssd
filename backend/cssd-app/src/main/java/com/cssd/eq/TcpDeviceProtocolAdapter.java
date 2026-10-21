package com.cssd.eq;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TcpDeviceProtocolAdapter implements DeviceProtocolAdapter {
    @Override
    public String protocol() {
        return "tcp";
    }

    @Override
    public Map<String, Object> readCycleParams(String deviceId) {
        return Map.of("source", "tcp", "implemented", false);
    }
}
