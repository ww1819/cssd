package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_sys_config", schema = "cssd")
public class CssdSysConfig extends BaseEntity {
    private String configKey;
    private String configValue;
    private String valueLabel;
    private String remark;
}