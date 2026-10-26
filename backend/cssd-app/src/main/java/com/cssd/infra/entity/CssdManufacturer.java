package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_manufacturer", schema = "cssd")
public class CssdManufacturer extends BaseEntity {
    /** 所属组织机构 id */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    private String mfrCode;
    private String mfrName;
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
    private Integer isActive;
    private String remark;
}
