package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_dist_item", schema = "cssd")
public class CssdDistItem extends BaseEntity {
    private String billId;
    private String billNo;
    private Integer lineNo;
    private String applyBillId;
    private String applyBillNo;
    private String applyItemId;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private LocalDate sterilizeDate;
    private LocalDate expireDate;
    private String deviceNo;
    private String loadNo;
}