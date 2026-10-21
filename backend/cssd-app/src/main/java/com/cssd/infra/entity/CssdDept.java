package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_dept", schema = "cssd")
public class CssdDept extends BaseEntity {
    private String deptCode;
    private String deptName;
    private String namePinyin;
    private String campusId;
    private String deptKind;
    private Integer isActive;
    private String remark;
}