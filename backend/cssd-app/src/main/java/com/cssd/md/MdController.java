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
import com.cssd.infra.entity.CssdUnit;
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
import com.cssd.infra.mapper.CssdUnitMapper;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final CssdUnitMapper unitMapper;
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
                        CssdUnitMapper unitMapper,
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
        this.unitMapper = unitMapper;
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

    /** 保存组织机构菜单授权（并同步到机构管理员账号，否则登录侧栏仍读旧 menu_auth） */
    @PutMapping("/campuses/{id}/menu-auth")
    public ApiResult<?> saveCampusMenuAuth(@PathVariable String id, @RequestBody Map<String, Object> body) {
        requireOrgAccess(id);
        CssdCampus campus = campusMapper.selectById(id);
        if (campus == null || (campus.getOrgId() != null && !campus.getOrgId().isBlank())) {
            throw new com.cssd.common.BizException(404, "组织机构不存在");
        }
        @SuppressWarnings("unchecked")
        List<String> menuKeys = body == null ? null : (List<String>) body.get("menuKeys");
        List<String> keys = OrgAdminInitService.ensureStation(menuKeys);
        campus.setMenuAuth(orgAdminInitService.toMenuAuthJson(keys));
        campusMapper.updateById(campus);
        orgAdminInitService.syncOrgAdminMenuAuth(id, keys);
        return ApiResult.ok(Map.of("id", id, "menuAuth", keys));
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

    /** 供应商：按组织机构隔离 */
    @GetMapping("/vendors")
    public ApiResult<?> vendors(PageQuery q) {
        QueryWrapper<CssdVendor> w = new QueryWrapper<CssdVendor>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("vendor_code", kw).or().like("vendor_name", kw)
                    .or().like("short_code", kw).or().like("name_pinyin", kw)
                    .or().like("social_credit_code", kw).or().like("legal_person", kw)
                    .or().like("salesman", kw).or().like("contact_phone", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(vendorMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/vendors")
    public ApiResult<CssdVendor> saveVendor(@RequestBody CssdVendor e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        String initials = PinyinCodeUtil.initials(e.getVendorName());
        e.setNamePinyin(initials);
        // 简码：按名称首字母大写；前端可预填，保存时以名称为准兜底
        if (e.getShortCode() == null || e.getShortCode().isBlank()) {
            e.setShortCode(initials == null || initials.isBlank() ? null : initials.toUpperCase());
        } else {
            e.setShortCode(e.getShortCode().trim().toUpperCase());
        }
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getVendorCode() == null ? "" : e.getVendorCode().trim();
        if (!creating) {
            // 已存在记录不允许改编码
            CssdVendor old = vendorMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "供应商不存在");
            code = old.getVendorCode();
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "G", vendorMapper, "vendor_code");
        } else {
            QueryWrapper<CssdVendor> dup = new QueryWrapper<CssdVendor>()
                    .eq("org_id", e.getOrgId())
                    .eq("vendor_code", code);
            if (vendorMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下供应商编码已存在：" + code);
            }
        }
        e.setVendorCode(code);
        return ApiResult.ok(upsert(vendorMapper, e, "vendor"));
    }

    @DeleteMapping("/vendors/{id}")
    public ApiResult<Void> delVendor(@PathVariable String id) {
        CssdVendor row = vendorMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        vendorMapper.deleteById(id);
        return ApiResult.ok();
    }

    /** 生产厂家：按组织机构隔离 */
    @GetMapping("/manufacturers")
    public ApiResult<?> manufacturers(PageQuery q) {
        QueryWrapper<CssdManufacturer> w = new QueryWrapper<CssdManufacturer>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("mfr_code", kw).or().like("mfr_name", kw)
                    .or().like("short_code", kw).or().like("name_pinyin", kw)
                    .or().like("social_credit_code", kw).or().like("legal_person", kw)
                    .or().like("salesman", kw).or().like("contact_phone", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(manufacturerMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/manufacturers")
    public ApiResult<CssdManufacturer> saveMfr(@RequestBody CssdManufacturer e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        String initials = PinyinCodeUtil.initials(e.getMfrName());
        e.setNamePinyin(initials);
        if (e.getShortCode() == null || e.getShortCode().isBlank()) {
            e.setShortCode(initials == null || initials.isBlank() ? null : initials.toUpperCase());
        } else {
            e.setShortCode(e.getShortCode().trim().toUpperCase());
        }
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getMfrCode() == null ? "" : e.getMfrCode().trim();
        if (!creating) {
            CssdManufacturer old = manufacturerMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "生产厂家不存在");
            code = old.getMfrCode();
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "S", manufacturerMapper, "mfr_code");
        } else {
            QueryWrapper<CssdManufacturer> dup = new QueryWrapper<CssdManufacturer>()
                    .eq("org_id", e.getOrgId())
                    .eq("mfr_code", code);
            if (manufacturerMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下生产厂家编码已存在：" + code);
            }
        }
        e.setMfrCode(code);
        return ApiResult.ok(upsert(manufacturerMapper, e, "manufacturer"));
    }

    @DeleteMapping("/manufacturers/{id}")
    public ApiResult<Void> delMfr(@PathVariable String id) {
        CssdManufacturer row = manufacturerMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        manufacturerMapper.deleteById(id);
        return ApiResult.ok();
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

    /** 器械基础档案：按组织机构隔离 */
    @GetMapping("/instruments")
    public ApiResult<?> instruments(PageQuery q) {
        QueryWrapper<CssdInstrument> w = new QueryWrapper<CssdInstrument>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("instrument_code", kw).or().like("instrument_name", kw)
                    .or().like("short_code", kw).or().like("name_pinyin", kw)
                    .or().like("alias_name", kw).or().like("english_name", kw)
                    .or().like("brand_name", kw).or().like("spec", kw).or().like("model", kw)
                    .or().like("register_no", kw).or().like("barcode", kw).or().like("udi", kw)
                    .or().like("manufacturer_name", kw).or().like("vendor_name", kw)
                    .or().like("instrument_category", kw).or().like("instrument_type", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(instrumentMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/instruments")
    public ApiResult<CssdInstrument> saveInstrument(@RequestBody CssdInstrument e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        String initials = PinyinCodeUtil.initials(e.getInstrumentName());
        e.setNamePinyin(initials);
        if (e.getShortCode() == null || e.getShortCode().isBlank()) {
            e.setShortCode(initials == null || initials.isBlank() ? null : initials.toUpperCase());
        } else {
            e.setShortCode(e.getShortCode().trim().toUpperCase());
        }
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getInstrumentCode() == null ? "" : e.getInstrumentCode().trim();
        if (!creating) {
            CssdInstrument old = instrumentMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "器械不存在");
            code = old.getInstrumentCode();
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "Q", instrumentMapper, "instrument_code");
        } else {
            QueryWrapper<CssdInstrument> dup = new QueryWrapper<CssdInstrument>()
                    .eq("org_id", e.getOrgId())
                    .eq("instrument_code", code);
            if (instrumentMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下器械编码已存在：" + code);
            }
        }
        e.setInstrumentCode(code);
        if (e.getBarcode() != null && !e.getBarcode().isBlank()) {
            QueryWrapper<CssdInstrument> barDup = new QueryWrapper<CssdInstrument>()
                    .eq("org_id", e.getOrgId())
                    .eq("barcode", e.getBarcode().trim());
            if (!creating) {
                barDup.ne("id", e.getId());
            }
            if (instrumentMapper.selectCount(barDup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下器械条码已存在：" + e.getBarcode().trim());
            }
            e.setBarcode(e.getBarcode().trim());
        }
        if (e.getUdi() != null && !e.getUdi().isBlank()) {
            String udi = e.getUdi().trim();
            QueryWrapper<CssdInstrument> udiDup = new QueryWrapper<CssdInstrument>()
                    .eq("org_id", e.getOrgId())
                    .eq("udi", udi);
            if (!creating) {
                udiDup.ne("id", e.getId());
            }
            if (instrumentMapper.selectCount(udiDup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下 UDI 已存在：" + udi);
            }
            e.setUdi(udi);
        } else {
            e.setUdi(null);
        }
        resolveInstrumentRefs(e);
        if (e.getIsHighValue() == null) e.setIsHighValue(0);
        if (e.getIsKeyMonitor() == null) e.setIsKeyMonitor(0);
        if (e.getCurrentStatus() == null || e.getCurrentStatus().isBlank()) {
            e.setCurrentStatus("在用");
        }
        if (e.getIsActive() == null) e.setIsActive(1);
        return ApiResult.ok(upsert(instrumentMapper, e, "instrument"));
    }

    /** 按本机构字典回填厂家/供应商名称快照；无 id 时按名称在本机构内解析 */
    private void resolveInstrumentRefs(CssdInstrument e) {
        if (e.getManufacturerId() != null && !e.getManufacturerId().isBlank()) {
            CssdManufacturer mfr = manufacturerMapper.selectById(e.getManufacturerId());
            if (mfr == null) throw new com.cssd.common.BizException(400, "生产厂家不存在");
            if (mfr.getOrgId() != null && !mfr.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "生产厂家不属于当前组织机构");
            }
            e.setManufacturerName(mfr.getMfrName());
        } else if (e.getManufacturerName() != null && !e.getManufacturerName().isBlank()) {
            String name = e.getManufacturerName().trim();
            QueryWrapper<CssdManufacturer> w = new QueryWrapper<CssdManufacturer>()
                    .eq("org_id", e.getOrgId())
                    .eq("mfr_name", name)
                    .last("limit 1");
            CssdManufacturer mfr = manufacturerMapper.selectOne(w);
            if (mfr == null) {
                throw new com.cssd.common.BizException(400, "生产厂家不存在：" + name);
            }
            e.setManufacturerId(mfr.getId());
            e.setManufacturerName(mfr.getMfrName());
        } else {
            e.setManufacturerId(null);
            e.setManufacturerName(null);
        }
        if (e.getVendorId() != null && !e.getVendorId().isBlank()) {
            CssdVendor vendor = vendorMapper.selectById(e.getVendorId());
            if (vendor == null) throw new com.cssd.common.BizException(400, "供应商不存在");
            if (vendor.getOrgId() != null && !vendor.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "供应商不属于当前组织机构");
            }
            e.setVendorName(vendor.getVendorName());
        } else if (e.getVendorName() != null && !e.getVendorName().isBlank()) {
            String name = e.getVendorName().trim();
            QueryWrapper<CssdVendor> w = new QueryWrapper<CssdVendor>()
                    .eq("org_id", e.getOrgId())
                    .eq("vendor_name", name)
                    .last("limit 1");
            CssdVendor vendor = vendorMapper.selectOne(w);
            if (vendor == null) {
                throw new com.cssd.common.BizException(400, "供应商不存在：" + name);
            }
            e.setVendorId(vendor.getId());
            e.setVendorName(vendor.getVendorName());
        } else {
            e.setVendorId(null);
            e.setVendorName(null);
        }
        if (e.getUnitId() != null && !e.getUnitId().isBlank()) {
            CssdUnit unit = unitMapper.selectById(e.getUnitId());
            if (unit == null) throw new com.cssd.common.BizException(400, "计量单位不存在");
            if (unit.getOrgId() != null && !unit.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "计量单位不属于当前组织机构");
            }
            e.setUnitName(unit.getUnitName());
        } else if (e.getUnitName() != null && !e.getUnitName().isBlank()) {
            String name = e.getUnitName().trim();
            QueryWrapper<CssdUnit> w = new QueryWrapper<CssdUnit>()
                    .eq("org_id", e.getOrgId())
                    .eq("unit_name", name)
                    .last("limit 1");
            CssdUnit unit = unitMapper.selectOne(w);
            if (unit == null) {
                throw new com.cssd.common.BizException(400, "计量单位不存在：" + name);
            }
            e.setUnitId(unit.getId());
            e.setUnitName(unit.getUnitName());
        } else {
            e.setUnitId(null);
            e.setUnitName(null);
        }
    }

    @DeleteMapping("/instruments/{id}")
    public ApiResult<Void> delInstrument(@PathVariable String id) {
        CssdInstrument row = instrumentMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        instrumentMapper.deleteById(id);
        return ApiResult.ok();
    }

    /** 器械包档案：按组织机构隔离 */
    @GetMapping("/templates")
    public ApiResult<?> templates(PageQuery q) {
        QueryWrapper<CssdPackTemplate> w = new QueryWrapper<CssdPackTemplate>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("template_code", kw).or().like("template_name", kw)
                    .or().like("short_code", kw).or().like("name_pinyin", kw)
                    .or().like("pack_category", kw).or().like("owner_dept_name", kw)
                    .or().like("material_name", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(templateMapper.selectPage(new Page<>(page, size), w));
    }

    @GetMapping("/templates/{id}")
    public ApiResult<Map<String, Object>> template(@PathVariable String id) {
        CssdPackTemplate t = templateMapper.selectById(id);
        if (t == null) throw new com.cssd.common.BizException(404, "器械包不存在");
        requireOrgAccess(t.getOrgId());
        return ApiResult.ok(Map.of("template", t, "items", templateItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CssdPackTemplateItem>()
                        .eq(CssdPackTemplateItem::getTemplateId, id).orderByAsc(CssdPackTemplateItem::getLineNo))));
    }

    @PostMapping("/templates")
    public ApiResult<CssdPackTemplate> saveTemplate(@RequestBody CssdPackTemplate e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        if (e.getPackCategory() == null || e.getPackCategory().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择包类别");
        }
        String initials = PinyinCodeUtil.initials(e.getTemplateName());
        e.setNamePinyin(initials);
        if (e.getShortCode() == null || e.getShortCode().isBlank()) {
            e.setShortCode(initials == null || initials.isBlank() ? null : initials.toUpperCase());
        } else {
            e.setShortCode(e.getShortCode().trim().toUpperCase());
        }
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getTemplateCode() == null ? "" : e.getTemplateCode().trim();
        if (!creating) {
            CssdPackTemplate old = templateMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "器械包不存在");
            code = old.getTemplateCode();
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "B", templateMapper, "template_code");
        } else {
            QueryWrapper<CssdPackTemplate> dup = new QueryWrapper<CssdPackTemplate>()
                    .eq("org_id", e.getOrgId())
                    .eq("template_code", code);
            if (templateMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下包编码已存在：" + code);
            }
        }
        e.setTemplateCode(code);
        if (e.getNeedWash() == null) e.setNeedWash(1);
        if (e.getWashMode() == null || e.getWashMode().isBlank()) e.setWashMode("scatter");
        if (e.getNeedPack() == null) e.setNeedPack(1);
        if (e.getNeedStore() == null) e.setNeedStore(1);
        if (e.getNeedRecycle() == null) e.setNeedRecycle(1);
        if (e.getIsImplant() == null) e.setIsImplant(0);
        if (e.getIsActive() == null) e.setIsActive(1);
        return ApiResult.ok(upsert(templateMapper, e, "pack_template"));
    }

    @DeleteMapping("/templates/{id}")
    public ApiResult<Void> delTemplate(@PathVariable String id) {
        CssdPackTemplate row = templateMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        templateMapper.deleteById(id);
        return ApiResult.ok();
    }

    @PostMapping("/templates/{id}/items")
    public ApiResult<CssdPackTemplateItem> saveTplItem(@PathVariable String id, @RequestBody CssdPackTemplateItem e) {
        CssdPackTemplate t = templateMapper.selectById(id);
        if (t == null) throw new com.cssd.common.BizException(404, "器械包不存在");
        requireOrgAccess(t.getOrgId());
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

    /** 设备：按组织机构隔离 */
    @GetMapping("/devices")
    public ApiResult<?> devices(PageQuery q) {
        QueryWrapper<CssdDevice> w = new QueryWrapper<CssdDevice>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("device_code", kw).or().like("device_name", kw)
                    .or().like("name_pinyin", kw).or().like("device_no", kw)
                    .or().like("serial_no", kw).or().like("device_type", kw)
                    .or().like("model", kw).or().like("install_location", kw)
                    .or().like("area_name", kw).or().like("operator_name", kw)
                    .or().like("manufacturer_name", kw).or().like("vendor_name", kw)
                    .or().like("brand_name", kw).or().like("country_name", kw)
                    .or().like("device_reg_no", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(deviceMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/devices")
    public ApiResult<CssdDevice> saveDevice(@RequestBody CssdDevice e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        e.setNamePinyin(PinyinCodeUtil.initials(e.getDeviceName()));
        resolveDeviceRefs(e);
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getDeviceCode() == null ? "" : e.getDeviceCode().trim();
        if (!creating) {
            CssdDevice old = deviceMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "设备不存在");
            code = old.getDeviceCode();
            // 保留业务字段，避免台账编辑冲掉
            if (e.getDeviceKind() == null || e.getDeviceKind().isBlank()) e.setDeviceKind(old.getDeviceKind());
            if (e.getDeviceNo() == null || e.getDeviceNo().isBlank()) e.setDeviceNo(old.getDeviceNo());
            if (e.getSterTempClass() == null || e.getSterTempClass().isBlank()) e.setSterTempClass(old.getSterTempClass());
            // 未重新上传附件时保留原证书内容
            if ((e.getCertAttachmentData() == null || e.getCertAttachmentData().isBlank())
                    && e.getCertAttachment() != null && !e.getCertAttachment().isBlank()
                    && e.getCertAttachment().equals(old.getCertAttachment())) {
                e.setCertAttachmentData(old.getCertAttachmentData());
            }
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "D", deviceMapper, "device_code");
        } else {
            QueryWrapper<CssdDevice> dup = new QueryWrapper<CssdDevice>()
                    .eq("org_id", e.getOrgId())
                    .eq("device_code", code);
            if (deviceMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下设备编码已存在：" + code);
            }
        }
        e.setDeviceCode(code);
        if (e.getDeviceNo() == null || e.getDeviceNo().isBlank()) {
            e.setDeviceNo(code);
        }
        if (e.getDeviceStatus() == null || e.getDeviceStatus().isBlank()) {
            e.setDeviceStatus("空闲");
        }
        if (e.getDeviceKind() == null || e.getDeviceKind().isBlank()) {
            e.setDeviceKind(guessDeviceKind(e.getDeviceType()));
        }
        return ApiResult.ok(upsert(deviceMapper, e, "device"));
    }

    /** 按本机构字典回填厂家/供应商/责任人，禁止跨机构引用 */
    private void resolveDeviceRefs(CssdDevice e) {
        if (e.getManufacturerId() != null && !e.getManufacturerId().isBlank()) {
            CssdManufacturer mfr = manufacturerMapper.selectById(e.getManufacturerId());
            if (mfr == null) throw new com.cssd.common.BizException(400, "生产厂家不存在");
            if (mfr.getOrgId() != null && !mfr.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "生产厂家不属于当前组织机构");
            }
            e.setManufacturerName(mfr.getMfrName());
        } else {
            e.setManufacturerId(null);
            e.setManufacturerName(null);
        }
        if (e.getVendorId() != null && !e.getVendorId().isBlank()) {
            CssdVendor vendor = vendorMapper.selectById(e.getVendorId());
            if (vendor == null) throw new com.cssd.common.BizException(400, "供应商不存在");
            if (vendor.getOrgId() != null && !vendor.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "供应商不属于当前组织机构");
            }
            e.setVendorName(vendor.getVendorName());
        } else {
            e.setVendorId(null);
            e.setVendorName(null);
        }
        if (e.getOperatorId() != null && !e.getOperatorId().isBlank()) {
            CssdStaff staff = staffMapper.selectById(e.getOperatorId());
            if (staff == null) throw new com.cssd.common.BizException(400, "操作责任人不存在");
            if (staff.getOrgId() != null && !staff.getOrgId().equals(e.getOrgId())) {
                throw new com.cssd.common.BizException(400, "操作责任人不属于当前组织机构");
            }
            e.setOperatorName(staff.getStaffName());
        } else {
            e.setOperatorId(null);
            e.setOperatorName(null);
        }
    }

    @DeleteMapping("/devices/{id}")
    public ApiResult<Void> delDevice(@PathVariable String id) {
        CssdDevice row = deviceMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        deviceMapper.deleteById(id);
        return ApiResult.ok();
    }

    /** 单位：按组织机构隔离 */
    @GetMapping("/units")
    public ApiResult<?> units(PageQuery q) {
        QueryWrapper<CssdUnit> w = new QueryWrapper<CssdUnit>().orderByDesc("created_at");
        String scope = scopedOrgId();
        if (scope != null) {
            w.eq("org_id", scope);
        } else if (q.getOrgId() != null && !q.getOrgId().isBlank()) {
            w.eq("org_id", q.getOrgId());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            String kw = q.getKeyword();
            w.and(x -> x.like("unit_code", kw).or().like("unit_name", kw).or().like("name_pinyin", kw));
        }
        long page = q.getPage() < 1 ? 1 : q.getPage();
        long size = q.getSize() < 1 ? 20 : q.getSize();
        return ApiResult.ok(unitMapper.selectPage(new Page<>(page, size), w));
    }

    @PostMapping("/units")
    public ApiResult<CssdUnit> saveUnit(@RequestBody CssdUnit e) {
        String scope = scopedOrgId();
        if (scope != null) {
            e.setOrgId(scope);
        }
        if (e.getOrgId() == null || e.getOrgId().isBlank()) {
            throw new com.cssd.common.BizException(400, "请选择所属组织机构");
        }
        requireOrgAccess(e.getOrgId());
        CssdCampus org = campusMapper.selectById(e.getOrgId());
        if (org != null) {
            e.setOrgCode(org.getCampusCode());
        }
        e.setNamePinyin(PinyinCodeUtil.initials(e.getUnitName()));
        boolean creating = e.getId() == null || e.getId().isBlank();
        String code = e.getUnitCode() == null ? "" : e.getUnitCode().trim();
        if (!creating) {
            CssdUnit old = unitMapper.selectById(e.getId());
            if (old == null) throw new com.cssd.common.BizException(404, "单位不存在");
            code = old.getUnitCode();
        } else if (code.isBlank()) {
            code = nextOrgSeqCode(e.getOrgId(), "D", unitMapper, "unit_code");
        } else {
            QueryWrapper<CssdUnit> dup = new QueryWrapper<CssdUnit>()
                    .eq("org_id", e.getOrgId())
                    .eq("unit_code", code);
            if (unitMapper.selectCount(dup) > 0) {
                throw new com.cssd.common.BizException(400, "该组织机构下单位编码已存在：" + code);
            }
        }
        e.setUnitCode(code);
        return ApiResult.ok(upsert(unitMapper, e, "unit"));
    }

    @DeleteMapping("/units/{id}")
    public ApiResult<Void> delUnit(@PathVariable String id) {
        CssdUnit row = unitMapper.selectById(id);
        if (row != null) requireOrgAccess(row.getOrgId());
        unitMapper.deleteById(id);
        return ApiResult.ok();
    }

    private static String guessDeviceKind(String deviceType) {
        if (deviceType == null || deviceType.isBlank()) return "washer";
        String t = deviceType.trim();
        if (t.contains("灭菌") || t.equalsIgnoreCase("sterilizer")) return "sterilizer";
        if (t.contains("封口") || t.equalsIgnoreCase("sealer")) return "sealer";
        if (t.contains("清洗") || t.equalsIgnoreCase("washer")) return "washer";
        return "washer";
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

    /** 名称转大写拼音首字母（简码） */
    @GetMapping("/name-initials")
    public ApiResult<?> nameInitials(@org.springframework.web.bind.annotation.RequestParam(required = false) String name) {
        String initials = PinyinCodeUtil.initials(name == null ? "" : name);
        if (initials == null) initials = "";
        return ApiResult.ok(Map.of("initials", initials.toUpperCase()));
    }

    /**
     * 机构内按前缀自增编码：G000001 / S000001。
     * 仅统计「前缀 + 纯数字」形式，忽略手工录入的其它编码。
     */
    private <T> String nextOrgSeqCode(String orgId, String prefix, BaseMapper<T> mapper, String codeCol) {
        QueryWrapper<T> w = new QueryWrapper<T>()
                .select(codeCol)
                .eq("org_id", orgId)
                .likeRight(codeCol, prefix);
        List<Map<String, Object>> rows = mapper.selectMaps(w);
        Pattern p = Pattern.compile("^" + Pattern.quote(prefix) + "(\\d+)$", Pattern.CASE_INSENSITIVE);
        int max = 0;
        for (Map<String, Object> row : rows) {
            if (row == null || row.isEmpty()) continue;
            Object v = row.values().iterator().next();
            if (v == null) continue;
            Matcher m = p.matcher(String.valueOf(v).trim());
            if (m.matches()) {
                try {
                    max = Math.max(max, Integer.parseInt(m.group(1)));
                } catch (NumberFormatException ignored) {
                    // skip
                }
            }
        }
        return prefix + String.format("%06d", max + 1);
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
