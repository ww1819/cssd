package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_auth_account", schema = "cssd")
public class CssdAuthAccount extends BaseEntity {
    private String staffId;
    private String staffNo;
    private String passwordHash;
}
