package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_staff", schema = "cssd")
public class CssdStaff extends BaseEntity {
    private String staffNo;
    private String staffName;
    private String namePinyin;
    private String deptId;
    private String staffRole;
    private Integer isActive;
    private String remark;
}