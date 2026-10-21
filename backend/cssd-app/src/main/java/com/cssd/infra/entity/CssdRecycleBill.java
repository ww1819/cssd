package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_recycle_bill", schema = "cssd")
public class CssdRecycleBill extends BaseEntity {
    private String billNo;
    private String billStatus;
    private String deptId;
    private String deptCode;
    private String deptName;
    private String campusId;
    private String campusCode;
    private String campusName;
    private OffsetDateTime recycAt;
    private String recycStaffId;
    private String recycStaffNo;
    private String recycStaffName;
    private String applyBillId;
    private String applyBillNo;
    private String sourceKind;
    private String remark;
}