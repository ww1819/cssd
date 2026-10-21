package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_once_io", schema = "cssd")
public class CssdOnceIo extends BaseEntity {
    private String ioNo;
    private String ioKind;
    private String ioStatus;
    private String lotId;
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
    private LocalDate expireDate;
    private String deptId;
    private String deptCode;
    private String deptName;
    private Integer qty;
    private String operatorId;
    private String operatorNo;
    private String operatorName;
    private OffsetDateTime ioAt;
    private String remark;
}