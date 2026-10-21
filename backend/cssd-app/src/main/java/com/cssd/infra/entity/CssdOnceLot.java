package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_once_lot", schema = "cssd")
public class CssdOnceLot extends BaseEntity {
    private String itemId;
    private String itemCode;
    private String itemName;
    private String spec;
    private String unitName;
    private String mfrId;
    private String mfrCode;
    private String mfrName;
    private String vendorId;
    private String vendorCode;
    private String vendorName;
    private String lotNo;
    private String sterLotNo;
    private LocalDate produceDate;
    private LocalDate expireDate;
    private Integer qtyOnHand;
    private Integer isBlocked;
    private String blockReason;
}