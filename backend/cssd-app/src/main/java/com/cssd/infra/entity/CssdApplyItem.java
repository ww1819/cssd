package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_apply_item", schema = "cssd")
public class CssdApplyItem extends BaseEntity {
    private String billId;
    private String billNo;
    private Integer lineNo;
    private String templateId;
    private String templateCode;
    private String templateName;
    private Integer qty;
    private Integer distQty;
    private Integer recycleQty;
}