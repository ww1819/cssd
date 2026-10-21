package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_pack_template", schema = "cssd")
public class CssdPackTemplate extends BaseEntity {
    private String templateCode;
    private String templateName;
    private String namePinyin;
    private String packCategory;
    private String ownerDeptId;
    private String ownerDeptCode;
    private String ownerDeptName;
    private String materialId;
    private String materialCode;
    private String materialName;
    private Integer shelfLifeDays;
    private Integer basketLoadQty;
    private Integer safetyStockQty;
    private Integer needWash;
    private String washMode;
    private Integer needPack;
    private Integer needStore;
    private Integer needRecycle;
    private Integer isImplant;
    private String imageUrl;
    private String checkImageUrl;
    private String sterMethod;
    private Integer isActive;
    private String remark;
}