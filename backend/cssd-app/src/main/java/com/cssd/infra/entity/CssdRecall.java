package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_recall", schema = "cssd")
public class CssdRecall extends BaseEntity {
    private String recallNo;
    private String recallStatus;
    private String recallReason;
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
    private String loadId;
    private String loadNo;
    private String monitorId;
    private OffsetDateTime sinceTime;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private OffsetDateTime recalledAt;
    private String remark;
}