package com.cssd.md;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cssd.common.BizException;
import com.cssd.common.IdUtils;
import com.cssd.common.PinyinCodeUtil;
import com.cssd.infra.entity.CssdAuthAccount;
import com.cssd.infra.entity.CssdCampus;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.mapper.CssdAuthAccountMapper;
import com.cssd.infra.mapper.CssdCampusMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 组织机构初始化：按机构顺序生成管理员账号 cssd_01 / cssd_02 …
 * 密码与账号一致；已初始化则不再重复建号。
 */
@Service
public class OrgAdminInitService {
    private static final String ORG_REMARK_PREFIX = "ORG:";
    public static final String DEFAULT_MENU = "station";

    private final CssdCampusMapper campusMapper;
    private final CssdStaffMapper staffMapper;
    private final CssdAuthAccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public OrgAdminInitService(CssdCampusMapper campusMapper, CssdStaffMapper staffMapper,
                               CssdAuthAccountMapper accountMapper, PasswordEncoder passwordEncoder,
                               ObjectMapper objectMapper) {
        this.campusMapper = campusMapper;
        this.staffMapper = staffMapper;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    public List<String> parseMenuKeys(String raw) {
        if (raw == null || raw.isBlank()) return Collections.emptyList();
        try {
            List<String> list = objectMapper.readValue(raw, new TypeReference<List<String>>() {});
            return list == null ? Collections.emptyList() : ensureStation(list);
        } catch (Exception e) {
            return List.of(DEFAULT_MENU);
        }
    }

    public String toMenuAuthJson(List<String> menuKeys) {
        try {
            return objectMapper.writeValueAsString(ensureStation(menuKeys));
        } catch (JsonProcessingException e) {
            throw new BizException(500, "菜单授权序列化失败");
        }
    }

    public static List<String> ensureStation(List<String> menuKeys) {
        Set<String> keys = new LinkedHashSet<>();
        keys.add(DEFAULT_MENU);
        if (menuKeys != null) {
            for (String k : menuKeys) {
                if (k != null && !k.isBlank()) keys.add(k.trim());
            }
        }
        return new ArrayList<>(keys);
    }

    @Transactional
    public Map<String, Object> initAdmin(String campusId, List<String> menuKeys) {
        CssdCampus campus = campusMapper.selectById(campusId);
        if (campus == null || (campus.getOrgId() != null && !campus.getOrgId().isBlank())) {
            throw new BizException(404, "组织机构不存在");
        }

        String remarkTag = ORG_REMARK_PREFIX + campusId;
        CssdStaff existing = staffMapper.selectOne(new LambdaQueryWrapper<CssdStaff>()
                .eq(CssdStaff::getRemark, remarkTag)
                .last("limit 1"));
        if (existing != null) {
            // 补齐机构隔离字段
            boolean staffDirty = false;
            if (existing.getOrgId() == null || existing.getOrgId().isBlank()) {
                existing.setOrgId(campusId);
                staffDirty = true;
            }
            if (existing.getOrgCode() == null || existing.getOrgCode().isBlank()) {
                existing.setOrgCode(campus.getCampusCode());
                staffDirty = true;
            }
            if (existing.getUserType() == null || existing.getUserType().isBlank()
                    || !"admin".equalsIgnoreCase(existing.getUserType())) {
                existing.setUserType("admin");
                staffDirty = true;
            }
            if (staffDirty) {
                staffMapper.updateById(existing);
            }
            CssdAuthAccount acc = accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                    .eq(CssdAuthAccount::getStaffId, existing.getId())
                    .last("limit 1"));
            // 入参有授权则写回机构并同步管理员账号；否则用机构已存授权同步账号
            List<String> incoming = ensureStation(menuKeys);
            List<String> existingMenus;
            if (incoming.size() > 1) {
                String json = toMenuAuthJson(incoming);
                campus.setMenuAuth(json);
                campusMapper.updateById(campus);
                if (acc != null) {
                    acc.setMenuAuth(json);
                    accountMapper.updateById(acc);
                }
                existingMenus = incoming;
            } else if (campus.getMenuAuth() != null && !campus.getMenuAuth().isBlank()) {
                existingMenus = parseMenuKeys(campus.getMenuAuth());
                if (acc != null) {
                    acc.setMenuAuth(toMenuAuthJson(existingMenus));
                    accountMapper.updateById(acc);
                }
            } else {
                existingMenus = acc == null ? List.of(DEFAULT_MENU) : parseMenuKeys(acc.getMenuAuth());
                // 回填机构授权，便于再次打开「授权」弹窗回显
                if (acc != null && acc.getMenuAuth() != null && !acc.getMenuAuth().isBlank()) {
                    campus.setMenuAuth(acc.getMenuAuth());
                    campusMapper.updateById(campus);
                }
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("alreadyInitialized", true);
            data.put("staffId", existing.getId());
            data.put("staffNo", existing.getStaffNo());
            data.put("password", existing.getStaffNo());
            data.put("staffName", existing.getStaffName());
            data.put("campusId", campusId);
            data.put("orgId", campusId);
            data.put("campusCode", campus.getCampusCode());
            data.put("orgCode", campus.getCampusCode());
            data.put("campusName", campus.getCampusName());
            data.put("menuAuth", existingMenus);
            return data;
        }

        List<CssdCampus> orgs = campusMapper.selectList(
                new QueryWrapper<CssdCampus>().isNull("org_id")
                        .orderByAsc("campus_code")
                        .orderByAsc("created_at"));
        int seq = -1;
        for (int i = 0; i < orgs.size(); i++) {
            if (campusId.equals(orgs.get(i).getId())) {
                seq = i + 1;
                break;
            }
        }
        if (seq < 0) {
            throw new BizException(404, "组织机构不存在");
        }

        String staffNo = allocateStaffNo(seq);
        String staffName = (campus.getCampusName() == null ? "机构" : campus.getCampusName()) + "管理员";

        CssdStaff staff = new CssdStaff();
        staff.setId(IdUtils.uuid7());
        staff.setStaffNo(staffNo);
        staff.setStaffName(staffName);
        staff.setNamePinyin(PinyinCodeUtil.initials(staffName));
        staff.setStaffRole("admin");
        staff.setUserType("admin");
        staff.setIsActive(1);
        staff.setOrgId(campusId);
        staff.setOrgCode(campus.getCampusCode());
        staff.setRemark(remarkTag);
        staffMapper.insert(staff);

        List<String> keys = ensureStation(menuKeys);
        // 优先用本次传入；若空则用机构上已保存的授权
        if (keys.size() <= 1 && campus.getMenuAuth() != null && !campus.getMenuAuth().isBlank()) {
            keys = parseMenuKeys(campus.getMenuAuth());
        }
        if (keys.size() <= 1) {
            throw new BizException(400, "请先完成菜单授权后再初始化");
        }
        String menuAuthJson = toMenuAuthJson(keys);

        CssdAuthAccount acc = new CssdAuthAccount();
        acc.setId(IdUtils.uuid7());
        acc.setStaffId(staff.getId());
        acc.setStaffNo(staffNo);
        acc.setPasswordHash(passwordEncoder.encode(staffNo));
        acc.setPasswordPlain(staffNo);
        acc.setMenuAuth(menuAuthJson);
        accountMapper.insert(acc);

        // 同步写回机构授权，便于下次打开弹窗回显
        campus.setMenuAuth(menuAuthJson);
        campusMapper.updateById(campus);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("alreadyInitialized", false);
        data.put("staffId", staff.getId());
        data.put("staffNo", staffNo);
        data.put("password", staffNo);
        data.put("staffName", staffName);
        data.put("campusId", campusId);
        data.put("orgId", campusId);
        data.put("campusCode", campus.getCampusCode());
        data.put("orgCode", campus.getCampusCode());
        data.put("campusName", campus.getCampusName());
        data.put("seq", seq);
        data.put("menuAuth", keys);
        return data;
    }

    /** 读取明文密码；无账号则空 */
    public String getPasswordPlain(String staffId) {
        CssdAuthAccount acc = accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                .eq(CssdAuthAccount::getStaffId, staffId)
                .last("limit 1"));
        if (acc == null) return null;
        if (acc.getPasswordPlain() != null && !acc.getPasswordPlain().isBlank()) {
            return acc.getPasswordPlain();
        }
        return null;
    }

    /**
     * 保存/创建登录账号并同步明文密码与哈希。
     * password 为空时：新建默认用工号；已有账号则不改密码。
     */
    @Transactional
    public String syncStaffPassword(CssdStaff staff, String password) {
        if (staff == null || staff.getId() == null || staff.getId().isBlank()) return null;
        String pwd = password == null ? null : password.trim();
        CssdAuthAccount acc = accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                .eq(CssdAuthAccount::getStaffId, staff.getId())
                .last("limit 1"));
        if (acc == null) {
            if (pwd == null || pwd.isBlank()) {
                pwd = staff.getStaffNo() == null || staff.getStaffNo().isBlank() ? "123456" : staff.getStaffNo().trim();
            }
            acc = new CssdAuthAccount();
            acc.setId(IdUtils.uuid7());
            acc.setStaffId(staff.getId());
            acc.setStaffNo(staff.getStaffNo());
            acc.setPasswordHash(passwordEncoder.encode(pwd));
            acc.setPasswordPlain(pwd);
            accountMapper.insert(acc);
            return pwd;
        }
        acc.setStaffNo(staff.getStaffNo());
        if (pwd != null && !pwd.isBlank()) {
            acc.setPasswordHash(passwordEncoder.encode(pwd));
            acc.setPasswordPlain(pwd);
        } else if (acc.getPasswordPlain() == null || acc.getPasswordPlain().isBlank()) {
            // 历史账号补齐明文展示（默认同工号，仅展示/兼容，不强制改哈希）
            String fallback = staff.getStaffNo() == null ? "" : staff.getStaffNo().trim();
            acc.setPasswordPlain(fallback);
        }
        accountMapper.updateById(acc);
        return acc.getPasswordPlain();
    }

    public CssdAuthAccount findAccountByStaffId(String staffId) {
        return accountMapper.selectOne(new LambdaQueryWrapper<CssdAuthAccount>()
                .eq(CssdAuthAccount::getStaffId, staffId)
                .last("limit 1"));
    }

    /** 读取人员菜单授权；无账号则空列表 */
    public List<String> getStaffMenuAuth(String staffId) {
        CssdAuthAccount acc = findAccountByStaffId(staffId);
        if (acc == null || acc.getMenuAuth() == null || acc.getMenuAuth().isBlank()) {
            return Collections.emptyList();
        }
        return parseMenuKeys(acc.getMenuAuth());
    }

    public List<String> getStaffDeptAuth(String staffId) {
        CssdAuthAccount acc = findAccountByStaffId(staffId);
        if (acc == null || acc.getDeptAuth() == null || acc.getDeptAuth().isBlank()) {
            return Collections.emptyList();
        }
        return parseMenuKeys(acc.getDeptAuth());
    }

    public List<String> getStaffWarehouseAuth(String staffId) {
        CssdAuthAccount acc = findAccountByStaffId(staffId);
        if (acc == null || acc.getWarehouseAuth() == null || acc.getWarehouseAuth().isBlank()) {
            return Collections.emptyList();
        }
        return parseMenuKeys(acc.getWarehouseAuth());
    }

    /**
     * 保存人员授权（菜单 / 科室 / 仓库）。
     * 菜单若所属机构已配置，则须为其子集。
     */
    @Transactional
    public Map<String, Object> saveStaffAuth(CssdStaff staff, List<String> menuKeys,
                                             List<String> deptIds, List<String> warehouseIds) {
        if (staff == null) throw new BizException(404, "人员不存在");
        List<String> keys = ensureStation(menuKeys);
        String orgId = staff.getOrgId();
        if (orgId != null && !orgId.isBlank()) {
            CssdCampus campus = campusMapper.selectById(orgId);
            if (campus != null && campus.getMenuAuth() != null && !campus.getMenuAuth().isBlank()) {
                Set<String> allowed = new LinkedHashSet<>(parseMenuKeys(campus.getMenuAuth()));
                allowed.add(DEFAULT_MENU);
                keys = keys.stream().filter(allowed::contains).collect(java.util.stream.Collectors.toList());
                keys = ensureStation(keys);
            }
        }
        List<String> depts = normalizeIdList(deptIds);
        List<String> warehouses = normalizeIdList(warehouseIds);
        String menuJson = toMenuAuthJson(keys);
        String deptJson = toMenuAuthJson(depts);
        String whJson = toMenuAuthJson(warehouses);
        CssdAuthAccount acc = findAccountByStaffId(staff.getId());
        if (acc == null) {
            String pwd = staff.getStaffNo() == null || staff.getStaffNo().isBlank() ? "123456" : staff.getStaffNo();
            acc = new CssdAuthAccount();
            acc.setId(IdUtils.uuid7());
            acc.setStaffId(staff.getId());
            acc.setStaffNo(staff.getStaffNo());
            acc.setPasswordHash(passwordEncoder.encode(pwd));
            acc.setPasswordPlain(pwd);
            acc.setMenuAuth(menuJson);
            acc.setDeptAuth(deptJson);
            acc.setWarehouseAuth(whJson);
            accountMapper.insert(acc);
        } else {
            acc.setMenuAuth(menuJson);
            acc.setDeptAuth(deptJson);
            acc.setWarehouseAuth(whJson);
            accountMapper.updateById(acc);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("menuAuth", keys);
        out.put("deptAuth", depts);
        out.put("warehouseAuth", warehouses);
        return out;
    }

    /** 兼容旧调用：仅保存菜单 */
    @Transactional
    public List<String> saveStaffMenuAuth(CssdStaff staff, List<String> menuKeys) {
        Map<String, Object> saved = saveStaffAuth(staff, menuKeys,
                getStaffDeptAuth(staff.getId()), getStaffWarehouseAuth(staff.getId()));
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>) saved.get("menuAuth");
        return keys;
    }

    /**
     * 将机构菜单授权同步到该机构初始化管理员账号（cssd_xx）。
     * 登录侧栏读的是 cssd_auth_account.menu_auth，仅改 campus.menu_auth 不会生效。
     */
    @Transactional
    public void syncOrgAdminMenuAuth(String campusId, List<String> menuKeys) {
        if (campusId == null || campusId.isBlank()) return;
        CssdStaff admin = findOrgAdminStaff(campusId);
        if (admin == null) return;
        CssdAuthAccount acc = findAccountByStaffId(admin.getId());
        if (acc == null) return;
        acc.setMenuAuth(toMenuAuthJson(menuKeys));
        accountMapper.updateById(acc);
    }

    /** 查找机构初始化管理员：remark=ORG:{id}，否则 org_id + cssd_* */
    public CssdStaff findOrgAdminStaff(String campusId) {
        if (campusId == null || campusId.isBlank()) return null;
        String remarkTag = ORG_REMARK_PREFIX + campusId;
        CssdStaff admin = staffMapper.selectOne(new LambdaQueryWrapper<CssdStaff>()
                .eq(CssdStaff::getRemark, remarkTag)
                .last("limit 1"));
        if (admin != null) return admin;
        return staffMapper.selectOne(new LambdaQueryWrapper<CssdStaff>()
                .eq(CssdStaff::getOrgId, campusId)
                .likeRight(CssdStaff::getStaffNo, "cssd_")
                .last("limit 1"));
    }

    private static List<String> normalizeIdList(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String id : ids) {
            if (id != null && !id.isBlank() && !"root".equals(id)) set.add(id.trim());
        }
        return new ArrayList<>(set);
    }

    /** 按机构序号分配 cssd_01…；若号已被其它机构占用则顺延 */
    private String allocateStaffNo(int seq) {
        int n = Math.max(seq, 1);
        for (int guard = 0; guard < 999; guard++, n++) {
            String candidate = String.format("cssd_%02d", n);
            CssdStaff hit = staffMapper.selectOne(new LambdaQueryWrapper<CssdStaff>()
                    .eq(CssdStaff::getStaffNo, candidate)
                    .last("limit 1"));
            if (hit == null) {
                return candidate;
            }
            String remark = hit.getRemark();
            if (remark != null && remark.startsWith(ORG_REMARK_PREFIX)) {
                continue;
            }
        }
        throw new BizException(500, "无法分配管理员账号");
    }
}
