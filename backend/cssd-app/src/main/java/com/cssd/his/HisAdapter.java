package com.cssd.his;

import java.util.List;
import java.util.Map;

public interface HisAdapter {
    Map<String, Object> findEncounter(String keyword);

    List<Map<String, Object>> searchPatients(String keyword);

    Map<String, Object> pushBind(String barcode, String hisEncounterId, Map<String, Object> payload);
}
