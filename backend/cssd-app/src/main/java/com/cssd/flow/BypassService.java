package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdDept;
import com.cssd.infra.entity.CssdOnceIo;
import com.cssd.infra.entity.CssdOnceItem;
import com.cssd.infra.entity.CssdOnceLot;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdPackTemplate;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.entity.CssdVendor;
import com.cssd.infra.mapper.CssdOnceIoMapper;
import com.cssd.infra.mapper.CssdOnceItemMapper;
import com.cssd.infra.mapper.CssdOnceLotMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdPackTemplateMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.cssd.infra.mapper.CssdVendorMapper;
import com.cssd.security.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class BypassService {
    private final FlowSupport support;
    private final CssdPackTemplateMapper templateMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdStaffMapper staffMapper;
    private final CssdVendorMapper vendorMapper;
    private final CssdOnceLotMapper lotMapper;
    private final CssdOnceItemMapper itemMapper;
    private final CssdOnceIoMapper ioMapper;

    public BypassService(FlowSupport support, CssdPackTemplateMapper templateMapper,
                         CssdPackInstanceMapper instanceMapper, CssdStaffMapper staffMapper,
                         CssdVendorMapper vendorMapper, CssdOnceLotMapper lotMapper,
                         CssdOnceItemMapper itemMapper, CssdOnceIoMapper ioMapper) {
        this.support = support;
        this.templateMapper = templateMapper;
        this.instanceMapper = instanceMapper;
        this.staffMapper = staffMapper;
        this.vendorMapper = vendorMapper;
        this.lotMapper = lotMapper;
        this.itemMapper = itemMapper;
        this.ioMapper = ioMapper;
    }

    @Transactional
    public CssdPackInstance loan(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdPackTemplate tpl = templateMapper.selectById(String.valueOf(body.get("templateId")));
        if (tpl == null || !"loan".equals(tpl.getPackCategory())) {
            throw new BizException("须选择寄消包模板");
        }
        CssdStaff loaner = staffMapper.selectById(String.valueOf(body.get("loanStaffId")));
        if (loaner == null) {
            throw new BizException("寄消人必填");
        }
        CssdPackInstance inst = new CssdPackInstance();
        inst.setId(support.id());
        inst.setBarcode("L" + support.billNo("PK").substring(2));
        inst.setLabelCarrier("qr");
        inst.setTemplateId(tpl.getId());
        inst.setTemplateCode(tpl.getTemplateCode());
        inst.setTemplateName(tpl.getTemplateName());
        inst.setPackCategory("loan");
        inst.setStatus("checked");
        inst.setNeedWash(0);
        inst.setNeedPack(0);
        inst.setNeedStore(0);
        inst.setNeedRecycle(0);
        inst.setSterMethod(tpl.getSterMethod());
        inst.setShelfLifeDays(tpl.getShelfLifeDays());
        inst.setMaterialId(tpl.getMaterialId());
        inst.setMaterialCode(tpl.getMaterialCode());
        inst.setMaterialName(tpl.getMaterialName());
        inst.setLoanStaffId(loaner.getId());
        inst.setLoanStaffNo(loaner.getStaffNo());
        inst.setLoanStaffName(loaner.getStaffName());
        inst.setCheckerId(user.getStaffId());
        inst.setCheckerNo(user.getStaffNo());
        inst.setCheckerName(user.getStaffName());
        inst.setCheckedAt(support.now());
        inst.setPackerId(user.getStaffId());
        inst.setPackerNo(user.getStaffNo());
        inst.setPackerName(user.getStaffName());
        instanceMapper.insert(inst);
        return inst;
    }

    @Transactional
    public CssdPackInstance externalRegister(Map<String, Object> body) {
        CssdPackTemplate tpl = templateMapper.selectById(String.valueOf(body.get("templateId")));
        if (tpl == null || !"external".equals(tpl.getPackCategory())) {
            throw new BizException("须选择外来器械模板");
        }
        CssdPackInstance inst = new CssdPackInstance();
        inst.setId(support.id());
        inst.setBarcode("E" + support.billNo("PK").substring(2));
        inst.setLabelCarrier("qr");
        inst.setTemplateId(tpl.getId());
        inst.setTemplateCode(tpl.getTemplateCode());
        inst.setTemplateName(tpl.getTemplateName());
        inst.setPackCategory("external");
        inst.setStatus("packed");
        inst.setNeedWash(1);
        inst.setNeedPack(1);
        inst.setNeedStore(0);
        inst.setNeedRecycle(0);
        inst.setSterMethod(tpl.getSterMethod());
        inst.setShelfLifeDays(tpl.getShelfLifeDays());
        inst.setPatientNo(str(body.get("patientNo")));
        inst.setPatientName(str(body.get("patientName")));
        inst.setSurgeryName(str(body.get("surgeryName")));
        if (body.get("vendorId") != null) {
            CssdVendor v = vendorMapper.selectById(String.valueOf(body.get("vendorId")));
            if (v != null) {
                inst.setExtVendorId(v.getId());
                inst.setExtVendorCode(v.getVendorCode());
                inst.setExtVendorName(v.getVendorName());
            }
        }
        instanceMapper.insert(inst);
        return inst;
    }

    @Transactional
    public CssdPackInstance vendorTake(String barcode) {
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null || !"external".equals(inst.getPackCategory())) {
            throw new BizException("非外来器械包");
        }
        inst.setStatus("vendor_taken");
        instanceMapper.updateById(inst);
        return inst;
    }

    @Transactional
    public CssdOnceIo onceIo(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdOnceLot lot = lotMapper.selectById(String.valueOf(body.get("lotId")));
        if (lot == null) {
            throw new BizException("批次不存在");
        }
        String kind = str(body.getOrDefault("ioKind", "in"));
        int qty = body.get("qty") instanceof Number n ? n.intValue() : 1;
        if ("out".equals(kind)) {
            if (Integer.valueOf(1).equals(lot.getIsBlocked())) {
                throw new BizException("批次已禁用");
            }
            if (lot.getExpireDate() != null && lot.getExpireDate().isBefore(LocalDate.now())) {
                throw new BizException("批次已过期");
            }
            if (lot.getQtyOnHand() < qty) {
                throw new BizException("库存不足");
            }
            lot.setQtyOnHand(lot.getQtyOnHand() - qty);
        } else {
            lot.setQtyOnHand(lot.getQtyOnHand() + qty);
        }
        lotMapper.updateById(lot);
        CssdOnceIo io = new CssdOnceIo();
        io.setId(support.id());
        io.setIoNo(support.billNo("YC"));
        io.setIoKind(kind);
        io.setIoStatus("submitted");
        io.setLotId(lot.getId());
        io.setItemId(lot.getItemId());
        io.setItemCode(lot.getItemCode());
        io.setItemName(lot.getItemName());
        io.setSpec(lot.getSpec());
        io.setUnitName(lot.getUnitName());
        io.setLotNo(lot.getLotNo());
        io.setSterLotNo(lot.getSterLotNo());
        io.setExpireDate(lot.getExpireDate());
        io.setMfrId(lot.getMfrId());
        io.setMfrCode(lot.getMfrCode());
        io.setMfrName(lot.getMfrName());
        io.setVendorId(lot.getVendorId());
        io.setVendorCode(lot.getVendorCode());
        io.setVendorName(lot.getVendorName());
        io.setQty(qty);
        io.setOperatorId(user.getStaffId());
        io.setOperatorNo(user.getStaffNo());
        io.setOperatorName(user.getStaffName());
        io.setIoAt(support.now());
        if (body.get("deptId") != null) {
            CssdDept dept = support.dept(String.valueOf(body.get("deptId")));
            io.setDeptId(dept.getId());
            io.setDeptCode(dept.getDeptCode());
            io.setDeptName(dept.getDeptName());
        }
        ioMapper.insert(io);
        return io;
    }

    @Transactional
    public CssdOnceLot blockLot(String lotId, String reason) {
        CssdOnceLot lot = lotMapper.selectById(lotId);
        lot.setIsBlocked(1);
        lot.setBlockReason(reason);
        lotMapper.updateById(lot);
        return lot;
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
