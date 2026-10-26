package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_device", schema = "cssd")
public class CssdDevice extends BaseEntity {
    /** 所属组织机构 id */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    private String deviceCode;
    private String deviceName;
    private String namePinyin;
    /** 业务类型：washer/sterilizer/sealer（列表隐藏） */
    private String deviceKind;
    /** 锅号/设备号（业务用） */
    private String deviceNo;
    /** 规格 */
    private String spec;
    /** 型号 */
    private String model;
    /** 单位 */
    private String unit;
    /** 序列号 */
    private String serialNo;
    /** 设备类型（台账展示） */
    private String deviceType;
    /** 安装位置 */
    private String installLocation;
    /** 所在区域 */
    private String areaName;
    /** 设备能力参数 */
    private String capabilityParams;
    /** 启用日期 */
    private LocalDate enabledDate;
    /** 设备状态 */
    private String deviceStatus;
    /** 操作责任人（cssd_staff.id） */
    private String operatorId;
    /** 设备操作责任人姓名（冗余） */
    private String operatorName;
    /** 生产厂家ID */
    private String manufacturerId;
    /** 生产厂家名称（冗余） */
    private String manufacturerName;
    /** 供应商ID */
    private String vendorId;
    /** 供应商名称（冗余） */
    private String vendorName;
    /** 品牌 */
    private String brandName;
    /** 国别 */
    private String countryName;
    /** 医疗器械注册证号 */
    private String deviceRegNo;
    /** 生产日期 */
    private LocalDate productionDate;
    /** 原值（元） */
    private BigDecimal originalValue;
    /** 购置日期 */
    private LocalDate purchaseDate;
    /** 设备图片 JSON */
    private String deviceImages;
    /** 保养记录 JSON */
    private String maintainLogs;
    /** 不良事件 JSON */
    private String adverseEvents;
    /** 维保周期 */
    private String maintainCycle;
    /** 维保计划 */
    private String maintainPlan;
    /** 上次维保时间 */
    private LocalDate lastMaintainAt;
    /** 维保到期预警 */
    private String maintainExpireWarn;
    /** 检定证书编号 */
    private String inspectCertNo;
    /** 检定有效期（计量强检设备） */
    private LocalDate inspectValidUntil;
    /** 证书附件文件名 */
    private String certAttachment;
    /** 证书附件内容（base64，可选） */
    private String certAttachmentData;
    /** 灭菌温级 high/low（业务用，列表不展示） */
    private String sterTempClass;
    private Integer isActive;
    private String remark;
}
