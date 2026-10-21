package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_dist_bill", schema = "cssd")
public class CssdDistBill extends BaseEntity {
    private String billNo;
    private String billStatus;
    private String distMode;
    private String deptId;
    private String deptCode;
    private String deptName;
    private String campusId;
    private String campusCode;
    private String campusName;
    private String applyBillId;
    private String applyBillNo;
    private String cartId;
    private String cartCode;
    private String cartName;
    private String distStaffId;
    private String distStaffNo;
    private String distStaffName;
    private OffsetDateTime distAt;
    private String receiverId;
    private String receiverNo;
    private String receiverName;
    private OffsetDateTime receivedAt;
    private String receivePlace;
    private String remark;
}