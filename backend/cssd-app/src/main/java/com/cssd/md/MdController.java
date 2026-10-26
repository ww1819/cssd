package com.cssd.md;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cssd.common.ApiResult;
import com.cssd.common.IdUtils;
import com.cssd.common.PageQuery;
import com.cssd.common.PinyinCodeUtil;
import com.cssd.infra.entity.BaseEntity;
import com.cssd.infra.entity.CssdBasket;
import com.cssd.infra.entity.CssdArea;
import com.cssd.infra.entity.CssdCampus;
import com.cssd.infra.entity.CssdCart;
import com.cssd.infra.entity.CssdDept;
import com.cssd.infra.entity.CssdDevice;
import com.cssd.infra.entity.CssdDeviceProgram;
import com.cssd.infra.entity.CssdInstrument;
import com.cssd.infra.entity.CssdManufacturer;
import com.cssd.infra.entity.CssdOnceItem;
import com.cssd.infra.entity.CssdOnceLot;
import com.cssd.infra.entity.CssdPackMaterial;
import com.cssd.infra.entity.CssdPackTemplate;
import com.cssd.infra.entity.CssdPackTemplateItem;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.entity.CssdVendor;
import com.cssd.infra.entity.CssdWarehouse;
import com.cssd.infra.mapper.CssdAreaMapper;
import com.cssd.infra.mapper.CssdBasketMapper;
import com.cssd.infra.mapper.CssdCampusMapper;
import com.cssd.infra.mapper.CssdCartMapper;
import com.cssd.infra.mapper.CssdDeptMapper;
import com.cssd.infra.mapper.CssdWarehouseMapper;
import com.cssd.infra.mapper.CssdDeviceMapper;
import com.cssd.infra.mapper.CssdDeviceProgramMapper;
import com.cssd.infra.mapper.CssdInstrumentMapper;
import com.cssd.infra.mapper.CssdManufacturerMapper;
import com.cssd.infra.mapper.CssdOnceItemMapper;
import com.cssd.infra.mapper.CssdOnceLotMapper;
import com.cssd.infra.mapper.CssdPackMaterialMapper;
import com.cssd.infra.mapper.CssdPackTemplateItemMapper;
import com.cssd.infra.mapper.CssdPackTemplateMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.cssd.infra.mapper.CssdVendorMapper;
import com.cssd.security.LoginUser;
import com.cssd.security.OrgScope;
import com.cssd.security.SecurityUtils;
import com.cssd.sys.ChangeLogService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/md")
public class MdController {
    private final ChangeLogService changeLog;
    private final CssdCampusMapper campusMapper;
    private final CssdAreaMapper areaMapper;
    private final CssdDeptMapper deptMapper;
    private final CssdStaffMapper staffMapper;
    private final CssdVendorMapper vendorMapper;
    private final CssdManufacturerMapper manufacturerMapper;
    private final CssdPackMaterialMapper materialMapper;
    private final CssdInstrumentMapper instrumentMapper;
    private final CssdPackTemplateMapper templateMapper;
    private final CssdPackTemplateItemMapper templateItemMapper;
    private final CssdDeviceMapper deviceMapper;
    private final CssdDeviceProgramMapper programMapper;
    private final CssdBasketMapper basketMapper;
    private final CssdCartMapper cartMapper;
    private final CssdOnceItemMapper onceItemMapper;
    private final CssdOnceLotMapper onceLotMapper;
    private final CssdWarehouseMapper warehouseMapper;
    private final OrgAdminInitService orgAdminInitService;
    private final OrgScope orgScope;

    public MdController(ChangeLogService changeLog, CssdCampusMapper campusMapper, CssdAreaMapper areaMapper,
                        CssdDeptMapper deptMapper,
                        CssdStaffMapper staffMapper, CssdVendorMapper vendorMapper,
                        CssdManufacturerMapper manufacturerMapper, CssdPackMaterialMapper materialMapper,
                        CssdInstrumentMapper instrumentMapper, CssdPackTemplateMapper templateMapper,
                        CssdPackTemplateItemMapper templateItemMapper, CssdDeviceMapper deviceMapper,
                        CssdDeviceProgramMapper programMapper, CssdBasketMapper basketMapper, CssdCartMapper cartMapper,
                        CssdOnceItemMapper onceItemMapper, CssdOnceLotMapper onceLotMapper,
                        CssdWarehouseMapper warehouseMapper,
                        OrgAdminInitService orgAdminInitService, OrgScope orgScope) {
        this.changeLog = changeLog;
        this.campusMapper = campusMapper;
        this.areaMapper = areaMapper;
        this.deptMapper = deptMapper;
        this.staffMapper = staffMapper;
        this.vendorMapper = vendorMapper;
        this.manufacturerMapper = manufacturerMapper;
        this.materialMapper = materialMapper;
        this.instrumentMapper = instrumentMapper;
        this.templateMapper = templateMapper;
        this.templateItemMapper = templateItemMapper;
        this.deviceMapper = deviceMapper;
        this.programMapper = programMapper;
        this.basketMapper = basketMapper;
        this.cartMapper = cartMapper;
        this.onceItemMapper = onceItemMapper;
        this.onceLotMapper = onceLotMapper;
        this.warehouseMapper = warehouseMapper;
        this.orgAdminInitService = orgAdminInitService;
        this.orgScope = orgScope;
    }

    /** 当前登录用户所属机构；空=平台账号，可见全部（支持 cssd_01→机构编码解析） */
    private String scopedOrgId() {
        return orgScope.currentOrgId();
    }

    private void requireOrgAccess(String orgId) {
        orgScope.requireAccess(orgId);
    }

    /** 组织机构（上一级） */
    @GetMapping("/campuses")
    public ApiResult<?> campuses(PageQuery q) {
        // cssd_campus 仅作组织机构；兼容旧数据时排除误写入的院区行（org_id 非空）
        QueryWrapper<CssdCampus> w = new QueryWrapper<CssdCampus>().orderByDesc("created_at").isNull("org_id");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("id", scope);
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("campus_code", kw).or().like("campus_name", kw).or().like("name_pinyin", kw));
        }
        return ApiResult.ok(campusMapper.selectPage(new Page<>(q.getPage(), q.getSize()), w));
    }

    @PostMapping("/campuses")
    public ApiResult<CssdCampus> saveCampus(@RequestBody CssdCampus e) {
        String scope = scopedOrgId();
        if (scope != null) {
            // 机构账号仅可改本机构，不可新增其他机构
            if (e.getId() == null || e.getId().isBlank() || !scope.equals(e.getId())) {
                throw new com.cssd.common.BizException(403, "无权维护其他组织机构");
            }
        }
        e.setNamePinyin(PinyinCodeUtil.initials(e.getCampusName()));
        e.setOrgId(null);
        return ApiResult.ok(upsert(campusMapper, e, "campus"));
    }

    @DeleteMapping("/campuses/{id}")
    public ApiResult<Void> delCampus(@PathVariable String id) {
        requireOrgAccess(id);
        if (scopedOrgId() != null) {
            throw new com.cssd.common.BizException(403, "机构账号不可删除组织机构");
        }
        campusMapper.deleteById(id);
        return ApiResult.ok();
    }

    /** 保存组织机构菜单授权 */
    @PutMapping("/campuses/{id}/menu-auth")
    public ApiResult<?> saveCampusMenuAuth(@PathVariable String id, @RequestBody Map<String, Object> body) {
        requireOrgAccess(id);
        CssdCampus campus = campusMapper.selectById(id);
        if (campus == null || (campus.getOrgId() != null && !campus.getOrgId().isBlank())) {
            throw new com.cssd.common.BizException(404, "组织机构不存在");
        }
        @SuppressWarnings("unchecked")
        List<String> menuKeys = body == null ? null : (List<String>) body.get("menuKeys");
        campus.setMenuAuth(orgAdminInitService.toMenuAuthJson(menuKeys));
        campusMapper.updateById(campus);
        return ApiResult.ok(Map.of("id", id, "menuAuth", orgAdminInitService.parseMenuKeys(campus.getMenuAuth())));
    }

    /** 组织机构初始化：生成管理员账号 cssd_01 / cssd_02…（密码同账号），并绑定菜单授权 */
    @PostMapping("/campuses/{id}/init-admin")
    public ApiResult<?> initCampusAdmin(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
        // 仅平台账号可初始化机构
        if (scopedOrgId() != null) {
            throw new com.cssd.common.BizException(403, "机构账号不可执行初始化");
        }
        @SuppressWarnings("unchecked")
        List<String> menuKeys = body == null ? null : (List<String>) body.get("menuKeys");
        return ApiResult.ok(orgAdminInitService.initAdmin(id, menuKeys));
    }

    /** 读取人员授权（菜单 / 科室 / 仓库） */
    @GetMapping("/staffs/{id}/menu-auth")
    public ApiResult<?> getStaffMenuAuth(@PathVariable String id) {
        CssdStaff staff = staffMapper.selectById(id);
        if (staff == null) throw new com.cssd.common.BizException(404, "人员不存在");
        requireOrgAccess(staff.getOrgId());
        List<String> menuAuth = orgAdminInitService.getStaffMenuAuth(id);
        List<String> deptAuth = orgAdminInitService.getStaffDeptAuth(id);
        List<String> warehouseAuth = orgAdminInitService.getStaffWarehouseAuth(id);
        List<String> orgMenuAuth = List.of();
        if (staff.getOrgId() != null && !staff.getOrgId().isBlank()) {
            CssdCampus campus = campusMapper.selectById(staff.getOrgId());
            if (campus != null) orgMenuAuth = orgAdminInitService.parseMenuKeys(campus.getMenuAuth());
        }
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("id", id);
        data.put("menuAuth", menuAuth);
        data.put("deptAuth", deptAuth);
        data.put("warehouseAuth", warehouseAuth);
        data.put("orgMenuAuth", orgMenuAuth);
        data.put("orgId", staff.getOrgId());
        return ApiResult.ok(data);
    }

    /** 保存人员授权（菜单 / 科室 / 仓库；菜单须落在所属机构已授权范围内） */
    @PutMapping("/staffs/{id}/menu-auth")
    public ApiResult<?> saveStaffMenuAuth(@PathVariable String id, @RequestBody Map<String, Object> body) {
        CssdStaff staff = staffMapper.selectById(id);
        if (staff == null) throw new com.cssd.common.BizException(404, "人员不存在");
        requireOrgAccess(staff.getOrgId());
        @SuppressWarnings("unchecked")
        List<String> menuKeys = body == null ? null : (List<String>) body.get("menuKeys");
        @SuppressWarnings("unchecked")
        List<String> deptIds = body == null ? null : (List<String>) body.get("deptIds");
        @SuppressWarnings("unchecked")
        List<String> warehouseIds = body == null ? null : (List<String>) body.get("warehouseIds");
        Map<String, Object> saved = orgAdminInitService.saveStaffAuth(staff, menuKeys, deptIds, warehouseIds);
        saved.put("id", id);
        return ApiResult.ok(saved);
    }

    /** 院区（下一级，独立表 cssd_area） */
    @GetMapping("/areas")
    public ApiResult<?> areas(PageQuery q) {
        QueryWrapper<CssdArea> w = new QueryWrapper<CssdArea>().orderByDesc("created_at");
        String scope = scopedOrgId();
        // 机构账号：强制本机构；平台账号：仅当明确传 orgId 时过滤（列表页不要误传残留 orgId）
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("area_code", kw).or().like("area_name", kw).or().like("name_pinyin", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(areaMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/areas")
    public ApiResult<CssdArea> saveArea(@RequestBody CssdArea e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        e.setNamePinyin(PinyinCodeUtil.initials(e.getAreaName()));
        boolean creating = e.getId() == null || e.getId().isBlank();
        if (e.getAreaCode() != null && !e.getAreaCode().isBlank()) {
            QueryWrapper<CssdArea> dup = new QueryWrapper<CssdArea>()
                    .eq("org_id", e.getOrgId())
                    .eq("area_code", e.getAreaCode().trim());
            if (!creating) {
                dup.ne("id", e.getId());
            }
            if (areaMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下院区编码已存在：" + e.getAreaCode().trim());
            }
        }
        return ApiResult.ok(upsert(areaMapper, e, "area"));
    }

    @DeleteMapping("/areas/{id}")
    public ApiResult<Void> delArea(@PathVariable String id) {
        CssdArea area = areaMapper.selectById(id);
        if (area != null) requireOrgAccess(area.getOrgId());
        areaMapper.deleteById(id);
        return ApiResult.ok();
    }

    /** 仓库：按组织机构隔离；admin 可见全部，机构账号仅本机构 */
    @GetMapping("/warehouses")
    public ApiResult<?> warehouses(PageQuery q) {
        QueryWrapper<CssdWarehouse> w = new QueryWrapper<CssdWarehouse>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getCampusId() != null && !q.getCampusId().isBlank()) {
            w.eq("campus_id", q.getCampusId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("warehouse_code", kw).or().like("warehouse_name", kw).or().like("name_pinyin", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(warehouseMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/warehouses")
    public ApiResult<CssdWarehouse> saveWarehouse(@RequestBody CssdWarehouse e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        // 未显式传机构时，从所属院区反查组织机构
        if ((e.getOrgId() == null || e.getOrgId().isBlank())
                && e.getCampusId() != null && !e.getCampusId().isBlank()) {
            CssdArea area = areaMapper.selectById(e.getCampusId());
            if (area != null) {
                e.setOrgId(area.getOrgId());
            }
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "无法确定所属组织机构，请选择院区");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        if (e.getCampusId() != null && !e.getCampusId().isBlank()) {
            CssdArea area = areaMapper.selectById(e.getCampusId());
            if (area == null) {
                throw new com.cssd.common.BizException(400, "所属院区不存在");
            }
            if (!e.getOrgId().equals(area.getOrgId())) {
                throw new com.cssd.common.BizException(400, "所属院区不属于当前组织机构");
            }
        }
        e.setNamePinyin(PinyinCodeUtil.initials(e.getWarehouseName()));
        boolean creating = e.getId() == null || e.getId().isBlank();
        if (e.getWarehouseCode() != null && !e.getWarehouseCode().isBlank()) {
            QueryWrapper<CssdWarehouse> dup = new QueryWrapper<CssdWarehouse>()
                    .eq("org_id", e.getOrgId())
                    .eq("warehouse_code", e.getWarehouseCode().trim());
            if (!creating) {
                dup.ne("id", e.getId());
            }
            if (warehouseMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下仓库编码已存在：" + e.getWarehouseCode().trim());
            }
        }
        return ApiResult.ok(upsert(warehouseMapper, e, "warehouse"));
    }

    @DeleteMapping("/warehouses/{id}")
    public ApiResult<Void> delWarehouse(@PathVariable String id) {
        CssdWarehouse wh = warehouseMapper.selectById(id);
        if (wh != null) requireOrgAccess(wh.getOrgId());
        warehouseMapper.deleteById(id);
        return ApiResult.ok();
    }

    @GetMapping("/depts")
    public ApiResult<?> depts(PageQuery q) {
        QueryWrapper<CssdDept> w = new QueryWrapper<CssdDept>().orderByAsc("dept_code");
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("dept_code", kw).or().like("dept_name", kw).or().like("name_pinyin", kw));
        }
        if (q.getCampusId() != null && !q.getCampusId().isBlank()) {
            w.eq("campus_id", q.getCampusId());
        }
        String orgId = scopedOrgId();
        if (orgId == null && q.getOrgId() != null && !q.getOrgId().isBlank()) {
            orgId = q.getOrgId();
        }
        if (orgId != null && !orgId.isBlank()) {
            List<String> areaIds = areaMapper.selectList(
                            new QueryWrapper<CssdArea>().eq("org_id", orgId).select("id"))
                    .stream().map(CssdArea::getId).toList();
            String finalOrgId = orgId;
            // 机构隔离：本院区 / 未分配 / 历史 campus_id=机构id
            w.and(x -> {
                x.eq("campus_id", finalOrgId);
                if (!areaIds.isEmpty()) {
                    x.or().in("campus_id", areaIds);
                }
                // 平台查询带 orgId 时仍兼容未分配；机构账号登录时不把「未分配」给所有机构看
                if (scopedOrgId() == null) {
                    x.or().isNull("campus_id").or().eq("campus_id", "");
                }
            });
        }
        if (q.getDeptId() != null && !q.getDeptId().isBlank()) {
            String did = q.getDeptId();
            w.and(x -> x.eq("id", did).or().eq("parent_id", did));
        }
        return ApiResult.ok(deptMapper.selectPage(new Page<>(q.getPage(), q.getSize()), w));
    }

    @PostMapping("/depts")
    public ApiResult<CssdDept> saveDept(@RequestBody CssdDept e) {
        String scope = scopedOrgId();
        if (scope != null) {
            // 机构账号新建科室默认挂到本机构（campus_id 存机构 id，与院区列表隔离一致）
            if (e.getCampusId() == null || e.getCampusId().isBlank()) {
                e.setCampusId(scope);
            } else {
                requireOrgAccess(resolveDeptOrgId(e.getCampusId()));
            }
        }
        e.setNamePinyin(PinyinCodeUtil.initials(e.getDeptName()));
        return ApiResult.ok(upsert(deptMapper, e, "dept"));
    }

    /** campus_id 可能是院区 id 或机构 id */
    private String resolveDeptOrgId(String campusOrOrgId) {
        if (campusOrOrgId == null || campusOrOrgId.isBlank()) return null;
        CssdArea area = areaMapper.selectById(campusOrOrgId);
        if (area != null) return area.getOrgId();
        CssdCampus campus = campusMapper.selectById(campusOrOrgId);
        if (campus != null && (campus.getOrgId() == null || campus.getOrgId().isBlank())) {
            return campus.getId();
        }
        return campusOrOrgId;
    }

    @DeleteMapping("/depts/{id}")
    public ApiResult<Void> delDept(@PathVariable String id) {
        deptMapper.deleteById(id);
        return ApiResult.ok();
    }

    @GetMapping("/staffs")
    public ApiResult<?> staffs(PageQuery q) {
        QueryWrapper<CssdStaff> w = new QueryWrapper<CssdStaff>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            String orgTag = "ORG:" + scope;
            String orgCode = orgScope.currentOrgCode();
            w.and(x -> {
                x.eq("org_id", scope).or().eq("remark", orgTag);
                if (orgCode != null && !orgCode.isBlank()) {
                    x.or().eq("org_code", orgCode);
                    // 本机构初始化管理员 cssd_{code}
                    x.or().eq("staff_no", "cssd_" + orgCode);
                }
            });
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("staff_no", kw).or().like("staff_name", kw).or().like("name_pinyin", kw));
        }
        if (q.getStaffRole() != null && !q.getStaffRole().isBlank()) {
            w.eq("staff_role", q.getStaffRole());
        }
        Page<CssdStaff> page = staffMapper.selectPage(new Page<>(q.getPage(), q.getSize()), w);
        for (CssdStaff s : page.getRecords()) {
            String plain = orgAdminInitService.getPasswordPlain(s.getId());
            if (plain == null || plain.isBlank()) {
                plain = s.getStaffNo();
            }
            s.setPassword(plain);
        }
        return ApiResult.ok(page);
    }

    @PostMapping("/staffs")
    public ApiResult<CssdStaff> saveStaff(@RequestBody CssdStaff e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
            String code = orgScope.currentOrgCode();
            if (code != null) e.setOrgCode(code);
        } else if (e.getOrgId() != null && !e.getOrgId().isBlank()) {
            CssdCampus org = campusMapper.selectById(e.getOrgId());
            if (org != null) e.setOrgCode(org.getCampusCode());
        }
        requireOrgAccess(e.getOrgId());
        applyStaffUserTypeRules(e);
        e.setNamePinyin(PinyinCodeUtil.initials(e.getStaffName()));
        String password = e.getPassword();
        e.setPassword(null);
        CssdStaff saved = upsert(staffMapper, e, "staff");
        String plain = orgAdminInitService.syncStaffPassword(saved, password);
        saved.setPassword(plain);
        return ApiResult.ok(saved);
    }

    /** 仅平台 admin 或用户类型=管理员可改他人用户类型；新增默认普通用户；cssd_* 默认管理员 */
    private void applyStaffUserTypeRules(CssdStaff e) {
        boolean create = e.getId() == null || e.getId().isBlank();
        CssdStaff existing = create ? null : staffMapper.selectById(e.getId());
        String requested = normalizeUserType(e.getUserType());
        if (e.getStaffNo() != null && e.getStaffNo().matches("(?i)^cssd_\\d+$")) {
            requested = "admin";
        } else if (OrgScope.isPlatformStaffNo(e.getStaffNo())) {
            requested = "admin";
        } else if (requested == null) {
            requested = create ? "normal" : normalizeUserType(existing == null ? null : existing.getUserType());
            if (requested == null) requested = "normal";
        }
        if (!canChangeStaffUserType()) {
            if (create) {
                e.setUserType(e.getStaffNo() != null && e.getStaffNo().matches("(?i)^cssd_\\d+$") ? "admin" : "normal");
            } else if (existing != null) {
                String keep = normalizeUserType(existing.getUserType());
                e.setUserType(keep == null ? "normal" : keep);
            } else {
                e.setUserType("normal");
            }
            return;
        }
        e.setUserType(requested);
    }

    private static String normalizeUserType(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return "admin".equalsIgnoreCase(raw.trim()) ? "admin" : "normal";
    }

    private boolean canChangeStaffUserType() {
        LoginUser u = SecurityUtils.current();
        if (u == null) return false;
        if (OrgScope.isPlatformStaffNo(u.getStaffNo())) return true;
        if ("admin".equalsIgnoreCase(u.getUserType())) return true;
        // 兼容 JWT 尚未带 userType 的机构初始化账号
        return u.getStaffNo() != null && u.getStaffNo().matches("(?i)^cssd_\\d+$");
    }

    @DeleteMapping("/staffs/{id}")
    public ApiResult<Void> delStaff(@PathVariable String id) {
        CssdStaff staff = staffMapper.selectById(id);
        if (staff != null) requireOrgAccess(staff.getOrgId());
        staffMapper.deleteById(id);
        return ApiResult.ok();
    }

    @GetMapping("/vendors")
    public ApiResult<?> vendors(PageQuery q) {
        return page(vendorMapper, q, "vendor_code", "vendor_name", "name_pinyin");
    }

    @PostMapping("/vendors")
    public ApiResult<CssdVendor> saveVendor(@RequestBody CssdVendor e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getVendorName()));
        return ApiResult.ok(upsert(vendorMapper, e, "vendor"));
    }

    @GetMapping("/manufacturers")
    public ApiResult<?> manufacturers(PageQuery q) {
        return page(manufacturerMapper, q, "mfr_code", "mfr_name", "name_pinyin");
    }

    @PostMapping("/manufacturers")
    public ApiResult<CssdManufacturer> saveMfr(@RequestBody CssdManufacturer e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getMfrName()));
        return ApiResult.ok(upsert(manufacturerMapper, e, "manufacturer"));
    }

    @GetMapping("/materials")
    public ApiResult<?> materials(PageQuery q) {
        return page(materialMapper, q, "material_code", "material_name", "name_pinyin");
    }

    @PostMapping("/materials")
    public ApiResult<CssdPackMaterial> saveMaterial(@RequestBody CssdPackMaterial e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getMaterialName()));
        return ApiResult.ok(upsert(materialMapper, e, "pack_material"));
    }

    @GetMapping("/instruments")
    public ApiResult<?> instruments(PageQuery q) {
        return page(instrumentMapper, q, "instrument_code", "instrument_name", "name_pinyin");
    }

    @PostMapping("/instruments")
    public ApiResult<CssdInstrument> saveInstrument(@RequestBody CssdInstrument e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getInstrumentName()));
        return ApiResult.ok(upsert(instrumentMapper, e, "instrument"));
    }

    @GetMapping("/templates")
    public ApiResult<?> templates(PageQuery q) {
        return page(templateMapper, q, "template_code", "template_name", "name_pinyin");
    }

    @GetMapping("/templates/{id}")
    public ApiResult<Map<String, Object>> template(@PathVariable String id) {
        CssdPackTemplate t = templateMapper.selectById(id);
        return ApiResult.ok(Map.of("template", t, "items", templateItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CssdPackTemplateItem>()
                        .eq(CssdPackTemplateItem::getTemplateId, id).orderByAsc(CssdPackTemplateItem::getLineNo))));
    }

    @PostMapping("/templates")
    public ApiResult<CssdPackTemplate> saveTemplate(@RequestBody CssdPackTemplate e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getTemplateName()));
        return ApiResult.ok(upsert(templateMapper, e, "pack_template"));
    }

    @PostMapping("/templates/{id}/items")
    public ApiResult<CssdPackTemplateItem> saveTplItem(@PathVariable String id, @RequestBody CssdPackTemplateItem e) {
        CssdPackTemplate t = templateMapper.selectById(id);
        e.setTemplateId(id);
        e.setTemplateCode(t.getTemplateCode());
        e.setTemplateName(t.getTemplateName());
        if (e.getId() == null) {
            e.setId(IdUtils.uuid7());
            templateItemMapper.insert(e);
        } else {
            templateItemMapper.updateById(e);
        }
        return ApiResult.ok(e);
    }

    @GetMapping("/devices")
    public ApiResult<?> devices(PageQuery q) {
        return page(deviceMapper, q, "device_code", "device_name", "name_pinyin", "device_no");
    }

    @PostMapping("/devices")
    public ApiResult<CssdDevice> saveDevice(@RequestBody CssdDevice e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getDeviceName()));
        return ApiResult.ok(upsert(deviceMapper, e, "device"));
    }

    @GetMapping("/programs")
    public ApiResult<?> programs(PageQuery q) {
        QueryWrapper<CssdDeviceProgram> w = new QueryWrapper<CssdDeviceProgram>().orderByAsc("program_code");
        if (q.getDeviceId() != null) {
            w.eq("device_id", q.getDeviceId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            w.and(x -> x.like("program_code", q.getKeyword()).or().like("program_name", q.getKeyword()));
        }
        return ApiResult.ok(programMapper.selectPage(new Page<>(q.getPage(), q.getSize()), w));
    }

    @PostMapping("/programs")
    public ApiResult<CssdDeviceProgram> saveProgram(@RequestBody CssdDeviceProgram e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getProgramName()));
        return ApiResult.ok(upsert(programMapper, e, "device_program"));
    }

    @GetMapping("/baskets")
    public ApiResult<?> baskets(PageQuery q) {
        return page(basketMapper, q, "basket_code", "basket_name", "name_pinyin");
    }

    @PostMapping("/baskets")
    public ApiResult<CssdBasket> saveBasket(@RequestBody CssdBasket e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getBasketName()));
        if (e.getBasketStatus() == null) {
            e.setBasketStatus("idle");
        }
        return ApiResult.ok(upsert(basketMapper, e, "basket"));
    }

    @GetMapping("/carts")
    public ApiResult<?> carts(PageQuery q) {
        return page(cartMapper, q, "cart_code", "cart_name", "name_pinyin");
    }

    @PostMapping("/carts")
    public ApiResult<CssdCart> saveCart(@RequestBody CssdCart e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getCartName()));
        return ApiResult.ok(upsert(cartMapper, e, "cart"));
    }

    @GetMapping("/once-items")
    public ApiResult<?> onceItems(PageQuery q) {
        return page(onceItemMapper, q, "item_code", "item_name", "name_pinyin");
    }

    @PostMapping("/once-items")
    public ApiResult<CssdOnceItem> saveOnceItem(@RequestBody CssdOnceItem e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getItemName()));
        return ApiResult.ok(upsert(onceItemMapper, e, "once_item"));
    }

    @GetMapping("/once-lots")
    public ApiResult<?> onceLots(PageQuery q) {
        return page(onceLotMapper, q, "lot_no", "item_code", "item_name");
    }

    @PostMapping("/once-lots")
    public ApiResult<CssdOnceLot> saveLot(@RequestBody CssdOnceLot e) {
        return ApiResult.ok(upsert(onceLotMapper, e, "once_lot"));
    }

    private <T extends BaseEntity> ApiResult<?> page(BaseMapper<T> mapper, PageQuery q, String... cols) {
        QueryWrapper<T> w = new QueryWrapper<T>().orderByDesc("created_at");
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            w.and(x -> {
                for (int i = 0; i < cols.length; i++) {
                    if (i == 0) {
                        x.like(cols[i], q.getKeyword());
                    } else {
                        x.or().like(cols[i], q.getKeyword());
                    }
                }
            });
        }
        return ApiResult.ok(mapper.selectPage(new Page<>(q.getPage(), q.getSize()), w));
    }

    private <T extends BaseEntity> T upsert(BaseMapper<T> mapper, T e, String type) {
        boolean create = e.getId() == null || e.getId().isBlank();
        if (create) {
            e.setId(IdUtils.uuid7());
            mapper.insert(e);
            changeLog.record(type, e.getId(), "create", null, e);
        } else {
            mapper.updateById(e);
            changeLog.record(type, e.getId(), "update", Map.of("id", e.getId()), e);
        }
        return mapper.selectById(e.getId());
    }
}
