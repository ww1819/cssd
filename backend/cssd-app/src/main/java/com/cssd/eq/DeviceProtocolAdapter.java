package com.cssd.eq;

import java.util.Map;

/**
 * 设备协议适配层。一期装锅不采集实时参数，仅预留 SPI + Mock。
 */
public interface DeviceProtocolAdapter {
    String protocol();

    default boolean available(String deviceId) {
        return true;
    }

    default Map<String, Object> readCycleParams(String deviceId) {
        return Map.of("source", protocol(), "note", "mock-or-stub");
    }
}
