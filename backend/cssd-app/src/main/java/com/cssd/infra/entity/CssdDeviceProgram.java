package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_device_program", schema = "cssd")
public class CssdDeviceProgram extends BaseEntity {
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
    private String programCode;
    private String programName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}