package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_wash_item", schema = "cssd")
public class CssdWashItem extends BaseEntity {
    private String batchId;
    private String batchNo;
    private Integer lineNo;
    private String basketId;
    private String basketCode;
    private String basketName;
    private String recycleBillId;
    private String recycleBillNo;
    private String recycleItemId;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private String remark;
}