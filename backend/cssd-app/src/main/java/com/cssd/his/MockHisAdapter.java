package com.cssd.his;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "cssd.his.mock", havingValue = "true", matchIfMissing = true)
public class MockHisAdapter implements HisAdapter {
    private static final List<Map<String, Object>> PATIENTS = List.of(
            Map.of("hisPatientId", "P10001", "hisEncounterId", "E20260922001",
                    "patientNo", "ZY10001", "patientName", "演示患者甲", "bedNo", "12-03",
                    "surgeonNo", "SG001", "surgeonName", "周医师", "surgeryName", "阑尾切除术",
                    "deptCode", "OR01"),
            Map.of("hisPatientId", "P10002", "hisEncounterId", "E20260922002",
                    "patientNo", "ZY10002", "patientName", "演示患者乙", "bedNo", "08-11",
                    "surgeonNo", "SG001", "surgeonName", "周医师", "surgeryName", "胆囊切除术",
                    "deptCode", "OR01")
    );

    @Override
    public Map<String, Object> findEncounter(String keyword) {
        return PATIENTS.stream()
                .filter(p -> p.values().stream().anyMatch(v -> String.valueOf(v).contains(keyword)))
                .findFirst()
                .orElse(Map.of());
    }

    @Override
    public List<Map<String, Object>> searchPatients(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return PATIENTS;
        }
        return PATIENTS.stream()
                .filter(p -> p.values().stream().anyMatch(v -> String.valueOf(v).contains(keyword)))
                .toList();
    }

    @Override
    public Map<String, Object> pushBind(String barcode, String hisEncounterId, Map<String, Object> payload) {
        return Map.of("ok", true, "barcode", barcode, "hisEncounterId", hisEncounterId, "mock", true);
    }
}
