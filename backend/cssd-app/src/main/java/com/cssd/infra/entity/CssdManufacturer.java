package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_manufacturer", schema = "cssd")
public class CssdManufacturer extends BaseEntity {
    private String mfrCode;
    private String mfrName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}