package com.cssd.flow;

import com.cssd.infra.entity.CssdApplyBill;
import com.cssd.infra.entity.CssdDistBill;
import com.cssd.infra.entity.CssdMonitor;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdRecycleBill;
import com.cssd.infra.entity.CssdSterLoad;
import com.cssd.infra.entity.CssdUseCheck;
import com.cssd.infra.entity.CssdWashBatch;
import com.cssd.infra.mapper.CssdRecycleItemMapper;
import com.cssd.security.LoginUser;
import com.cssd.infra.entity.CssdRecycleItem;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DemoClosedLoopService {
    private static final String DEPT_OR = "10000000-0000-7000-8000-000000000012";
    private static final String TPL = "60000000-0000-7000-8000-000000000001";
    private static final String BASKET = "80000000-0000-7000-8000-000000000001";
    private static final String WASH = "70000000-0000-7000-8000-000000000001";
    private static final String WASH_P = "71000000-0000-7000-8000-000000000001";
    private static final String STER = "70000000-0000-7000-8000-000000000002";
    private static final String STER_P = "71000000-0000-7000-8000-000000000002";
    private static final String SURGEON = "20000000-0000-7000-8000-000000000008";

    private final RecycleWashPackService recycleWashPackService;
    private final SterMonitorService sterMonitorService;
    private final DistUseService distUseService;
    private final PrintService printService;
    private final CssdRecycleItemMapper recycleItemMapper;
    private final TraceQueryService traceQueryService;

    public DemoClosedLoopService(RecycleWashPackService recycleWashPackService, SterMonitorService sterMonitorService,
                                 DistUseService distUseService, PrintService printService,
                                 CssdRecycleItemMapper recycleItemMapper, TraceQueryService traceQueryService) {
        this.recycleWashPackService = recycleWashPackService;
        this.sterMonitorService = sterMonitorService;
        this.distUseService = distUseService;
        this.printService = printService;
        this.recycleItemMapper = recycleItemMapper;
        this.traceQueryService = traceQueryService;
    }

    @Transactional
    public Map<String, Object> run() {
        as("OP001", "李配包", "operator", "20000000-0000-7000-8000-000000000003");
        Map<String, Object> recBody = new HashMap<>();
        recBody.put("deptId", DEPT_OR);
        recBody.put("items", List.of(Map.of(
                "templateId", TPL, "qty", 1, "basketId", BASKET, "urgentFlag", 0)));
        CssdRecycleBill rec = recycleWashPackService.submitRecycle(recBody);
        CssdRecycleItem ri = recycleItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CssdRecycleItem>()
                        .eq(CssdRecycleItem::getBillId, rec.getId())).get(0);

        Map<String, Object> washBody = new HashMap<>();
        washBody.put("washMode", "machine");
        washBody.put("deviceId", WASH);
        washBody.put("programId", WASH_P);
        washBody.put("recycleItemIds", List.of(ri.getId()));
        CssdWashBatch wash = recycleWashPackService.saveWash(washBody);
        recycleWashPackService.startWash(wash.getId());
        recycleWashPackService.finishWash(wash.getId(), Map.of());
        as("QC001", "赵质检", "qc", "20000000-0000-7000-8000-000000000005");
        recycleWashPackService.checkWash(wash.getId(), Map.of("checkResult", "pass"));

        as("OP001", "李配包", "operator", "20000000-0000-7000-8000-000000000003");
        CssdPackInstance inst = recycleWashPackService.pack(Map.of("templateId", TPL, "recycleItemId", ri.getId(), "qty", 1));
        as("OP002", "张核包", "operator", "20000000-0000-7000-8000-000000000004");
        recycleWashPackService.checkPack(inst.getId(), Map.of("pass", 1));
        printService.printInstance(inst.getBarcode(), "process", false);

        as("QC001", "赵质检", "qc", "20000000-0000-7000-8000-000000000005");
        sterMonitorService.submitMonitor(Map.of("deviceId", STER, "monitorKind", "bd", "resultCode", "pass"));
        CssdSterLoad load = sterMonitorService.createLoad(Map.of(
                "deviceId", STER, "programId" , STER_P, "mainLoadDesc", "口护包演示锅"));
        sterMonitorService.addItem(load.getId(), inst.getBarcode());
        sterMonitorService.start(load.getId());
        sterMonitorService.finish(load.getId(), Map.of("paramsJson", Map.of("temp", 134)));
        sterMonitorService.submitMonitor(Map.of("deviceId", STER, "loadId", load.getId(), "monitorKind", "pcd", "resultCode", "pass"));
        sterMonitorService.submitMonitor(Map.of("deviceId", STER, "loadId", load.getId(), "monitorKind", "chem", "resultCode", "pass"));
        sterMonitorService.submitMonitor(Map.of("deviceId", STER, "loadId", load.getId(), "monitorKind", "bio", "resultCode", "pass"));
        sterMonitorService.release(load.getId());
        printService.printInstance(inst.getBarcode(), "legal", true);
        sterMonitorService.store(inst.getBarcode());

        as("CL001", "孙护士", "clinical", "20000000-0000-7000-8000-000000000007");
        CssdApplyBill apply = distUseService.submitApply(Map.of(
                "deptId", DEPT_OR, "items", List.of(Map.of("templateId", TPL, "qty", 1))));
        as("DL001", "钱下送", "delivery", "20000000-0000-7000-8000-000000000006");
        CssdDistBill dist = distUseService.issue(Map.of(
                "applyBillId", apply.getId(), "distMode", "direct", "autoReceive", true,
                "barcodes", List.of(inst.getBarcode())));
        as("CL001", "孙护士", "clinical", "20000000-0000-7000-8000-000000000007");
        CssdUseCheck pre = distUseService.preCheck(Map.of(
                "barcodes", List.of(inst.getBarcode()),
                "patientNo", "ZY10001", "patientName", "演示患者甲", "bedNo", "12-03",
                "surgeryName", "阑尾切除术", "surgeonId", SURGEON,
                "hisPatientId", "P10001", "hisEncounterId", "E20260922001",
                "idempotencyKey", inst.getBarcode() + "|E20260922001|use"));
        distUseService.postCheck(Map.of("barcodes", List.of(inst.getBarcode())));
        as("OP001", "李配包", "operator", "20000000-0000-7000-8000-000000000003");
        recycleWashPackService.submitRecycle(Map.of(
                "deptId", DEPT_OR,
                "items", List.of(Map.of("barcode", inst.getBarcode(), "qty", 1, "basketId", BASKET))));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("barcode", inst.getBarcode());
        out.put("recycleNo", rec.getBillNo());
        out.put("washNo", wash.getBatchNo());
        out.put("loadNo", load.getLoadNo());
        out.put("applyNo", apply.getBillNo());
        out.put("distNo", dist.getBillNo());
        out.put("preCheckNo", pre.getCheckNo());
        out.put("trace", traceQueryService.byCode(inst.getBarcode()));
        return out;
    }

    private void as(String no, String name, String role, String id) {
        LoginUser user = new LoginUser(id, no, name, role,
                "20000000-0000-7000-8000-000000000011".equals(id) ? null : "10000000-0000-7000-8000-000000000011");
        if ("CL001".equals(no) || "SG001".equals(no)) {
            user.setDeptId(DEPT_OR);
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of()));
    }
}
