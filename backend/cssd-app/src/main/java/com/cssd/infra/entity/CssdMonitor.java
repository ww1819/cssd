package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_monitor", schema = "cssd")
public class CssdMonitor extends BaseEntity {
    private String monitorNo;
    private String monitorKind;
    private String monitorStatus;
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
    private String loadId;
    private String loadNo;
    private String instanceId;
    private String barcode;
    private LocalDate monitorDate;
    private String resultCode;
    private OffsetDateTime resultAt;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private String reportUrl;
    private String remark;
}