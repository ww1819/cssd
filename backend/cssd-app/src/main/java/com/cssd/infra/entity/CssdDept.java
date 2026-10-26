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
    /** 上级科室 id */
    private String parentId;
    /** 位置 */
    private String location;
    /** 责任人 */
    private String contactName;
    /** 责任人电话 */
    private String contactPhone;
    /** 科室电话 */
    private String deptPhone;
    private Integer isActive;
    private String remark;
}