package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_basket", schema = "cssd")
public class CssdBasket extends BaseEntity {
    private String basketCode;
    private String basketName;
    private String namePinyin;
    private String basketKind;
    private String colorCode;
    private String basketStatus;
    private Integer isActive;
    private String remark;
}