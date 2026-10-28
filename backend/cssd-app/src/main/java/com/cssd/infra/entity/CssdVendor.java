package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_vendor", schema = "cssd")
public class CssdVendor extends BaseEntity {
    /** 所属组织机构 id */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    private String vendorCode;
    private String vendorName;
    private String namePinyin;
    /** 简码 */
    private String shortCode;
    /** 统一社会信用代码 */
    private String socialCreditCode;
    /** 法人 */
    private String legalPerson;
    /** 业务员 */
    private String salesman;
    /** 联系电话 */
    private String contactPhone;
    /** 公司地址 */
    private String companyAddress;
    /** 邮箱 */
    private String email;
    /** 注册资金 */
    private String registeredCapital;
    /** 公司负责人 */
    private String companyPrincipal;
    /** 经营范围 */
    private String businessScope;
    /** 传真 */
    private String fax;
    /** 网址 */
    private String website;
    /**
     * 供应商类型（多选，逗号分隔）：
     * external_instrument / implant / reusable_owned / cssd_consumable / device_maintain
     */
    private String vendorTypes;
    private Integer isActive;
    private String remark;
}
