package com.cssd.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private String staffId;
    private String staffNo;
    private String staffName;
    private String role;
    private String deptId;
    /** 所属组织机构 id；空=平台级（可见全部机构） */
    private String orgId;
    /** 所属组织机构编码 */
    private String orgCode;
    /** 用户类型：normal=普通用户，admin=管理员 */
    private String userType;
}
