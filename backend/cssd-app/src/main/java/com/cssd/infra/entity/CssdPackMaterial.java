package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_pack_material", schema = "cssd")
public class CssdPackMaterial extends BaseEntity {
    private String materialCode;
    private String materialName;
    private String namePinyin;
    private Integer shelfLifeDays;
    private String sterMethod;
    private Integer isActive;
    private String remark;
}