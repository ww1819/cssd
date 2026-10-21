package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_recall_item", schema = "cssd")
public class CssdRecallItem extends BaseEntity {
    private String recallId;
    private String recallNo;
    private Integer lineNo;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private String locDeptId;
    private String locDeptCode;
    private String locDeptName;
    private String locCampusId;
    private String locCampusCode;
    private String locCampusName;
    private Integer usedFlag;
    private String patientNo;
    private String patientName;
    private String bedNo;
    private String surgeonId;
    private String surgeonNo;
    private String surgeonName;
}