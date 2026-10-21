package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdBasket;
import com.cssd.infra.entity.CssdDept;
import com.cssd.infra.entity.CssdDevice;
import com.cssd.infra.entity.CssdDeviceProgram;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdPackInstanceItem;
import com.cssd.infra.entity.CssdPackTemplate;
import com.cssd.infra.entity.CssdPackTemplateItem;
import com.cssd.infra.entity.CssdRecycleBill;
import com.cssd.infra.entity.CssdRecycleItem;
import com.cssd.infra.entity.CssdWashBatch;
import com.cssd.infra.entity.CssdWashItem;
import com.cssd.infra.mapper.CssdBasketMapper;
import com.cssd.infra.mapper.CssdDeviceMapper;
import com.cssd.infra.mapper.CssdDeviceProgramMapper;
import com.cssd.infra.mapper.CssdPackInstanceItemMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdPackTemplateItemMapper;
import com.cssd.infra.mapper.CssdPackTemplateMapper;
import com.cssd.infra.mapper.CssdRecycleBillMapper;
import com.cssd.infra.mapper.CssdRecycleItemMapper;
import com.cssd.infra.mapper.CssdWashBatchMapper;
import com.cssd.infra.mapper.CssdWashItemMapper;
import com.cssd.security.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RecycleWashPackService {
    private final FlowSupport support;
    private final CssdRecycleBillMapper recycleBillMapper;
    private final CssdRecycleItemMapper recycleItemMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdPackInstanceItemMapper instanceItemMapper;
    private final CssdPackTemplateMapper templateMapper;
    private final CssdPackTemplateItemMapper templateItemMapper;
    private final CssdBasketMapper basketMapper;
    private final CssdWashBatchMapper washBatchMapper;
    private final CssdWashItemMapper washItemMapper;
    private final CssdDeviceMapper deviceMapper;
    private final CssdDeviceProgramMapper programMapper;

    public RecycleWashPackService(FlowSupport support, CssdRecycleBillMapper recycleBillMapper,
                                  CssdRecycleItemMapper recycleItemMapper, CssdPackInstanceMapper instanceMapper,
                                  CssdPackInstanceItemMapper instanceItemMapper, CssdPackTemplateMapper templateMapper,
                                  CssdPackTemplateItemMapper templateItemMapper, CssdBasketMapper basketMapper,
                                  CssdWashBatchMapper washBatchMapper, CssdWashItemMapper washItemMapper,
                                  CssdDeviceMapper deviceMapper, CssdDeviceProgramMapper programMapper) {
        this.support = support;
        this.recycleBillMapper = recycleBillMapper;
        this.recycleItemMapper = recycleItemMapper;
        this.instanceMapper = instanceMapper;
        this.instanceItemMapper = instanceItemMapper;
        this.templateMapper = templateMapper;
        this.templateItemMapper = templateItemMapper;
        this.basketMapper = basketMapper;
        this.washBatchMapper = washBatchMapper;
        this.washItemMapper = washItemMapper;
        this.deviceMapper = deviceMapper;
        this.programMapper = programMapper;
    }

    @Transactional
    public CssdRecycleBill submitRecycle(Map<String, Object> body) {
        LoginUser user = support.user();
        String deptId = str(body.get("deptId"));
        CssdDept dept = support.dept(deptId);
        CssdRecycleBill bill = new CssdRecycleBill();
        bill.setId(support.id());
        bill.setBillNo(support.billNo("HS"));
        bill.setBillStatus("submitted");
        bill.setDeptId(dept.getId());
        bill.setDeptCode(dept.getDeptCode());
        bill.setDeptName(dept.getDeptName());
        bill.setRecycAt(support.now());
        bill.setRecycStaffId(user.getStaffId());
        bill.setRecycStaffNo(user.getStaffNo());
        bill.setRecycStaffName(user.getStaffName());
        bill.setSourceKind("web");
        recycleBillMapper.insert(bill);
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        if (items == null || items.isEmpty()) {
            throw new BizException("回收明细不能为空");
        }
        int line = 1;
        for (Map<String, Object> it : items) {
            CssdRecycleItem row = new CssdRecycleItem();
            row.setId(support.id());
            row.setBillId(bill.getId());
            row.setBillNo(bill.getBillNo());
            row.setLineNo(line++);
            String barcode = str(it.get("barcode"));
            if (barcode != null && !barcode.isBlank()) {
                CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                        .eq(CssdPackInstance::getBarcode, barcode));
                if (inst == null) {
                    throw new BizException("条码不存在: " + barcode);
                }
                row.setInstanceId(inst.getId());
                row.setBarcode(inst.getBarcode());
                row.setTemplateId(inst.getTemplateId());
                row.setTemplateCode(inst.getTemplateCode());
                row.setTemplateName(inst.getTemplateName());
                inst.setStatus("recycled");
                instanceMapper.updateById(inst);
            } else {
                CssdPackTemplate tpl = templateMapper.selectById(str(it.get("templateId")));
                if (tpl == null) {
                    throw new BizException("无码入洗必须选择包模板");
                }
                row.setTemplateId(tpl.getId());
                row.setTemplateCode(tpl.getTemplateCode());
                row.setTemplateName(tpl.getTemplateName());
            }
            row.setQty(num(it.get("qty"), 1));
            row.setPackedQty(0);
            row.setUrgentFlag(num(it.get("urgentFlag"), 0));
            row.setContaminationFlag(num(it.get("contaminationFlag"), 0));
            row.setInfectionType(str(it.get("infectionType")));
            String basketId = str(it.get("basketId"));
            if (basketId != null) {
                CssdBasket basket = basketMapper.selectById(basketId);
                if (basket != null) {
                    row.setBasketId(basket.getId());
                    row.setBasketCode(basket.getBasketCode());
                    row.setBasketName(basket.getBasketName());
                    basket.setBasketStatus("pending_wash");
                    basketMapper.updateById(basket);
                }
            }
            recycleItemMapper.insert(row);
        }
        return bill;
    }

    @Transactional
    public CssdWashBatch saveWash(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdWashBatch batch = new CssdWashBatch();
        batch.setId(support.id());
        batch.setBatchNo(support.billNo("QX"));
        batch.setWashMode(str(body.getOrDefault("washMode", "machine")));
        batch.setTemplateWashMode(str(body.get("templateWashMode")));
        batch.setModeOverridden(num(body.get("modeOverridden"), 0));
        batch.setOverrideReason(str(body.get("overrideReason")));
        if (Integer.valueOf(1).equals(batch.getModeOverridden())
                && (batch.getOverrideReason() == null || batch.getOverrideReason().isBlank())) {
            throw new BizException("覆盖清洗方式必须填写原因");
        }
        batch.setBatchStatus("draft");
        String deviceId = str(body.get("deviceId"));
        if (deviceId != null) {
            CssdDevice d = deviceMapper.selectById(deviceId);
            if (d != null) {
                batch.setDeviceId(d.getId());
                batch.setDeviceCode(d.getDeviceCode());
                batch.setDeviceName(d.getDeviceName());
                batch.setDeviceNo(d.getDeviceNo());
            }
        }
        String programId = str(body.get("programId"));
        if (programId != null) {
            CssdDeviceProgram p = programMapper.selectById(programId);
            if (p != null) {
                batch.setProgramId(p.getId());
                batch.setProgramCode(p.getProgramCode());
                batch.setProgramName(p.getProgramName());
            }
        }
        batch.setOperatorId(user.getStaffId());
        batch.setOperatorNo(user.getStaffNo());
        batch.setOperatorName(user.getStaffName());
        washBatchMapper.insert(batch);
        List<String> recycleItemIds = (List<String>) body.get("recycleItemIds");
        if (recycleItemIds == null || recycleItemIds.isEmpty()) {
            throw new BizException("请选择入洗回收明细");
        }
        int line = 1;
        for (String rid : recycleItemIds) {
            CssdRecycleItem ri = recycleItemMapper.selectById(rid);
            if (ri == null) {
                throw new BizException("回收明细不存在");
            }
            CssdWashItem wi = new CssdWashItem();
            wi.setId(support.id());
            wi.setBatchId(batch.getId());
            wi.setBatchNo(batch.getBatchNo());
            wi.setLineNo(line++);
            wi.setBasketId(ri.getBasketId());
            wi.setBasketCode(ri.getBasketCode());
            wi.setBasketName(ri.getBasketName());
            wi.setRecycleBillId(ri.getBillId());
            wi.setRecycleBillNo(ri.getBillNo());
            wi.setRecycleItemId(ri.getId());
            wi.setInstanceId(ri.getInstanceId());
            wi.setBarcode(ri.getBarcode());
            wi.setTemplateId(ri.getTemplateId());
            wi.setTemplateCode(ri.getTemplateCode());
            wi.setTemplateName(ri.getTemplateName());
            washItemMapper.insert(wi);
        }
        return batch;
    }

    @Transactional
    public CssdWashBatch startWash(String id) {
        CssdWashBatch batch = washBatchMapper.selectById(id);
        if (batch == null || !"draft".equals(batch.getBatchStatus())) {
            throw new BizException("仅草稿批次可开始");
        }
        batch.setBatchStatus("running");
        batch.setStartedAt(support.now());
        washBatchMapper.updateById(batch);
        for (CssdWashItem wi : washItemMapper.selectList(new LambdaQueryWrapper<CssdWashItem>()
                .eq(CssdWashItem::getBatchId, id))) {
            if (wi.getBasketId() != null) {
                CssdBasket b = basketMapper.selectById(wi.getBasketId());
                if (b != null) {
                    b.setBasketStatus("washing");
                    basketMapper.updateById(b);
                }
            }
        }
        return batch;
    }

    @Transactional
    public CssdWashBatch finishWash(String id, Map<String, Object> body) {
        CssdWashBatch batch = washBatchMapper.selectById(id);
        if (batch == null || !"running".equals(batch.getBatchStatus())) {
            throw new BizException("仅进行中批次可结束");
        }
        batch.setBatchStatus("finished");
        batch.setFinishedAt(support.now());
        if (body != null) {
            batch.setReportUrl(str(body.get("reportUrl")));
        }
        washBatchMapper.updateById(batch);
        return batch;
    }

    @Transactional
    public CssdWashBatch checkWash(String id, Map<String, Object> body) {
        LoginUser user = support.user();
        CssdWashBatch batch = washBatchMapper.selectById(id);
        if (batch == null || !"finished".equals(batch.getBatchStatus())) {
            throw new BizException("须先结束清洗再检测");
        }
        String result = str(body.getOrDefault("checkResult", "pass"));
        if ("pass".equals(batch.getCheckResult()) && "pass".equals(result)) {
            return batch;
        }
        if (batch.getCheckResult() != null && !batch.getCheckResult().equals(result)
                && "pass".equals(result) && "fail".equals(batch.getCheckResult())) {
            throw new BizException("禁止无痕把不合格改为合格，请复洗");
        }
        batch.setCheckResult(result);
        batch.setCheckerId(user.getStaffId());
        batch.setCheckerNo(user.getStaffNo());
        batch.setCheckerName(user.getStaffName());
        batch.setCheckedAt(support.now());
        washBatchMapper.updateById(batch);
        if ("pass".equals(result) || "fail".equals(result)) {
            releaseBaskets(id);
        }
        return batch;
    }

    @Transactional
    public CssdWashBatch rewash(String id) {
        CssdWashBatch old = washBatchMapper.selectById(id);
        if (old == null || !"fail".equals(old.getCheckResult())) {
            throw new BizException("仅检测不合格批次可复洗");
        }
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("washMode", old.getWashMode());
        body.put("deviceId", old.getDeviceId());
        body.put("programId", old.getProgramId());
        List<String> ids = washItemMapper.selectList(new LambdaQueryWrapper<CssdWashItem>()
                        .eq(CssdWashItem::getBatchId, id))
                .stream().map(CssdWashItem::getRecycleItemId).toList();
        body.put("recycleItemIds", ids);
        CssdWashBatch neu = saveWash(body);
        return startWash(neu.getId());
    }

    @Transactional
    public CssdPackInstance pack(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdPackTemplate tpl = templateMapper.selectById(str(body.get("templateId")));
        if (tpl == null || Integer.valueOf(0).equals(tpl.getIsActive())) {
            throw new BizException("模板不存在或已禁用");
        }
        if (Integer.valueOf(0).equals(tpl.getNeedPack())) {
            throw new BizException("该模板不走配包");
        }
        String recycleItemId = str(body.get("recycleItemId"));
        CssdRecycleItem ri = recycleItemMapper.selectById(recycleItemId);
        if (ri == null) {
            throw new BizException("必须指定来源回收明细");
        }
        int remain = ri.getQty() - (ri.getPackedQty() == null ? 0 : ri.getPackedQty());
        int qty = num(body.get("qty"), 1);
        if (qty > remain) {
            throw new BizException("超过回收剩余可配数量");
        }
        if (Integer.valueOf(1).equals(tpl.getNeedWash()) && !washedPass(recycleItemId)) {
            throw new BizException("来源回收明细须出现在检测合格的清洗批上");
        }
        CssdPackInstance inst = new CssdPackInstance();
        inst.setId(support.id());
        inst.setBarcode("P" + support.billNo("PK").substring(2));
        inst.setLabelCarrier("qr");
        inst.setTemplateId(tpl.getId());
        inst.setTemplateCode(tpl.getTemplateCode());
        inst.setTemplateName(tpl.getTemplateName());
        inst.setPackCategory(tpl.getPackCategory());
        inst.setStatus("packed");
        inst.setMaterialId(tpl.getMaterialId());
        inst.setMaterialCode(tpl.getMaterialCode());
        inst.setMaterialName(tpl.getMaterialName());
        inst.setShelfLifeDays(tpl.getShelfLifeDays());
        inst.setIsImplant(tpl.getIsImplant());
        inst.setNeedWash(tpl.getNeedWash());
        inst.setWashMode(tpl.getWashMode());
        inst.setNeedPack(tpl.getNeedPack());
        inst.setNeedStore(tpl.getNeedStore());
        inst.setNeedRecycle(tpl.getNeedRecycle());
        inst.setSterMethod(tpl.getSterMethod());
        inst.setUrgentFlag(ri.getUrgentFlag());
        inst.setOwnerDeptId(tpl.getOwnerDeptId());
        inst.setOwnerDeptCode(tpl.getOwnerDeptCode());
        inst.setOwnerDeptName(tpl.getOwnerDeptName());
        inst.setPackerId(user.getStaffId());
        inst.setPackerNo(user.getStaffNo());
        inst.setPackerName(user.getStaffName());
        inst.setPackedAt(support.now());
        inst.setRecycleBillId(ri.getBillId());
        inst.setRecycleBillNo(ri.getBillNo());
        inst.setRecycleItemId(ri.getId());
        inst.setPrevInstanceId(ri.getInstanceId());
        instanceMapper.insert(inst);
        List<CssdPackTemplateItem> tItems = templateItemMapper.selectList(
                new LambdaQueryWrapper<CssdPackTemplateItem>().eq(CssdPackTemplateItem::getTemplateId, tpl.getId())
                        .orderByAsc(CssdPackTemplateItem::getLineNo));
        Map<String, Object> actual = body.get("actualQty") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
        for (CssdPackTemplateItem ti : tItems) {
            CssdPackInstanceItem ii = new CssdPackInstanceItem();
            ii.setId(support.id());
            ii.setInstanceId(inst.getId());
            ii.setBarcode(inst.getBarcode());
            ii.setLineNo(ti.getLineNo());
            ii.setInstrumentId(ti.getInstrumentId());
            ii.setInstrumentCode(ti.getInstrumentCode());
            ii.setInstrumentName(ti.getInstrumentName());
            ii.setSpec(ti.getSpec());
            ii.setModel(ti.getModel());
            ii.setQty(ti.getQty());
            Object av = actual.get(ti.getId());
            ii.setActualQty(av == null ? ti.getQty() : num(av, ti.getQty()));
            instanceItemMapper.insert(ii);
        }
        ri.setPackedQty((ri.getPackedQty() == null ? 0 : ri.getPackedQty()) + qty);
        recycleItemMapper.updateById(ri);
        return inst;
    }

    @Transactional
    public CssdPackInstance checkPack(String instanceId, Map<String, Object> body) {
        LoginUser user = support.user();
        CssdPackInstance inst = instanceMapper.selectById(instanceId);
        if (inst == null) {
            throw new BizException("实例不存在");
        }
        support.requireStatus(inst, "packed");
        boolean pass = !"0".equals(String.valueOf(body.getOrDefault("pass", "1")));
        if (!pass) {
            inst.setRemark(str(body.get("reason")));
            instanceMapper.updateById(inst);
            return inst;
        }
        if (user.getStaffId().equals(inst.getPackerId())) {
            throw new BizException("配包人与核包人不得为同一人");
        }
        inst.setStatus("checked");
        inst.setCheckerId(user.getStaffId());
        inst.setCheckerNo(user.getStaffNo());
        inst.setCheckerName(user.getStaffName());
        inst.setCheckedAt(support.now());
        instanceMapper.updateById(inst);
        return inst;
    }

    @Transactional
    public CssdPackInstance voidPack(String instanceId) {
        CssdPackInstance inst = instanceMapper.selectById(instanceId);
        if (inst == null) {
            throw new BizException("实例不存在");
        }
        support.requireStatus(inst, "packed");
        inst.setStatus("voided");
        instanceMapper.updateById(inst);
        instanceMapper.deleteById(inst.getId());
        if (inst.getRecycleItemId() != null) {
            CssdRecycleItem ri = recycleItemMapper.selectById(inst.getRecycleItemId());
            if (ri != null) {
                ri.setPackedQty(Math.max(0, (ri.getPackedQty() == null ? 0 : ri.getPackedQty()) - 1));
                recycleItemMapper.updateById(ri);
            }
        }
        return inst;
    }

    private boolean washedPass(String recycleItemId) {
        List<CssdWashItem> items = washItemMapper.selectList(new LambdaQueryWrapper<CssdWashItem>()
                .eq(CssdWashItem::getRecycleItemId, recycleItemId));
        for (CssdWashItem wi : items) {
            CssdWashBatch b = washBatchMapper.selectById(wi.getBatchId());
            if (b != null && "pass".equals(b.getCheckResult())) {
                return true;
            }
        }
        return false;
    }

    private void releaseBaskets(String batchId) {
        for (CssdWashItem wi : washItemMapper.selectList(new LambdaQueryWrapper<CssdWashItem>()
                .eq(CssdWashItem::getBatchId, batchId))) {
            if (wi.getBasketId() == null) {
                continue;
            }
            CssdBasket b = basketMapper.selectById(wi.getBasketId());
            if (b != null) {
                b.setBasketStatus("idle");
                basketMapper.updateById(b);
            }
        }
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int num(Object o, int def) {
        if (o == null) {
            return def;
        }
        if (o instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
