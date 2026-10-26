package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    /** 明文密码（非表字段，来自/写入 cssd_auth_account.password_plain） */
    @TableField(exist = false)
    private String password;
    /** 所属组织机构 id（数据隔离） */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
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
    /** 联系人 */
    private String contactName;
    /** 联系电话 */
    private String contactPhone;
    /** QQ */
    private String qq;
    /** 电子邮箱 */
    private String email;
    /** 用户类型 */
    private String userType;
    private Integer isActive;
    private String remark;
}