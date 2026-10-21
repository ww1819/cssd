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
    private String namePinyin;
    private Integer isActive;
    private String remark;
}