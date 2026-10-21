package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.infra.entity.CssdDevice;
import com.cssd.infra.entity.CssdDeviceProgram;
import com.cssd.infra.entity.CssdMonitor;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdRecall;
import com.cssd.infra.entity.CssdRecallItem;
import com.cssd.infra.entity.CssdSterLoad;
import com.cssd.infra.entity.CssdSterLoadItem;
import com.cssd.infra.mapper.CssdDeviceMapper;
import com.cssd.infra.mapper.CssdDeviceProgramMapper;
import com.cssd.infra.mapper.CssdMonitorMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdRecallItemMapper;
import com.cssd.infra.mapper.CssdRecallMapper;
import com.cssd.infra.mapper.CssdSterLoadItemMapper;
import com.cssd.infra.mapper.CssdSterLoadMapper;
import com.cssd.security.LoginUser;
import com.cssd.sys.DictConfigController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SterMonitorService {
    private final FlowSupport support;
    private final CssdSterLoadMapper loadMapper;
    private final CssdSterLoadItemMapper loadItemMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdDeviceMapper deviceMapper;
    private final CssdDeviceProgramMapper programMapper;
    private final CssdMonitorMapper monitorMapper;
    private final CssdRecallMapper recallMapper;
    private final CssdRecallItemMapper recallItemMapper;
    private final DictConfigController configs;
    private final ObjectMapper objectMapper;

    public SterMonitorService(FlowSupport support, CssdSterLoadMapper loadMapper, CssdSterLoadItemMapper loadItemMapper,
                              CssdPackInstanceMapper instanceMapper, CssdDeviceMapper deviceMapper,
                              CssdDeviceProgramMapper programMapper, CssdMonitorMapper monitorMapper,
                              CssdRecallMapper recallMapper, CssdRecallItemMapper recallItemMapper,
                              DictConfigController configs, ObjectMapper objectMapper) {
        this.support = support;
        this.loadMapper = loadMapper;
        this.loadItemMapper = loadItemMapper;
        this.instanceMapper = instanceMapper;
        this.deviceMapper = deviceMapper;
        this.programMapper = programMapper;
        this.monitorMapper = monitorMapper;
        this.recallMapper = recallMapper;
        this.recallItemMapper = recallItemMapper;
        this.configs = configs;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CssdSterLoad createLoad(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdDevice device = deviceMapper.selectById(String.valueOf(body.get("deviceId")));
        if (device == null || Integer.valueOf(0).equals(device.getIsActive())) {
            throw new BizException("灭菌器不可用");
        }
        CssdSterLoad load = new CssdSterLoad();
        load.setId(support.id());
        load.setLoadNo(support.billNo("MJ"));
        load.setLoadStatus("draft");
        load.setDeviceId(device.getId());
        load.setDeviceCode(device.getDeviceCode());
        load.setDeviceName(device.getDeviceName());
        load.setDeviceNo(device.getDeviceNo());
        load.setSterTempClass(device.getSterTempClass());
        if (body.get("programId") != null) {
            CssdDeviceProgram p = programMapper.selectById(String.valueOf(body.get("programId")));
            if (p != null) {
                load.setProgramId(p.getId());
                load.setProgramCode(p.getProgramCode());
                load.setProgramName(p.getProgramName());
            }
        }
        load.setOperatorId(user.getStaffId());
        load.setOperatorNo(user.getStaffNo());
        load.setOperatorName(user.getStaffName());
        load.setMainLoadDesc(str(body.get("mainLoadDesc")));
        load.setReleaseStatus("pending");
        loadMapper.insert(load);
        return load;
    }

    @Transactional
    public CssdSterLoadItem addItem(String loadId, String barcode) {
        CssdSterLoad load = loadMapper.selectById(loadId);
        if (load == null || !"draft".equals(load.getLoadStatus())) {
            throw new BizException("仅草稿锅次可装载");
        }
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null) {
            throw new BizException("包不存在");
        }
        if (!"loan".equals(inst.getPackCategory())) {
            support.requireStatus(inst, "checked");
        } else if (!"checked".equals(inst.getStatus()) && !"packed".equals(inst.getStatus())) {
            throw new BizException("寄消包状态不可装锅");
        }
        if (inst.getSterMethod() != null && load.getSterTempClass() != null
                && !inst.getSterMethod().equals(load.getSterTempClass())) {
            throw new BizException("灭菌方式与设备类型不匹配");
        }
        if (inst.getSterMethod() == null) {
            throw new BizException("实例灭菌方式为空，不可入临床锅");
        }
        CssdSterLoadItem item = new CssdSterLoadItem();
        item.setId(support.id());
        item.setLoadId(load.getId());
        item.setLoadNo(load.getLoadNo());
        long cnt = loadItemMapper.selectCount(new LambdaQueryWrapper<CssdSterLoadItem>().eq(CssdSterLoadItem::getLoadId, loadId));
        item.setLineNo((int) cnt + 1);
        item.setInstanceId(inst.getId());
        item.setBarcode(inst.getBarcode());
        item.setTemplateId(inst.getTemplateId());
        item.setTemplateCode(inst.getTemplateCode());
        item.setTemplateName(inst.getTemplateName());
        item.setPackCategory(inst.getPackCategory());
        item.setIsImplant(inst.getIsImplant());
        item.setDeviceId(load.getDeviceId());
        item.setDeviceCode(load.getDeviceCode());
        item.setDeviceName(load.getDeviceName());
        item.setDeviceNo(load.getDeviceNo());
        loadItemMapper.insert(item);
        if (Integer.valueOf(1).equals(inst.getIsImplant())) {
            load.setHasImplant(1);
            loadMapper.updateById(load);
        }
        return item;
    }

    @Transactional
    public void removeItem(String loadId, String itemId) {
        CssdSterLoad load = loadMapper.selectById(loadId);
        if (load == null || !"draft".equals(load.getLoadStatus())) {
            throw new BizException("灭菌开始后锁定明细");
        }
        loadItemMapper.deleteById(itemId);
    }

    @Transactional
    public CssdSterLoad start(String loadId) {
        CssdSterLoad load = loadMapper.selectById(loadId);
        if (load == null || !"draft".equals(load.getLoadStatus())) {
            throw new BizException("仅草稿可开始灭菌");
        }
        if (clinicalLoad(load) && "high".equals(load.getSterTempClass()) && !bdPassToday(load.getDeviceId())) {
            throw new BizException("高温灭菌器当日无合格BD，禁止开始临床锅次");
        }
        if (deviceBlocked(load.getDeviceId())) {
            throw new BizException("该灭菌器生物失败后尚未再次合格，禁止临床锅");
        }
        load.setLoadStatus("running");
        load.setStartedAt(support.now());
        loadMapper.updateById(load);
        for (CssdSterLoadItem it : items(loadId)) {
            CssdPackInstance inst = instanceMapper.selectById(it.getInstanceId());
            inst.setStatus("in_ster");
            inst.setSterLoadId(load.getId());
            inst.setSterLoadNo(load.getLoadNo());
            inst.setDeviceId(load.getDeviceId());
            inst.setDeviceCode(load.getDeviceCode());
            inst.setDeviceName(load.getDeviceName());
            inst.setDeviceNo(load.getDeviceNo());
            inst.setProgramId(load.getProgramId());
            inst.setProgramCode(load.getProgramCode());
            inst.setProgramName(load.getProgramName());
            instanceMapper.updateById(inst);
        }
        return load;
    }

    @Transactional
    public CssdSterLoad finish(String loadId, Map<String, Object> body) {
        CssdSterLoad load = loadMapper.selectById(loadId);
        if (load == null || !"running".equals(load.getLoadStatus())) {
            throw new BizException("仅进行中锅次可结束");
        }
        LocalDate sterDate = body.get("sterilizeDate") != null
                ? LocalDate.parse(String.valueOf(body.get("sterilizeDate")))
                : LocalDate.now();
        load.setLoadStatus("finished");
        load.setFinishedAt(support.now());
        load.setSterilizeDate(sterDate);
        if (body.get("paramsJson") != null) {
            try {
                load.setParamsJson(objectMapper.writeValueAsString(body.get("paramsJson")));
            } catch (Exception e) {
                load.setParamsJson(String.valueOf(body.get("paramsJson")));
            }
        }
        load.setReportUrl(str(body.get("reportUrl")));
        loadMapper.updateById(load);
        for (CssdSterLoadItem it : items(loadId)) {
            CssdPackInstance inst = instanceMapper.selectById(it.getInstanceId());
            inst.setStatus("ster_done");
            inst.setSterilizedAt(load.getFinishedAt());
            inst.setSterilizeDate(sterDate);
            int days = inst.getShelfLifeDays() == null ? 14 : inst.getShelfLifeDays();
            inst.setExpireDate(sterDate.plusDays(days));
            instanceMapper.updateById(inst);
            it.setSterilizeDate(sterDate);
            it.setExpireDate(inst.getExpireDate());
            loadItemMapper.updateById(it);
        }
        return load;
    }

    @Transactional
    public CssdPackInstance wetFail(String instanceId, String reason) {
        CssdPackInstance inst = instanceMapper.selectById(instanceId);
        if (inst == null) {
            throw new BizException("实例不存在");
        }
        inst.setStatus("ster_failed");
        inst.setRemark(reason);
        instanceMapper.updateById(inst);
        return inst;
    }

    @Transactional
    public CssdMonitor submitMonitor(Map<String, Object> body) {
        LoginUser user = support.user();
        CssdDevice device = deviceMapper.selectById(String.valueOf(body.get("deviceId")));
        if (device == null) {
            throw new BizException("设备不存在");
        }
        CssdMonitor m = new CssdMonitor();
        m.setId(support.id());
        m.setMonitorNo(support.billNo("JC"));
        m.setMonitorKind(str(body.get("monitorKind")));
        m.setMonitorStatus("submitted");
        m.setDeviceId(device.getId());
        m.setDeviceCode(device.getDeviceCode());
        m.setDeviceName(device.getDeviceName());
        m.setDeviceNo(device.getDeviceNo());
        m.setLoadId(str(body.get("loadId")));
        if (m.getLoadId() != null) {
            CssdSterLoad load = loadMapper.selectById(m.getLoadId());
            if (load != null) {
                m.setLoadNo(load.getLoadNo());
            }
        }
        m.setMonitorDate(body.get("monitorDate") != null
                ? LocalDate.parse(String.valueOf(body.get("monitorDate"))) : LocalDate.now());
        m.setResultCode(str(body.getOrDefault("resultCode", "pass")));
        m.setResultAt(support.now());
        m.setOperatorId(user.getStaffId());
        m.setOperatorNo(user.getStaffNo());
        m.setOperatorName(user.getStaffName());
        m.setReportUrl(str(body.get("reportUrl")));
        monitorMapper.insert(m);
        if (m.getLoadId() != null) {
            CssdSterLoad load = loadMapper.selectById(m.getLoadId());
            if (load != null) {
                switch (m.getMonitorKind()) {
                    case "pcd" -> load.setPcdResult(m.getResultCode());
                    case "bio" -> load.setBioResult(m.getResultCode());
                    case "chem" -> load.setChemResult(m.getResultCode());
                    case "bd" -> load.setBdResult(m.getResultCode());
                    default -> {
                    }
                }
                loadMapper.updateById(load);
                if ("fail".equals(m.getResultCode()) && ("chem".equals(m.getMonitorKind()) || "pcd".equals(m.getMonitorKind()))) {
                    failWholeLoad(load, false);
                }
                if ("fail".equals(m.getResultCode()) && "bio".equals(m.getMonitorKind())) {
                    failWholeLoad(load, true);
                    createBioRecall(load, m);
                }
            }
        }
        return m;
    }

    @Transactional
    public CssdSterLoad release(String loadId) {
        LoginUser user = support.user();
        CssdSterLoad load = loadMapper.selectById(loadId);
        if (load == null || !"finished".equals(load.getLoadStatus())) {
            throw new BizException("须灭菌结束后放行");
        }
        if (clinicalLoad(load) && "high".equals(load.getSterTempClass()) && !bdPassToday(load.getDeviceId())) {
            throw new BizException("高温当日BD未合格，不得放行");
        }
        if (!"pass".equals(load.getPcdResult())) {
            throw new BizException("本锅PCD未合格，不得放行");
        }
        if (!"pass".equals(load.getChemResult())) {
            throw new BizException("本锅化学监测未合格，不得放行");
        }
        if (configs.waitBio() && !"pass".equals(load.getBioResult())) {
            throw new BizException("生物监测未合格，不得放行");
        }
        load.setReleaseStatus("released");
        load.setReleasedAt(support.now());
        loadMapper.updateById(load);
        for (CssdSterLoadItem it : items(loadId)) {
            CssdPackInstance inst = instanceMapper.selectById(it.getInstanceId());
            if ("ster_failed".equals(inst.getStatus())) {
                continue;
            }
            if (isTest(inst.getPackCategory())) {
                continue;
            }
            inst.setStatus("released");
            inst.setReleasedAt(support.now());
            inst.setReleaseStaffId(user.getStaffId());
            inst.setReleaseStaffNo(user.getStaffNo());
            inst.setReleaseStaffName(user.getStaffName());
            instanceMapper.updateById(inst);
        }
        return load;
    }

    @Transactional
    public CssdPackInstance store(String barcode) {
        LoginUser user = support.user();
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null) {
            throw new BizException("包不存在");
        }
        support.requireStatus(inst, "released");
        if (!Integer.valueOf(1).equals(inst.getNeedStore())) {
            throw new BizException("该包不需入库");
        }
        inst.setStatus("stored");
        inst.setStoredAt(support.now());
        inst.setStoreStaffId(user.getStaffId());
        inst.setStoreStaffNo(user.getStaffNo());
        inst.setStoreStaffName(user.getStaffName());
        instanceMapper.updateById(inst);
        return inst;
    }

    public CssdRecall createBioRecall(CssdSterLoad load, CssdMonitor monitor) {
        LoginUser user = support.user();
        LocalDate since = lastBioPassDate(load.getDeviceId(), monitor.getMonitorDate());
        CssdRecall recall = new CssdRecall();
        recall.setId(support.id());
        recall.setRecallNo(support.billNo("ZH"));
        recall.setRecallStatus("submitted");
        recall.setRecallReason("bio_fail");
        recall.setDeviceId(load.getDeviceId());
        recall.setDeviceCode(load.getDeviceCode());
        recall.setDeviceName(load.getDeviceName());
        recall.setDeviceNo(load.getDeviceNo());
        recall.setLoadId(load.getId());
        recall.setLoadNo(load.getLoadNo());
        recall.setMonitorId(monitor.getId());
        recall.setSinceTime(since.atStartOfDay().atOffset(support.now().getOffset()));
        recall.setOperatorId(user.getStaffId());
        recall.setOperatorNo(user.getStaffNo());
        recall.setOperatorName(user.getStaffName());
        recall.setRecalledAt(support.now());
        recallMapper.insert(recall);
        List<CssdPackInstance> scope = instanceMapper.selectList(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getDeviceId, load.getDeviceId())
                .ge(CssdPackInstance::getSterilizeDate, since)
                .in(CssdPackInstance::getStatus, List.of("released", "stored", "distributed", "received",
                        "pre_checked", "used")));
        int line = 1;
        for (CssdPackInstance inst : scope) {
            CssdRecallItem ri = new CssdRecallItem();
            ri.setId(support.id());
            ri.setRecallId(recall.getId());
            ri.setRecallNo(recall.getRecallNo());
            ri.setLineNo(line++);
            ri.setInstanceId(inst.getId());
            ri.setBarcode(inst.getBarcode());
            ri.setTemplateId(inst.getTemplateId());
            ri.setTemplateCode(inst.getTemplateCode());
            ri.setTemplateName(inst.getTemplateName());
            ri.setLocDeptId(inst.getLocDeptId());
            ri.setLocDeptCode(inst.getLocDeptCode());
            ri.setLocDeptName(inst.getLocDeptName());
            ri.setLocCampusId(inst.getLocCampusId());
            ri.setLocCampusCode(inst.getLocCampusCode());
            ri.setLocCampusName(inst.getLocCampusName());
            boolean used = "used".equals(inst.getStatus()) || "pre_checked".equals(inst.getStatus());
            ri.setUsedFlag(used ? 1 : 0);
            ri.setPatientNo(inst.getPatientNo());
            ri.setPatientName(inst.getPatientName());
            ri.setBedNo(inst.getBedNo());
            ri.setSurgeonId(inst.getSurgeonId());
            ri.setSurgeonNo(inst.getSurgeonNo());
            ri.setSurgeonName(inst.getSurgeonName());
            recallItemMapper.insert(ri);
            inst.setStatus("recalled");
            instanceMapper.updateById(inst);
        }
        return recall;
    }

    @Transactional
    public CssdPackInstance confirmReturn(String barcode) {
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null || !"recalled".equals(inst.getStatus())) {
            throw new BizException("非召回包");
        }
        return inst;
    }

    private void failWholeLoad(CssdSterLoad load, boolean bio) {
        load.setReleaseStatus("rejected");
        loadMapper.updateById(load);
        for (CssdSterLoadItem it : items(load.getId())) {
            CssdPackInstance inst = instanceMapper.selectById(it.getInstanceId());
            if (!isTest(inst.getPackCategory())) {
                inst.setStatus("ster_failed");
                instanceMapper.updateById(inst);
            }
        }
    }

    private boolean clinicalLoad(CssdSterLoad load) {
        return items(load.getId()).stream().anyMatch(it -> !isTest(it.getPackCategory()));
    }

    private boolean isTest(String cat) {
        return cat != null && cat.startsWith("test_");
    }

    private boolean bdPassToday(String deviceId) {
        return monitorMapper.selectCount(new LambdaQueryWrapper<CssdMonitor>()
                .eq(CssdMonitor::getDeviceId, deviceId)
                .eq(CssdMonitor::getMonitorKind, "bd")
                .eq(CssdMonitor::getResultCode, "pass")
                .eq(CssdMonitor::getMonitorDate, LocalDate.now())) > 0;
    }

    private boolean deviceBlocked(String deviceId) {
        CssdMonitor lastBio = monitorMapper.selectOne(new LambdaQueryWrapper<CssdMonitor>()
                .eq(CssdMonitor::getDeviceId, deviceId)
                .eq(CssdMonitor::getMonitorKind, "bio")
                .in(CssdMonitor::getResultCode, List.of("pass", "fail"))
                .orderByDesc(CssdMonitor::getResultAt)
                .last("limit 1"));
        return lastBio != null && "fail".equals(lastBio.getResultCode());
    }

    private LocalDate lastBioPassDate(String deviceId, LocalDate failDate) {
        CssdMonitor last = monitorMapper.selectOne(new LambdaQueryWrapper<CssdMonitor>()
                .eq(CssdMonitor::getDeviceId, deviceId)
                .eq(CssdMonitor::getMonitorKind, "bio")
                .eq(CssdMonitor::getResultCode, "pass")
                .lt(CssdMonitor::getMonitorDate, failDate)
                .orderByDesc(CssdMonitor::getMonitorDate)
                .last("limit 1"));
        return last == null ? LocalDate.of(1970, 1, 1) : last.getMonitorDate();
    }

    private List<CssdSterLoadItem> items(String loadId) {
        return loadItemMapper.selectList(new LambdaQueryWrapper<CssdSterLoadItem>().eq(CssdSterLoadItem::getLoadId, loadId));
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
