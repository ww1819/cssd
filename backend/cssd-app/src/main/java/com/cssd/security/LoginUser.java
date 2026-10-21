package com.cssd.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUser {
    private String staffId;
    private String staffNo;
    private String staffName;
    private String role;
    private String deptId;
}
