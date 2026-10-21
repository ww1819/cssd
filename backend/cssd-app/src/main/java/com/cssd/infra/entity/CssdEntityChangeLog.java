package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_entity_change_log", schema = "cssd")
public class CssdEntityChangeLog extends BaseEntity {
    private String entityType;
    private String entityId;
    private String action;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private String client;
    private String diffJson;
    private String snapshotJson;
}