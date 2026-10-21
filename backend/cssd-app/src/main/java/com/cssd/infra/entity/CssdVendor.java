package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_vendor", schema = "cssd")
public class CssdVendor extends BaseEntity {
    private String vendorCode;
    private String vendorName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}