package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.common.IdUtils;
import com.cssd.his.HisAdapter;
import com.cssd.infra.entity.CssdApplyBill;
import com.cssd.infra.entity.CssdApplyItem;
import com.cssd.infra.entity.CssdCart;
import com.cssd.infra.entity.CssdDept;
import com.cssd.infra.entity.CssdDistBill;
import com.cssd.infra.entity.CssdDistItem;
import com.cssd.infra.entity.CssdHisSyncLog;
import com.cssd.infra.entity.CssdOnceLot;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdPackTemplate;
import com.cssd.infra.entity.CssdStaff;
import com.cssd.infra.entity.CssdUseCheck;
import com.cssd.infra.entity.CssdUseCheckItem;
import com.cssd.infra.mapper.CssdApplyBillMapper;
import com.cssd.infra.mapper.CssdApplyItemMapper;
import com.cssd.infra.mapper.CssdCartMapper;
import com.cssd.infra.mapper.CssdDistBillMapper;
import com.cssd.infra.mapper.CssdDistItemMapper;
import com.cssd.infra.mapper.CssdHisSyncLogMapper;
import com.cssd.infra.mapper.CssdOnceLotMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdPackTemplateMapper;
import com.cssd.infra.mapper.CssdStaffMapper;
import com.cssd.infra.mapper.CssdUseCheckItemMapper;
import com.cssd.infra.mapper.CssdUseCheckMapper;
import com.cssd.security.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DistUseService {
    private final FlowSupport support;
    private final CssdApplyBillMapper applyBillMapper;
    private final CssdApplyItemMapper applyItemMapper;
    private final CssdDistBillMapper distBillMapper;
    private final CssdDistItemMapper distItemMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdPackTemplateMapper templateMapper;
    private final CssdCartMapper cartMapper;
    private final CssdUseCheckMapper useCheckMapper;
    private final CssdUseCheckItemMapper useCheckItemMapper;
    private final CssdStaffMapper staffMapper;
    private final CssdHisSyncLogMapper hisLogMapper;
    private final CssdOnceLotMapper onceLotMapper;
    private final HisAdapter hisAdapter;

    public DistUseService(FlowSupport support, CssdApplyBillMapper applyBillMapper, CssdApplyItemMapper applyItemMapper,
                          CssdDistBillMapper distBillMapper, CssdDistItemMapper distItemMapper,
                          CssdPackInstanceMapper instanceMapper, CssdPackTemplateMapper templateMapper,
                          CssdCartMapper cartMapper, CssdUseCheckMapper useCheckMapper,
                          CssdUseCheckItemMapper useCheckItemMapper, CssdStaffMapper staffMapper,
                          CssdHisSyncLogMapper hisLogMapper, CssdOnceLotMapper onceLotMapper, HisAdapter hisAdapter) {
        this.support = support;
        this.applyBillMapper = applyBillMapper;
        this.applyItemMapper = applyItemMapper;
        this.distBillMapper = distBillMapper;
        this.distItemMapper = distItemMapper;
        this.instanceMapper = instanceMapper;
        this.templateMapper = templateMapper;
        this.cartMapper = cartMapper;
        this.useCheckMapper = useCheckMapper;
        this.useCheckItemMapper = useCheckItemMapper;
        this.staffMapper = staffMapper;
        this.hisLogMapper = hisLogMapper;
        this.onceLotMapper = onceLotMapper;
        this.hisAdapter = hisAdapter;
    }

    @Transactional
    public CssdApplyBill submitApply(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdDept dept = support.dept(String.valueOf(body.get("deptId")));
        CssdApplyBill bill = new CssdApplyBill();
        bill.setId(support.id());
        bill.setBillNo(support.billNo("SL"));
        bill.setBillStatus("submitted");
        bill.setDeptId(dept.getId());
        bill.setDeptCode(dept.getDeptCode());
        bill.setDeptName(dept.getDeptName());
        bill.setApplyType(str(body.getOrDefault("applyType", "dept")));
        bill.setSurgeryName(str(body.get("surgeryName")));
        bill.setPatientNo(str(body.get("patientNo")));
        bill.setPatientName(str(body.get("patientName")));
        bill.setApplicantId(user.getStaffId());
        bill.setApplicantNo(user.getStaffNo());
        bill.setApplicantName(user.getStaffName());
        bill.setSubmittedAt(support.now());
        applyBillMapper.insert(bill);
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        int line = 1;
        for (Map<String, Object> it : items) {
            CssdPackTemplate tpl = templateMapper.selectById(String.valueOf(it.get("templateId")));
            CssdApplyItem row = new CssdApplyItem();
            row.setId(support.id());
            row.setBillId(bill.getId());
            row.setBillNo(bill.getBillNo());
            row.setLineNo(line++);
            row.setTemplateId(tpl.getId());
            row.setTemplateCode(tpl.getTemplateCode());
            row.setTemplateName(tpl.getTemplateName());
            row.setQty(it.get("qty") instanceof Number n ? n.intValue() : 1);
            row.setDistQty(0);
            row.setRecycleQty(0);
            applyItemMapper.insert(row);
        }
        return bill;
    }

    @Transactional
    public CssdDistBill issue(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdApplyBill apply = applyBillMapper.selectById(String.valueOf(body.get("applyBillId")));
        if (apply == null || !List.of("submitted", "partial").contains(apply.getBillStatus())) {
            throw new BizException("发放必须挂已提交申领");
        }
        CssdDistBill dist = new CssdDistBill();
        dist.setId(support.id());
        dist.setBillNo(support.billNo("FF"));
        dist.setBillStatus("submitted");
        dist.setDistMode(str(body.getOrDefault("distMode", "direct")));
        dist.setDeptId(apply.getDeptId());
        dist.setDeptCode(apply.getDeptCode());
        dist.setDeptName(apply.getDeptName());
        dist.setApplyBillId(apply.getId());
        dist.setApplyBillNo(apply.getBillNo());
        if (body.get("cartId") != null) {
            CssdCart cart = cartMapper.selectById(String.valueOf(body.get("cartId")));
            if (cart != null) {
                dist.setCartId(cart.getId());
                dist.setCartCode(cart.getCartCode());
                dist.setCartName(cart.getCartName());
            }
        }
        dist.setDistStaffId(user.getStaffId());
        dist.setDistStaffNo(user.getStaffNo());
        dist.setDistStaffName(user.getStaffName());
        dist.setDistAt(support.now());
        boolean autoReceive = Boolean.TRUE.equals(body.get("autoReceive")) || "direct".equals(dist.getDistMode());
        if (autoReceive) {
            dist.setReceiverId(user.getStaffId());
            dist.setReceiverNo(user.getStaffNo());
            dist.setReceiverName(user.getStaffName());
            dist.setReceivedAt(support.now());
        }
        distBillMapper.insert(dist);
        List<String> barcodes = (List<String>) body.get("barcodes");
        int line = 1;
        for (String barcode : barcodes) {
            CssdPackInstance inst = mustIssuable(barcode);
            CssdApplyItem applyItem = matchApplyItem(apply.getId(), inst.getTemplateId());
            if (applyItem.getDistQty() + 1 > applyItem.getQty()) {
                throw new BizException("可少发不可多发: " + inst.getTemplateName());
            }
            CssdDistItem di = new CssdDistItem();
            di.setId(support.id());
            di.setBillId(dist.getId());
            di.setBillNo(dist.getBillNo());
            di.setLineNo(line++);
            di.setApplyBillId(apply.getId());
            di.setApplyBillNo(apply.getBillNo());
            di.setApplyItemId(applyItem.getId());
            di.setInstanceId(inst.getId());
            di.setBarcode(inst.getBarcode());
            di.setTemplateId(inst.getTemplateId());
            di.setTemplateCode(inst.getTemplateCode());
            di.setTemplateName(inst.getTemplateName());
            di.setSterilizeDate(inst.getSterilizeDate());
            di.setExpireDate(inst.getExpireDate());
            di.setDeviceNo(inst.getDeviceNo());
            di.setLoadNo(inst.getSterLoadNo());
            distItemMapper.insert(di);
            applyItem.setDistQty(applyItem.getDistQty() + 1);
            applyItemMapper.updateById(applyItem);
            inst.setStatus(autoReceive ? "received" : "distributed");
            inst.setApplyBillId(apply.getId());
            inst.setApplyBillNo(apply.getBillNo());
            inst.setDistBillId(dist.getId());
            inst.setDistBillNo(dist.getBillNo());
            inst.setLocDeptId(apply.getDeptId());
            inst.setLocDeptCode(apply.getDeptCode());
            inst.setLocDeptName(apply.getDeptName());
            instanceMapper.updateById(inst);
        }
        refreshApplyStatus(apply);
        return dist;
    }

    @Transactional
    public CssdDistBill receive(String distId) {
        LoginUser user = support.user();
        CssdDistBill dist = distBillMapper.selectById(distId);
        if (dist == null) {
            throw new BizException("发放单不存在");
        }
        dist.setReceiverId(user.getStaffId());
        dist.setReceiverNo(user.getStaffNo());
        dist.setReceiverName(user.getStaffName());
        dist.setReceivedAt(support.now());
        distBillMapper.updateById(dist);
        for (CssdDistItem di : distItemMapper.selectList(new LambdaQueryWrapper<CssdDistItem>()
                .eq(CssdDistItem::getBillId, distId))) {
            CssdPackInstance inst = instanceMapper.selectById(di.getInstanceId());
            if ("distributed".equals(inst.getStatus())) {
                inst.setStatus("received");
                instanceMapper.updateById(inst);
            }
        }
        return dist;
    }

    @Transactional
    public CssdPackInstance returnUnused(String barcode) {
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null) {
            throw new BizException("包不存在");
        }
        if (!"received".equals(inst.getStatus())) {
            throw new BizException("仅已接收未术前的完好包装可退无菌库");
        }
        if (support.isExpired(inst)) {
            throw new BizException("过期包不得退无菌库，请污染回收");
        }
        inst.setStatus(Integer.valueOf(1).equals(inst.getNeedStore()) ? "stored" : "released");
        instanceMapper.updateById(inst);
        return inst;
    }

    @Transactional
    public CssdUseCheck preCheck(Map<String, Object> body) {
        LoginUser user = support.user();
        List<String> barcodes = (List<String>) body.get("barcodes");
        String idem = str(body.get("idempotencyKey"));
        if (idem != null) {
            CssdUseCheck existed = useCheckMapper.selectOne(new LambdaQueryWrapper<CssdUseCheck>()
                    .eq(CssdUseCheck::getIdempotencyKey, idem));
            if (existed != null) {
                return existed;
            }
        }
        CssdUseCheck chk = new CssdUseCheck();
        chk.setId(support.id());
        chk.setCheckNo(support.billNo("HZ"));
        chk.setCheckKind("pre");
        chk.setCheckStatus("submitted");
        chk.setPatientNo(str(body.get("patientNo")));
        chk.setPatientName(str(body.get("patientName")));
        chk.setBedNo(str(body.get("bedNo")));
        chk.setSurgeryName(str(body.get("surgeryName")));
        chk.setHisPatientId(str(body.get("hisPatientId")));
        chk.setHisEncounterId(str(body.get("hisEncounterId")));
        chk.setIdempotencyKey(idem);
        if (body.get("surgeonId") != null) {
            CssdStaff s = staffMapper.selectById(String.valueOf(body.get("surgeonId")));
            if (s != null) {
                chk.setSurgeonId(s.getId());
                chk.setSurgeonNo(s.getStaffNo());
                chk.setSurgeonName(s.getStaffName());
            }
        }
        chk.setCheckerId(user.getStaffId());
        chk.setCheckerNo(user.getStaffNo());
        chk.setCheckerName(user.getStaffName());
        chk.setCheckedAt(support.now());
        if (barcodes != null && !barcodes.isEmpty()) {
            CssdPackInstance first = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                    .eq(CssdPackInstance::getBarcode, barcodes.get(0)));
            if (first != null) {
                chk.setDeptId(first.getLocDeptId());
                chk.setDeptCode(first.getLocDeptCode());
                chk.setDeptName(first.getLocDeptName());
            }
        }
        useCheckMapper.insert(chk);
        int line = 1;
        for (String barcode : barcodes) {
            CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                    .eq(CssdPackInstance::getBarcode, barcode));
            if (inst == null) {
                throw new BizException("包不存在");
            }
            if (!"received".equals(inst.getStatus())) {
                throw new BizException("术前核准须已接收: " + barcode);
            }
            if (support.isExpired(inst) || "recalled".equals(inst.getStatus()) || inst.getReleasedAt() == null) {
                throw new BizException("过期/召回/未放行包不得术前使用");
            }
            CssdUseCheckItem ui = new CssdUseCheckItem();
            ui.setId(support.id());
            ui.setCheckId(chk.getId());
            ui.setCheckNo(chk.getCheckNo());
            ui.setLineNo(line++);
            ui.setInstanceId(inst.getId());
            ui.setBarcode(inst.getBarcode());
            ui.setTemplateId(inst.getTemplateId());
            ui.setTemplateCode(inst.getTemplateCode());
            ui.setTemplateName(inst.getTemplateName());
            ui.setSterilizeDate(inst.getSterilizeDate());
            ui.setExpireDate(inst.getExpireDate());
            ui.setDeviceNo(inst.getDeviceNo());
            ui.setQtyOk(1);
            ui.setSterOk(1);
            useCheckItemMapper.insert(ui);
            inst.setStatus("pre_checked");
            inst.setPatientNo(chk.getPatientNo());
            inst.setPatientName(chk.getPatientName());
            inst.setBedNo(chk.getBedNo());
            inst.setSurgeonId(chk.getSurgeonId());
            inst.setSurgeonNo(chk.getSurgeonNo());
            inst.setSurgeonName(chk.getSurgeonName());
            inst.setSurgeryName(chk.getSurgeryName());
            inst.setHisPatientId(chk.getHisPatientId());
            inst.setHisEncounterId(chk.getHisEncounterId());
            instanceMapper.updateById(inst);
            writeHis(inst, chk);
        }
        List<String> lotIds = (List<String>) body.get("onceLotIds");
        if (lotIds != null) {
            for (String lotId : lotIds) {
                CssdOnceLot lot = onceLotMapper.selectById(lotId);
                if (lot != null && (Integer.valueOf(1).equals(lot.getIsBlocked())
                        || (lot.getExpireDate() != null && lot.getExpireDate().isBefore(LocalDate.now())))) {
                    throw new BizException("一次性批次已禁用或过期");
                }
            }
        }
        return chk;
    }

    @Transactional
    public CssdUseCheck postCheck(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdUseCheck chk = new CssdUseCheck();
        chk.setId(support.id());
        chk.setCheckNo(support.billNo("HZ"));
        chk.setCheckKind("post");
        chk.setCheckStatus("submitted");
        chk.setCheckerId(user.getStaffId());
        chk.setCheckerNo(user.getStaffNo());
        chk.setCheckerName(user.getStaffName());
        chk.setCheckedAt(support.now());
        useCheckMapper.insert(chk);
        List<String> barcodes = (List<String>) body.get("barcodes");
        int line = 1;
        for (String barcode : barcodes) {
            CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                    .eq(CssdPackInstance::getBarcode, barcode));
            if (inst == null) {
                throw new BizException("包不存在");
            }
            CssdUseCheckItem ui = new CssdUseCheckItem();
            ui.setId(support.id());
            ui.setCheckId(chk.getId());
            ui.setCheckNo(chk.getCheckNo());
            ui.setLineNo(line++);
            ui.setInstanceId(inst.getId());
            ui.setBarcode(inst.getBarcode());
            ui.setTemplateId(inst.getTemplateId());
            ui.setTemplateCode(inst.getTemplateCode());
            ui.setTemplateName(inst.getTemplateName());
            useCheckItemMapper.insert(ui);
            if ("pre_checked".equals(inst.getStatus())) {
                inst.setStatus("used");
                instanceMapper.updateById(inst);
            }
        }
        return chk;
    }

    @Transactional
    public CssdPackInstance correctPatient(String instanceId, Map<String, Object> body) {
        LoginUser user = support.user();
        if (!List.of("admin", "nurse_lead", "qc").contains(user.getRole())) {
            throw new BizException(403, "合患者纠错需要高权限");
        }
        CssdPackInstance inst = instanceMapper.selectById(instanceId);
        inst.setPatientNo(str(body.get("patientNo")));
        inst.setPatientName(str(body.get("patientName")));
        inst.setBedNo(str(body.get("bedNo")));
        instanceMapper.updateById(inst);
        return inst;
    }

    private CssdPackInstance mustIssuable(String barcode) {
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null) {
            throw new BizException("包不存在: " + barcode);
        }
        if (inst.getLegalPrintedAt() == null) {
            throw new BizException("未打法规签不得发放: " + barcode);
        }
        if (support.isExpired(inst)) {
            throw new BizException("过期不得发放");
        }
        if (List.of("recalled", "ster_failed", "voided", "expired").contains(inst.getStatus())) {
            throw new BizException("当前状态不得发放");
        }
        if (isTest(inst.getPackCategory())) {
            throw new BizException("试验包不得发放临床");
        }
        if (Integer.valueOf(1).equals(inst.getNeedStore())) {
            if (!"stored".equals(inst.getStatus())) {
                throw new BizException("常规包须先入库再发放");
            }
        } else if (!"released".equals(inst.getStatus())) {
            throw new BizException("直发包须已放行");
        }
        return inst;
    }

    private CssdApplyItem matchApplyItem(String applyId, String templateId) {
        CssdApplyItem item = applyItemMapper.selectOne(new LambdaQueryWrapper<CssdApplyItem>()
                .eq(CssdApplyItem::getBillId, applyId).eq(CssdApplyItem::getTemplateId, templateId));
        if (item == null) {
            throw new BizException("禁止发放未申请的包类型");
        }
        return item;
    }

    private void refreshApplyStatus(CssdApplyBill apply) {
        List<CssdApplyItem> items = applyItemMapper.selectList(new LambdaQueryWrapper<CssdApplyItem>()
                .eq(CssdApplyItem::getBillId, apply.getId()));
        boolean all = items.stream().allMatch(i -> i.getDistQty() >= i.getQty());
        boolean any = items.stream().anyMatch(i -> i.getDistQty() > 0);
        apply.setBillStatus(all ? "closed" : (any ? "partial" : "submitted"));
        applyBillMapper.updateById(apply);
    }

    private void writeHis(CssdPackInstance inst, CssdUseCheck chk) {
        String key = (inst.getBarcode() == null ? "" : inst.getBarcode()) + "|"
                + (chk.getHisEncounterId() == null ? "manual" : chk.getHisEncounterId()) + "|use";
        CssdHisSyncLog log = new CssdHisSyncLog();
        log.setId(IdUtils.uuid7());
        log.setDirection("out");
        log.setBizType("use_bind");
        log.setIdempotencyKey(key);
        log.setSyncStatus("ok");
        log.setInstanceId(inst.getId());
        log.setBarcode(inst.getBarcode());
        log.setTemplateCode(inst.getTemplateCode());
        log.setTemplateName(inst.getTemplateName());
        log.setPatientNo(chk.getPatientNo());
        log.setPatientName(chk.getPatientName());
        log.setHisPatientId(chk.getHisPatientId());
        log.setHisEncounterId(chk.getHisEncounterId());
        log.setSyncedAt(support.now());
        try {
            hisLogMapper.insert(log);
            if (chk.getHisEncounterId() != null) {
                hisAdapter.pushBind(inst.getBarcode(), chk.getHisEncounterId(), Map.of("patientNo", chk.getPatientNo()));
            }
        } catch (Exception ignored) {
            // unique idempotency: treat as already synced
        }
    }

    private boolean isTest(String cat) {
        return cat != null && cat.startsWith("test_");
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
