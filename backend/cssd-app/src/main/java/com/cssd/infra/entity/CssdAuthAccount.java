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
    /** 明文密码（管理端展示；登录仍用 passwordHash） */
    private String passwordPlain;
    /** 菜单授权 JSON 数组；null/空=不限制 */
    private String menuAuth;
    /** 科室授权 JSON 数组（dept id） */
    private String deptAuth;
    /** 仓库授权 JSON 数组（warehouse id） */
    private String warehouseAuth;
}
