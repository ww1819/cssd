package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_device", schema = "cssd")
public class CssdDevice extends BaseEntity {
    private String deviceCode;
    private String deviceName;
    private String namePinyin;
    private String deviceKind;
    private String deviceNo;
    private String model;
    private String sterTempClass;
    private Integer isActive;
    private String remark;
}