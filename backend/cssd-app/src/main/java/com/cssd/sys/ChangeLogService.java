package com.cssd.sys;

import com.cssd.common.IdUtils;
import com.cssd.infra.entity.CssdEntityChangeLog;
import com.cssd.infra.mapper.CssdEntityChangeLogMapper;
import com.cssd.security.LoginUser;
import com.cssd.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChangeLogService {
    private final CssdEntityChangeLogMapper mapper;
    private final ObjectMapper objectMapper;

    public ChangeLogService(CssdEntityChangeLogMapper mapper, ObjectMapper objectMapper) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public void record(String entityType, String entityId, String action, Object diff, Object snapshot) {
        CssdEntityChangeLog log = new CssdEntityChangeLog();
        log.setId(IdUtils.uuid7());
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        LoginUser user = SecurityUtils.current();
        if (user != null) {
            log.setOperatorId(user.getStaffId());
            log.setOperatorNo(user.getStaffNo());
            log.setOperatorName(user.getStaffName());
            log.setClient("web");
        }
        log.setDiffJson(write(diff));
        log.setSnapshotJson(write(snapshot));
        mapper.insert(log);
    }

    private String write(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(o instanceof String s ? Map.of("text", s) : o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
