package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_rfid_bind_log", schema = "cssd")
public class CssdRfidBindLog extends BaseEntity {
    private String instanceId;
    private String barcode;
    private String templateCode;
    private String templateName;
    private String rfidUid;
    private String bindAction;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private OffsetDateTime boundAt;
    private String remark;
}