package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_cart", schema = "cssd")
public class CssdCart extends BaseEntity {
    private String cartCode;
    private String cartName;
    private String namePinyin;
    private Integer isActive;
    private String remark;
}