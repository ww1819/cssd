package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_ster_load_item", schema = "cssd")
public class CssdSterLoadItem extends BaseEntity {
    private String loadId;
    private String loadNo;
    private Integer lineNo;
    private String instanceId;
    private String barcode;
    private String templateId;
    private String templateCode;
    private String templateName;
    private String packCategory;
    private Integer isImplant;
    private LocalDate sterilizeDate;
    private LocalDate expireDate;
    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceNo;
}