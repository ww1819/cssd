package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_use_check_item", schema = "cssd")
public class CssdUseCheckItem extends BaseEntity {
    private String checkId;
    private String checkNo;
    private Integer lineNo;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private LocalDate sterilizeDate;
    private LocalDate expireDate;
    private String deviceNo;
    private Integer qtyOk;
    private Integer sterOk;
    private Integer itemQty;
    private String remark;
}