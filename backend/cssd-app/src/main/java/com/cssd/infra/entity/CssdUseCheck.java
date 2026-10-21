package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_use_check", schema = "cssd")
public class CssdUseCheck extends BaseEntity {
    private String checkNo;
    private String checkKind;
    private String checkStatus;
    private String deptId;
    private String deptCode;
    private String deptName;
    private String campusId;
    private String campusCode;
    private String campusName;
    private String applyBillId;
    private String applyBillNo;
    private String patientNo;
    private String patientName;
    private String bedNo;
    private String surgeonId;
    private String surgeonNo;
    private String surgeonName;
    private String surgeryCode;
    private String surgeryName;
    private String hisPatientId;
    private String hisEncounterId;
    private String idempotencyKey;
    private String checkerId;
    private String checkerNo;
    private String checkerName;
    private OffsetDateTime checkedAt;
    private String remark;
}