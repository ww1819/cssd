package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_once_item", schema = "cssd")
public class CssdOnceItem extends BaseEntity {
    private String itemCode;
    private String itemName;
    private String namePinyin;
    private String spec;
    private String unitName;
    private String mfrId;
    private String mfrCode;
    private String mfrName;
    private String vendorId;
    private String vendorCode;
    private String vendorName;
    private Integer minStock;
    private Integer isActive;
    private String remark;
}