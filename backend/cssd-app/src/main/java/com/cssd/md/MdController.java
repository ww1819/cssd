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
import com.cssd.infra.mapper.CssdBasketMapper;
import com.cssd.infra.mapper.CssdCampusMapper;
import com.cssd.infra.mapper.CssdCartMapper;
import com.cssd.infra.mapper.CssdDeptMapper;
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

    public MdController(ChangeLogService changeLog, CssdCampusMapper campusMapper, CssdDeptMapper deptMapper,
                        CssdStaffMapper staffMapper, CssdVendorMapper vendorMapper,
                        CssdManufacturerMapper manufacturerMapper, CssdPackMaterialMapper materialMapper,
                        CssdInstrumentMapper instrumentMapper, CssdPackTemplateMapper templateMapper,
                        CssdPackTemplateItemMapper templateItemMapper, CssdDeviceMapper deviceMapper,
                        CssdDeviceProgramMapper programMapper, CssdBasketMapper basketMapper, CssdCartMapper cartMapper,
                        CssdOnceItemMapper onceItemMapper, CssdOnceLotMapper onceLotMapper) {
        this.changeLog = changeLog;
        this.campusMapper = campusMapper;
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
    }

    @GetMapping("/campuses")
    public ApiResult<?> campuses(PageQuery q) {
        return page(campusMapper, q, "campus_code", "campus_name", "name_pinyin");
    }

    @PostMapping("/campuses")
    public ApiResult<CssdCampus> saveCampus(@RequestBody CssdCampus e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getCampusName()));
        return ApiResult.ok(upsert(campusMapper, e, "campus"));
    }

    @DeleteMapping("/campuses/{id}")
    public ApiResult<Void> delCampus(@PathVariable String id) {
        campusMapper.deleteById(id);
        return ApiResult.ok();
    }

    @GetMapping("/depts")
    public ApiResult<?> depts(PageQuery q) {
        return page(deptMapper, q, "dept_code", "dept_name", "name_pinyin");
    }

    @PostMapping("/depts")
    public ApiResult<CssdDept> saveDept(@RequestBody CssdDept e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getDeptName()));
        return ApiResult.ok(upsert(deptMapper, e, "dept"));
    }

    @DeleteMapping("/depts/{id}")
    public ApiResult<Void> delDept(@PathVariable String id) {
        deptMapper.deleteById(id);
        return ApiResult.ok();
    }

    @GetMapping("/staffs")
    public ApiResult<?> staffs(PageQuery q) {
        return page(staffMapper, q, "staff_no", "staff_name", "name_pinyin");
    }

    @PostMapping("/staffs")
    public ApiResult<CssdStaff> saveStaff(@RequestBody CssdStaff e) {
        e.setNamePinyin(PinyinCodeUtil.initials(e.getStaffName()));
        return ApiResult.ok(upsert(staffMapper, e, "staff"));
    }

    @DeleteMapping("/staffs/{id}")
    public ApiResult<Void> delStaff(@PathVariable String id) {
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
