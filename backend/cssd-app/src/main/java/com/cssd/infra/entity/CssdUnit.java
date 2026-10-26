package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 计量单位，按组织机构隔离。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_unit", schema = "cssd")
public class CssdUnit extends BaseEntity {
    /** 所属组织机构 id */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    private String unitCode;
    private String unitName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}
