package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_campus", schema = "cssd")
public class CssdCampus extends BaseEntity {
    private String campusCode;
    private String campusName;
    /** 所属组织机构 id（空=组织机构本身；有值=院区） */
    private String orgId;
    /** 机构代码 */
    private String orgCode;
    /** 社会统一信用代码 */
    private String socialCreditCode;
    private String address;
    private String contactName;
    private String contactPhone;
    private String namePinyin;
    private Integer isActive;
    private String remark;
    /** 机构菜单授权 JSON 数组 */
    private String menuAuth;
}