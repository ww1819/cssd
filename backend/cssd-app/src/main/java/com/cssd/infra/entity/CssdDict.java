package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_dict", schema = "cssd")
public class CssdDict extends BaseEntity {
    private String dictType;
    private String dictCode;
    private String dictLabel;
    private Integer sortNo;
    private Integer isActive;
    private String remark;
}