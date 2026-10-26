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
    /** 年龄 */
    private Integer age;
    /** 学历 */
    private String education;
    /** 专业 */
    private String major;
    /** 职称 */
    private String jobTitle;
    /** 能级（层级） */
    private String energyLevel;
    private Integer isActive;
    private String remark;
}