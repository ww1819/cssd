package com.cssd.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.ApiResult;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdAuthAccount;
import com.cssd.infra.entity.CssdCampus;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.mapper.CssdAuthAccountMapper;
import com.cssd.infra.mapper.CssdCampusMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.cssd.security.JwtService;
import com.cssd.security.LoginUser;
import com.cssd.security.OrgScope;
import com.cssd.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RestController
@RequestMapping("/api/auth")
public class AuthService {
    private final CssdStaffMapper staffMapper;
    private final CssdAuthAccountMapper accountMapper;
    private final CssdCampusMapper campusMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public AuthService(CssdStaffMapper staffMapper, CssdAuthAccountMapper accountMapper,
                       CssdCampusMapper campusMapper,
                       PasswordEncoder passwordEncoder, JwtService jwtService, ObjectMapper objectMapper) {
        this.staffMapper = staffMapper;
        this.accountMapper = accountMapper;
        this.campusMapper = campusMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String staffNo = body.getOrDefault("staffNo", body.get("username"));
        String password = body.get("password");
        if (staffNo == null || password == null) {
            throw new BizException("工号和密码不能为空");
        }
        CssdStaff staff = staffMapper.selectOne(new LambdaQueryWrapper<CssdStaff>()
                .eq(CssdStaff::getStaffNo, staffNo).eq(CssdStaff::getIsActive, 1));
        if (staff == null) {
            throw new BizException(401, "工号不存在或已停用");
        }
        CssdAuthAccount acc = accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                .eq(CssdAuthAccount::getStaffId, staff.getId()));
        if (acc == null || acc.getPasswordHash() == null
                || !passwordEncoder.matches(password, acc.getPasswordHash())) {
            throw new BizException(401, "密码错误");
        }
        LoginUser user = toLoginUser(staff);
        String token = jwtService.issue(user);
        List<String> menuAuth = parseMenuAuth(acc.getMenuAuth());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("staff", user);
        data.put("menus", menusOf(user.getRole()));
        data.put("menuAuth", menuAuth);
        return ApiResult.ok(data);
    }

    @GetMapping("/me")
    public ApiResult<Map<String, Object>> me() {
        LoginUser user = SecurityUtils.requireUser();
        CssdAuthAccount acc = accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                .eq(CssdAuthAccount::getStaffId, user.getStaffId())
                .last("limit 1"));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("staff", user);
        data.put("menus", menusOf(user.getRole()));
        data.put("menuAuth", acc == null ? null : parseMenuAuth(acc.getMenuAuth()));
        return ApiResult.ok(data);
    }

    private LoginUser toLoginUser(CssdStaff staff) {
        String userType = resolveUserType(staff);
        // 平台 admin：永远不带机构范围（防止历史 remark/org_id 误绑导致院区等列表被滤空）
        if (OrgScope.isPlatformStaffNo(staff.getStaffNo())) {
            return new LoginUser(staff.getId(), staff.getStaffNo(), staff.getStaffName(),
                    staff.getStaffRole(), staff.getDeptId(), null, null, userType);
        }
        String orgId = blankToNull(staff.getOrgId());
        String orgCode = blankToNull(staff.getOrgCode());
        // 兼容历史：remark=ORG:{id}
        if (orgId == null && staff.getRemark() != null && staff.getRemark().startsWith("ORG:")) {
            orgId = blankToNull(staff.getRemark().substring(4));
        }
        // 机构初始化账号 cssd_01 → 机构编码 01
        if (orgId == null && staff.getStaffNo() != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("^cssd_(\\d+)$", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(staff.getStaffNo().trim());
            if (m.matches()) {
                String code = m.group(1);
                CssdCampus byCode = campusMapper.selectOne(new LambdaQueryWrapper<CssdCampus>()
                        .isNull(CssdCampus::getOrgId)
                        .eq(CssdCampus::getCampusCode, code)
                        .last("limit 1"));
                if (byCode == null) {
                    // 兼容未补零编码，如 1
                    byCode = campusMapper.selectOne(new LambdaQueryWrapper<CssdCampus>()
                            .isNull(CssdCampus::getOrgId)
                            .eq(CssdCampus::getCampusCode, String.valueOf(Integer.parseInt(code)))
                            .last("limit 1"));
                }
                if (byCode != null) {
                    orgId = byCode.getId();
                    orgCode = byCode.getCampusCode();
                }
            }
        }
        if (orgId != null && orgCode == null) {
            CssdCampus campus = campusMapper.selectById(orgId);
            if (campus != null) {
                orgCode = campus.getCampusCode();
            }
        }
        // 懒回填 org_id / org_code / user_type，保证后续请求可隔离
        boolean needBackfill = orgId != null && (blankToNull(staff.getOrgId()) == null || blankToNull(staff.getOrgCode()) == null);
        boolean needUserType = blankToNull(staff.getUserType()) == null;
        if (needBackfill || needUserType) {
            try {
                if (needBackfill) {
                    staff.setOrgId(orgId);
                    staff.setOrgCode(orgCode);
                    if (staff.getRemark() == null || staff.getRemark().isBlank()) {
                        staff.setRemark("ORG:" + orgId);
                    }
                }
                if (needUserType) {
                    staff.setUserType(userType);
                }
                staffMapper.updateById(staff);
            } catch (Exception ignored) {
                // 列未迁移时忽略回填，仍把机构写入 LoginUser / JWT
            }
        }
        return new LoginUser(staff.getId(), staff.getStaffNo(), staff.getStaffName(),
                staff.getStaffRole(), staff.getDeptId(), orgId, orgCode, userType);
    }

    /** normal=普通用户，admin=管理员；cssd_* / 平台 admin 默认管理员 */
    private static String resolveUserType(CssdStaff staff) {
        String raw = blankToNull(staff.getUserType());
        if (raw != null) {
            return "admin".equalsIgnoreCase(raw) ? "admin" : "normal";
        }
        if (OrgScope.isPlatformStaffNo(staff.getStaffNo())) return "admin";
        if (staff.getStaffNo() != null && staff.getStaffNo().matches("(?i)^cssd_\\d+$")) return "admin";
        return "normal";
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    /** null = 不限制菜单；非空列表 = 仅可见授权项（可为空列表表示无菜单） */
    private List<String> parseMenuAuth(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            List<String> list = objectMapper.readValue(raw, new TypeReference<List<String>>() {});
            return list == null ? Collections.emptyList() : list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    static Object menusOf(String role) {
        return switch (role == null ? "" : role) {
            case "admin", "nurse_lead" -> java.util.List.of(
                    "md", "station", "clinical", "trace", "recall", "monitor", "report", "sys");
            case "operator" -> java.util.List.of("station", "trace");
            case "qc" -> java.util.List.of("monitor", "recall", "trace", "report");
            case "delivery" -> java.util.List.of("station-dist", "trace");
            case "clinical", "surgeon" -> java.util.List.of("clinical", "trace");
            default -> java.util.List.of("trace");
        };
    }
}
