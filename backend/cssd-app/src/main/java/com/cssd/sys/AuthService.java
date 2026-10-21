package com.cssd.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.ApiResult;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdAuthAccount;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.mapper.CssdAuthAccountMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.cssd.security.JwtService;
import com.cssd.security.LoginUser;
import com.cssd.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RestController
@RequestMapping("/api/auth")
public class AuthService {
    private final CssdStaffMapper staffMapper;
    private final CssdAuthAccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CssdStaffMapper staffMapper, CssdAuthAccountMapper accountMapper,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.staffMapper = staffMapper;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        LoginUser user = new LoginUser(staff.getId(), staff.getStaffNo(), staff.getStaffName(),
                staff.getStaffRole(), staff.getDeptId());
        String token = jwtService.issue(user);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("staff", user);
        data.put("menus", menusOf(user.getRole()));
        return ApiResult.ok(data);
    }

    @GetMapping("/me")
    public ApiResult<Map<String, Object>> me() {
        LoginUser user = SecurityUtils.requireUser();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("staff", user);
        data.put("menus", menusOf(user.getRole()));
        return ApiResult.ok(data);
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
