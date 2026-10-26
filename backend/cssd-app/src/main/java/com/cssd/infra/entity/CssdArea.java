package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 院区（下一级），归属组织机构 cssd_campus。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_area", schema = "cssd")
public class CssdArea extends BaseEntity {
    /** 所属组织机构 id（cssd_campus.id） */
    private String orgId;
    private String areaCode;
    private String areaName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}
