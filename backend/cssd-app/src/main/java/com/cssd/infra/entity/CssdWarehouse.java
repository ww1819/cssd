package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库：归属组织机构，可选挂院区（campusId 存 cssd_area.id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_warehouse", schema = "cssd")
public class CssdWarehouse extends BaseEntity {
    /** 所属组织机构 id（cssd_campus.id） */
    private String orgId;
    /** 所属组织机构编码（冗余） */
    private String orgCode;
    /** 所属院区 id（cssd_area.id，可空） */
    private String campusId;
    private String warehouseCode;
    private String warehouseName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}
