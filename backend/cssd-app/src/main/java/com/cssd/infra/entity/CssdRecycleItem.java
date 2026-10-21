package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_recycle_item", schema = "cssd")
public class CssdRecycleItem extends BaseEntity {
    private String billId;
    private String billNo;
    private Integer lineNo;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private String applyBillId;
    private String applyBillNo;
    private String applyItemId;
    private String distBillId;
    private String distBillNo;
    private String distItemId;
    private String basketId;
    private String basketCode;
    private String basketName;
    private Integer qty;
    private Integer packedQty;
    private Integer urgentFlag;
    private Integer contaminationFlag;
    private String infectionType;
    private Integer hasDefect;
    private String defectRemark;
}