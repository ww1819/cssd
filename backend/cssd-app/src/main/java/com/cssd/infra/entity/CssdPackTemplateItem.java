package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_pack_template_item", schema = "cssd")
public class CssdPackTemplateItem extends BaseEntity {
    private String templateId;
    private String templateCode;
    private String templateName;
    private Integer lineNo;
    private String instrumentId;
    private String instrumentCode;
    private String instrumentName;
    private String spec;
    private String model;
    private Integer qty;
}