package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_print_template", schema = "cssd")
public class CssdPrintTemplate extends BaseEntity {
    private String templateCode;
    private String templateName;
    private String carrierKind;
    private String labelPhase;
    private BigDecimal paperWidthMm;
    private BigDecimal paperHeightMm;
    private String layoutJson;
    private Integer isActive;
    private String remark;
}