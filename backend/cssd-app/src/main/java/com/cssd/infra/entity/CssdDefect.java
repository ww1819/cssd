package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_defect", schema = "cssd")
public class CssdDefect extends BaseEntity {
    private String defectNo;
    private String recycleBillId;
    private String recycleBillNo;
    private String recycleItemId;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private String deptId;
    private String deptCode;
    private String deptName;
    private String instrumentId;
    private String instrumentCode;
    private String instrumentName;
    private String spec;
    private String model;
    private String defectKind;
    private Integer qty;
    private String remark;
}