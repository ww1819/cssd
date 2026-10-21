package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public abstract class BaseEntity {
    @TableId(type = IdType.INPUT)
    private String id;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT)
    private String createdByNo;
    @TableField(fill = FieldFill.INSERT)
    private String createdByName;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedByNo;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedByName;
    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
    private OffsetDateTime deletedAt;
    private String deletedBy;
    private String deletedByNo;
    private String deletedByName;
    @TableField(fill = FieldFill.INSERT)
    private String createChannel;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateChannel;
    private String deleteChannel;
}
