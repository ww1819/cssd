package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_pack_instance_item", schema = "cssd")
public class CssdPackInstanceItem extends BaseEntity {
    private String instanceId;
    private String barcode;
    private Integer lineNo;
    private String instrumentId;
    private String instrumentCode;
    private String instrumentName;
    private String spec;
    private String model;
    private String registerNo;
    private Integer qty;
    private Integer actualQty;
}