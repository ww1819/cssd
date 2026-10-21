package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_his_sync_log", schema = "cssd")
public class CssdHisSyncLog extends BaseEntity {
    private String direction;
    private String bizType;
    private String idempotencyKey;
    private String syncStatus;
    private String instanceId;
    private String barcode;
    private String templateCode;
    private String templateName;
    private String patientNo;
    private String patientName;
    private String hisPatientId;
    private String hisEncounterId;
    private String hisApplyId;
    private String payloadJson;
    private String resultJson;
    private String errorMsg;
    private OffsetDateTime syncedAt;
}