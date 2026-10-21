package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_wash_batch", schema = "cssd")
public class CssdWashBatch extends BaseEntity {
    private String batchNo;
    private String washMode;
    private String templateWashMode;
    private Integer modeOverridden;
    private String overrideReason;
    private String batchStatus;
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
    private String programId;
    private String programCode;
    private String programName;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private String checkResult;
    private String checkerId;
    private String checkerNo;
    private String checkerName;
    private OffsetDateTime checkedAt;
    private String reportUrl;
    private String remark;
}