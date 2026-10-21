package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdApplyBill;
import com.cssd.infra.entity.CssdDistBill;
import com.cssd.infra.entity.CssdMonitor;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdPackInstanceItem;
import com.cssd.infra.entity.CssdRecall;
import com.cssd.infra.entity.CssdRecycleBill;
import com.cssd.infra.entity.CssdSterLoad;
import com.cssd.infra.entity.CssdUseCheck;
import com.cssd.infra.entity.CssdWashBatch;
import com.cssd.infra.mapper.CssdApplyBillMapper;
import com.cssd.infra.mapper.CssdDistBillMapper;
import com.cssd.infra.mapper.CssdMonitorMapper;
import com.cssd.infra.mapper.CssdPackInstanceItemMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdPackTemplateMapper;
import com.cssd.infra.mapper.CssdRecallMapper;
import com.cssd.infra.mapper.CssdRecycleBillMapper;
import com.cssd.infra.mapper.CssdSterLoadMapper;
import com.cssd.infra.mapper.CssdUseCheckMapper;
import com.cssd.infra.mapper.CssdWashBatchMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraceQueryService {
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdPackInstanceItemMapper itemMapper;
    private final CssdRecycleBillMapper recycleBillMapper;
    private final CssdWashBatchMapper washBatchMapper;
    private final CssdSterLoadMapper loadMapper;
    private final CssdMonitorMapper monitorMapper;
    private final CssdApplyBillMapper applyBillMapper;
    private final CssdDistBillMapper distBillMapper;
    private final CssdUseCheckMapper useCheckMapper;
    private final CssdRecallMapper recallMapper;
    private final CssdPackTemplateMapper templateMapper;

    public TraceQueryService(CssdPackInstanceMapper instanceMapper, CssdPackInstanceItemMapper itemMapper,
                             CssdRecycleBillMapper recycleBillMapper, CssdWashBatchMapper washBatchMapper,
                             CssdSterLoadMapper loadMapper, CssdMonitorMapper monitorMapper,
                             CssdApplyBillMapper applyBillMapper, CssdDistBillMapper distBillMapper,
                             CssdUseCheckMapper useCheckMapper, CssdRecallMapper recallMapper,
                             CssdPackTemplateMapper templateMapper) {
        this.instanceMapper = instanceMapper;
        this.itemMapper = itemMapper;
        this.recycleBillMapper = recycleBillMapper;
        this.washBatchMapper = washBatchMapper;
        this.loadMapper = loadMapper;
        this.monitorMapper = monitorMapper;
        this.applyBillMapper = applyBillMapper;
        this.distBillMapper = distBillMapper;
        this.useCheckMapper = useCheckMapper;
        this.recallMapper = recallMapper;
        this.templateMapper = templateMapper;
    }

    public Map<String, Object> byCode(String code) {
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, code));
        if (inst == null) {
            inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                    .eq(CssdPackInstance::getRfidUid, code));
        }
        if (inst == null) {
            throw new BizException("未找到该条码/RFID对应实例");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("instance", inst);
        data.put("items", itemMapper.selectList(new LambdaQueryWrapper<CssdPackInstanceItem>()
                .eq(CssdPackInstanceItem::getInstanceId, inst.getId())));
        if (inst.getRecycleBillId() != null) {
            data.put("recycle", recycleBillMapper.selectById(inst.getRecycleBillId()));
        }
        if (inst.getWashBatchId() != null) {
            data.put("wash", washBatchMapper.selectById(inst.getWashBatchId()));
        }
        if (inst.getSterLoadId() != null) {
            CssdSterLoad load = loadMapper.selectById(inst.getSterLoadId());
            data.put("sterLoad", load);
            data.put("monitors", monitorMapper.selectList(new LambdaQueryWrapper<CssdMonitor>()
                    .eq(CssdMonitor::getLoadId, inst.getSterLoadId())));
            data.put("sameLoad", instanceMapper.selectList(new LambdaQueryWrapper<CssdPackInstance>()
                    .eq(CssdPackInstance::getSterLoadId, inst.getSterLoadId())));
        }
        if (inst.getApplyBillId() != null) {
            data.put("apply", applyBillMapper.selectById(inst.getApplyBillId()));
        }
        if (inst.getDistBillId() != null) {
            data.put("dist", distBillMapper.selectById(inst.getDistBillId()));
        }
        data.put("useChecks", useCheckMapper.selectList(new LambdaQueryWrapper<CssdUseCheck>()
                .inSql(CssdUseCheck::getId,
                        "select check_id from cssd.cssd_use_check_item where instance_id='" + inst.getId() + "' and is_deleted=0")));
        return data;
    }

    public Map<String, Object> alerts() {
        LocalDate today = LocalDate.now();
        LocalDate warn = today.plusDays(7);
        Map<String, Object> m = new HashMap<>();
        m.put("expiring", instanceMapper.selectList(new LambdaQueryWrapper<CssdPackInstance>()
                .le(CssdPackInstance::getExpireDate, warn)
                .ge(CssdPackInstance::getExpireDate, today)
                .in(CssdPackInstance::getStatus, List.of("released", "stored", "distributed", "received"))));
        m.put("expired", instanceMapper.selectList(new LambdaQueryWrapper<CssdPackInstance>()
                .lt(CssdPackInstance::getExpireDate, today)
                .in(CssdPackInstance::getStatus, List.of("released", "stored", "distributed", "received"))));
        m.put("unrecycled", instanceMapper.selectList(new LambdaQueryWrapper<CssdPackInstance>()
                .in(CssdPackInstance::getStatus, List.of("distributed", "received", "pre_checked", "used"))));
        m.put("recalls", recallMapper.selectList(new LambdaQueryWrapper<CssdRecall>()
                .orderByDesc(CssdRecall::getCreatedAt).last("limit 50")));
        m.put("lowStock", templateMapper.selectList(null));
        return m;
    }
}
