package com.cssd.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.infra.entity.CssdCampus;
import com.cssd.infra.mapper.CssdCampusMapper;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析当前登录用户的组织机构范围。
 * 平台账号（无机构）返回 null；机构账号返回 cssd_campus.id。
 */
@Component
public class OrgScope {
    private static final Pattern CSSD_NO = Pattern.compile("^cssd_(\\d+)$", Pattern.CASE_INSENSITIVE);

    private final CssdCampusMapper campusMapper;

    public OrgScope(CssdCampusMapper campusMapper) {
        this.campusMapper = campusMapper;
    }

    /** 平台管理员工号：始终全局可见，忽略 JWT/本地残留的 orgId */
    public static boolean isPlatformStaffNo(String staffNo) {
        return staffNo != null && "admin".equalsIgnoreCase(staffNo.trim());
    }

    /** 当前请求所属机构 id；null=平台可见全部 */
    public String currentOrgId() {
        LoginUser u = SecurityUtils.current();
        if (u == null) return null;
        if (isPlatformStaffNo(u.getStaffNo())) {
            u.setOrgId(null);
            u.setOrgCode(null);
            return null;
        }
        if (u.getOrgId() != null && !u.getOrgId().isBlank()) {
            return u.getOrgId();
        }
        String resolved = resolveOrgId(u.getStaffNo(), u.getOrgCode());
        if (resolved != null) {
            u.setOrgId(resolved);
            if (u.getOrgCode() == null || u.getOrgCode().isBlank()) {
                CssdCampus c = campusMapper.selectById(resolved);
                if (c != null) u.setOrgCode(c.getCampusCode());
            }
        }
        return resolved;
    }

    public String currentOrgCode() {
        LoginUser u = SecurityUtils.current();
        if (u == null) return null;
        if (u.getOrgCode() != null && !u.getOrgCode().isBlank()) return u.getOrgCode();
        currentOrgId(); // 顺带解析
        return u.getOrgCode();
    }

    public boolean isPlatform() {
        return currentOrgId() == null;
    }

    public void requireAccess(String orgId) {
        String scope = currentOrgId();
        if (scope != null && (orgId == null || !scope.equals(orgId))) {
            throw new com.cssd.common.BizException(403, "无权操作其他组织机构数据");
        }
    }

    private String resolveOrgId(String staffNo, String orgCode) {
        String code = orgCode;
        if ((code == null || code.isBlank()) && staffNo != null) {
            Matcher m = CSSD_NO.matcher(staffNo.trim());
            if (m.matches()) code = m.group(1);
        }
        if (code == null || code.isBlank()) return null;
        CssdCampus byCode = campusMapper.selectOne(new LambdaQueryWrapper<CssdCampus>()
                .isNull(CssdCampus::getOrgId)
                .eq(CssdCampus::getCampusCode, code)
                .last("limit 1"));
        if (byCode != null) return byCode.getId();
        try {
            String bare = String.valueOf(Integer.parseInt(code));
            if (!bare.equals(code)) {
                byCode = campusMapper.selectOne(new LambdaQueryWrapper<CssdCampus>()
                        .isNull(CssdCampus::getOrgId)
                        .eq(CssdCampus::getCampusCode, bare)
                        .last("limit 1"));
                if (byCode != null) return byCode.getId();
            }
        } catch (NumberFormatException ignored) {
            // ignore
        }
        return null;
    }
}
