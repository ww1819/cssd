package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_apply_bill", schema = "cssd")
public class CssdApplyBill extends BaseEntity {
    private String billNo;
    private String billStatus;
    private String deptId;
    private String deptCode;
    private String deptName;
    private String campusId;
    private String campusCode;
    private String campusName;
    private String applyType;
    private String surgeryCode;
    private String surgeryName;
    private String patientNo;
    private String patientName;
    private String bedNo;
    private String hisPatientId;
    private String hisEncounterId;
    private String hisApplyId;
    private OffsetDateTime needAt;
    private String applicantId;
    private String applicantNo;
    private String applicantName;
    private OffsetDateTime submittedAt;
    private String remark;
}