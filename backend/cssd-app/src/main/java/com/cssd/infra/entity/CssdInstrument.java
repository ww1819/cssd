package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_instrument", schema = "cssd")
public class CssdInstrument extends BaseEntity {
    /** 所属组织机构 id */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    private String instrumentCode;
    private String instrumentName;
    private String namePinyin;
    /** 简码 */
    private String shortCode;
    /** 别名 */
    private String aliasName;
    /** 英文名称 */
    private String englishName;
    /** 品牌 */
    private String brandName;
    private String spec;
    private String model;
    /** UDI 唯一器械标识 */
    private String udi;
    private String registerNo;
    /** 注册证有效期 */
    private LocalDate registerValidUntil;
    private String barcode;
    /** 材质 */
    private String materialName;
    /** 器械分类 */
    private String instrumentCategory;
    /** 器械类型 */
    private String instrumentType;
    /** 灭菌方式 */
    private String sterMethod;
    /** 清洗方式 */
    private String washMethod;
    /** 包装要求 */
    private String packRequirement;
    /** 单价 */
    private BigDecimal costPrice;
    /** 计量单位 */
    private String unitId;
    private String unitName;
    /** 生产厂家 */
    private String manufacturerId;
    private String manufacturerName;
    /** 供应商 */
    private String vendorId;
    private String vendorName;
    /** 存放属性 */
    private String storageAttr;
    /** 高值器械：1是/0否 */
    private Integer isHighValue;
    /** 重点监控器械：1是/0否 */
    private Integer isKeyMonitor;
    /** 当前状态 */
    private String currentStatus;
    private String ownerDeptId;
    private Integer isActive;
    /** 备注 / 特殊注意事项 */
    private String remark;
    /** 器械图片 JSON[{name,dataUrl}] */
    private String instrumentImages;
    /** 器械附件 JSON[{name,dataUrl}] */
    private String instrumentAttachments;
}
