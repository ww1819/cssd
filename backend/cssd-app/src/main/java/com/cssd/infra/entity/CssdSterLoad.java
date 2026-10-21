package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_ster_load", schema = "cssd")
public class CssdSterLoad extends BaseEntity {
    private String loadNo;
    private String loadStatus;
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
    private String programId;
    private String programCode;
    private String programName;
    private String sterTempClass;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private LocalDate sterilizeDate;
    private String paramsJson;
    private String reportUrl;
    private String mainLoadDesc;
    private Integer hasImplant;
    private String bdResult;
    private String pcdResult;
    private String bioResult;
    private String chemResult;
    private String releaseStatus;
    private OffsetDateTime releasedAt;
    private String remark;
}