package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_instrument", schema = "cssd")
public class CssdInstrument extends BaseEntity {
    private String instrumentCode;
    private String instrumentName;
    private String namePinyin;
    private String spec;
    private String model;
    private String registerNo;
    private String barcode;
    private String ownerDeptId;
    private Integer isActive;
    private String remark;
}