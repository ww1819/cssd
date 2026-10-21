package com.cssd.eq;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeviceProtocolRegistry {
    private final List<DeviceProtocolAdapter> adapters;

    public DeviceProtocolRegistry(List<DeviceProtocolAdapter> adapters) {
        this.adapters = adapters;
    }

    public DeviceProtocolAdapter of(String protocol) {
        String p = protocol == null ? "mock" : protocol.toLowerCase();
        return adapters.stream().filter(a -> a.protocol().equals(p)).findFirst()
                .orElseGet(() -> adapters.stream().filter(a -> "mock".equals(a.protocol())).findFirst().orElseThrow());
    }

    public List<Map<String, Object>> list() {
        return adapters.stream().map(a -> Map.<String, Object>of("protocol", a.protocol())).toList();
    }
}
