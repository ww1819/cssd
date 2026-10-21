package com.cssd.flow;

import com.cssd.common.IdUtils;
import com.cssd.infra.entity.CssdDept;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.mapper.CssdDeptMapper;
import com.cssd.security.LoginUser;
import com.cssd.security.SecurityUtils;
import com.cssd.sys.BillNoService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class FlowSupport {
    private final CssdDeptMapper deptMapper;
    private final BillNoService billNoService;

    public FlowSupport(CssdDeptMapper deptMapper, BillNoService billNoService) {
        this.deptMapper = deptMapper;
        this.billNoService = billNoService;
    }

    public LoginUser user() {
        return SecurityUtils.requireUser();
    }

    public String billNo(String prefix) {
        return billNoService.next(prefix);
    }

    public String id() {
        return IdUtils.uuid7();
    }

    public CssdDept dept(String deptId) {
        CssdDept d = deptMapper.selectById(deptId);
        if (d == null) {
            throw new com.cssd.common.BizException("科室不存在");
        }
        return d;
    }

    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    public void requireStatus(CssdPackInstance inst, String... allowed) {
        for (String s : allowed) {
            if (s.equals(inst.getStatus())) {
                return;
            }
        }
        throw new com.cssd.common.BizException("当前状态不允许此操作: " + inst.getStatus());
    }

    public boolean isExpired(CssdPackInstance inst) {
        return inst.getExpireDate() != null && inst.getExpireDate().isBefore(java.time.LocalDate.now());
    }
}
