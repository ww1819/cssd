package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cssd.common.ApiResult;
import com.cssd.common.PageQuery;
import com.cssd.eq.DeviceProtocolRegistry;
import com.cssd.his.HisAdapter;
import com.cssd.infra.entity.CssdApplyBill;
import com.cssd.infra.entity.CssdDistBill;
import com.cssd.infra.entity.CssdMonitor;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdRecall;
import com.cssd.infra.entity.CssdRecycleBill;
import com.cssd.infra.entity.CssdRecycleItem;
import com.cssd.infra.entity.CssdSterLoad;
import com.cssd.infra.entity.CssdSterLoadItem;
import com.cssd.infra.entity.CssdWashBatch;
import com.cssd.infra.entity.CssdWashItem;
import com.cssd.infra.mapper.CssdApplyBillMapper;
import com.cssd.infra.mapper.CssdDistBillMapper;
import com.cssd.infra.mapper.CssdMonitorMapper;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdRecallItemMapper;
import com.cssd.infra.mapper.CssdRecallMapper;
import com.cssd.infra.mapper.CssdRecycleBillMapper;
import com.cssd.infra.mapper.CssdRecycleItemMapper;
import com.cssd.infra.mapper.CssdSterLoadItemMapper;
import com.cssd.infra.mapper.CssdSterLoadMapper;
import com.cssd.infra.mapper.CssdWashBatchMapper;
import com.cssd.infra.mapper.CssdWashItemMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FlowController {
    private final RecycleWashPackService recycleWashPackService;
    private final SterMonitorService sterMonitorService;
    private final DistUseService distUseService;
    private final BypassService bypassService;
    private final PrintService printService;
    private final TraceQueryService traceQueryService;
    private final DemoClosedLoopService demoClosedLoopService;
    private final HisAdapter hisAdapter;
    private final DeviceProtocolRegistry protocolRegistry;
    private final CssdRecycleBillMapper recycleBillMapper;
    private final CssdRecycleItemMapper recycleItemMapper;
    private final CssdWashBatchMapper washBatchMapper;
    private final CssdWashItemMapper washItemMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdSterLoadMapper loadMapper;
    private final CssdSterLoadItemMapper loadItemMapper;
    private final CssdMonitorMapper monitorMapper;
    private final CssdApplyBillMapper applyBillMapper;
    private final CssdDistBillMapper distBillMapper;
    private final CssdRecallMapper recallMapper;
    private final CssdRecallItemMapper recallItemMapper;

    public FlowController(RecycleWashPackService recycleWashPackService, SterMonitorService sterMonitorService,
                          DistUseService distUseService, BypassService bypassService, PrintService printService,
                          TraceQueryService traceQueryService, DemoClosedLoopService demoClosedLoopService,
                          HisAdapter hisAdapter, DeviceProtocolRegistry protocolRegistry,
                          CssdRecycleBillMapper recycleBillMapper, CssdRecycleItemMapper recycleItemMapper,
                          CssdWashBatchMapper washBatchMapper, CssdWashItemMapper washItemMapper,
                          CssdPackInstanceMapper instanceMapper, CssdSterLoadMapper loadMapper,
                          CssdSterLoadItemMapper loadItemMapper, CssdMonitorMapper monitorMapper,
                          CssdApplyBillMapper applyBillMapper, CssdDistBillMapper distBillMapper,
                          CssdRecallMapper recallMapper, CssdRecallItemMapper recallItemMapper) {
        this.recycleWashPackService = recycleWashPackService;
        this.sterMonitorService = sterMonitorService;
        this.distUseService = distUseService;
        this.bypassService = bypassService;
        this.printService = printService;
        this.traceQueryService = traceQueryService;
        this.demoClosedLoopService = demoClosedLoopService;
        this.hisAdapter = hisAdapter;
        this.protocolRegistry = protocolRegistry;
        this.recycleBillMapper = recycleBillMapper;
        this.recycleItemMapper = recycleItemMapper;
        this.washBatchMapper = washBatchMapper;
        this.washItemMapper = washItemMapper;
        this.instanceMapper = instanceMapper;
        this.loadMapper = loadMapper;
        this.loadItemMapper = loadItemMapper;
        this.monitorMapper = monitorMapper;
        this.applyBillMapper = applyBillMapper;
        this.distBillMapper = distBillMapper;
        this.recallMapper = recallMapper;
        this.recallItemMapper = recallItemMapper;
    }

    @PostMapping("/recycle/submit")
    public ApiResult<?> recycle(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.submitRecycle(body));
    }

    @GetMapping("/recycle")
    public ApiResult<?> recycleList(PageQuery q) {
        return ApiResult.ok(recycleBillMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdRecycleBill>().orderByDesc("created_at")));
    }

    @GetMapping("/recycle/{id}/items")
    public ApiResult<?> recycleItems(@PathVariable String id) {
        return ApiResult.ok(recycleItemMapper.selectList(new QueryWrapper<CssdRecycleItem>().eq("bill_id", id)));
    }

    @PostMapping("/wash")
    public ApiResult<?> wash(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.saveWash(body));
    }

    @PostMapping("/wash/{id}/start")
    public ApiResult<?> washStart(@PathVariable String id) {
        return ApiResult.ok(recycleWashPackService.startWash(id));
    }

    @PostMapping("/wash/{id}/finish")
    public ApiResult<?> washFinish(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.finishWash(id, body));
    }

    @PostMapping("/wash/{id}/check")
    public ApiResult<?> washCheck(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.checkWash(id, body));
    }

    @PostMapping("/wash/{id}/rewash")
    public ApiResult<?> rewash(@PathVariable String id) {
        return ApiResult.ok(recycleWashPackService.rewash(id));
    }

    @GetMapping("/wash")
    public ApiResult<?> washList(PageQuery q) {
        return ApiResult.ok(washBatchMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdWashBatch>().orderByDesc("created_at")));
    }

    @GetMapping("/wash/{id}/items")
    public ApiResult<?> washItems(@PathVariable String id) {
        return ApiResult.ok(washItemMapper.selectList(new QueryWrapper<CssdWashItem>().eq("batch_id", id)));
    }

    @PostMapping("/pack")
    public ApiResult<?> pack(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.pack(body));
    }

    @PostMapping("/pack/{id}/check")
    public ApiResult<?> packCheck(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResult.ok(recycleWashPackService.checkPack(id, body));
    }

    @PostMapping("/pack/{id}/void")
    public ApiResult<?> packVoid(@PathVariable String id) {
        return ApiResult.ok(recycleWashPackService.voidPack(id));
    }

    @GetMapping("/instances")
    public ApiResult<?> instances(PageQuery q) {
        QueryWrapper<CssdPackInstance> w = new QueryWrapper<CssdPackInstance>().orderByDesc("created_at");
        if (q.getStatus() != null && !q.getStatus().isBlank()) {
            w.eq("status", q.getStatus());
        }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            w.and(x -> x.like("barcode", q.getKeyword()).or().like("template_name", q.getKeyword())
                    .or().like("template_code", q.getKeyword()));
        }
        return ApiResult.ok(instanceMapper.selectPage(new Page<>(q.getPage(), q.getSize()), w));
    }

    @PostMapping("/ster")
    public ApiResult<?> ster(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(sterMonitorService.createLoad(body));
    }

    @PostMapping("/ster/{id}/items")
    public ApiResult<?> sterAdd(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ApiResult.ok(sterMonitorService.addItem(id, body.get("barcode")));
    }

    @PostMapping("/ster/{id}/items/{itemId}/remove")
    public ApiResult<?> sterRemove(@PathVariable String id, @PathVariable String itemId) {
        sterMonitorService.removeItem(id, itemId);
        return ApiResult.ok();
    }

    @PostMapping("/ster/{id}/start")
    public ApiResult<?> sterStart(@PathVariable String id) {
        return ApiResult.ok(sterMonitorService.start(id));
    }

    @PostMapping("/ster/{id}/finish")
    public ApiResult<?> sterFinish(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResult.ok(sterMonitorService.finish(id, body));
    }

    @PostMapping("/ster/{id}/release")
    public ApiResult<?> sterRelease(@PathVariable String id) {
        return ApiResult.ok(sterMonitorService.release(id));
    }

    @PostMapping("/ster/wet-fail")
    public ApiResult<?> wetFail(@RequestBody Map<String, String> body) {
        return ApiResult.ok(sterMonitorService.wetFail(body.get("instanceId"), body.get("reason")));
    }

    @GetMapping("/ster")
    public ApiResult<?> sterList(PageQuery q) {
        return ApiResult.ok(loadMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdSterLoad>().orderByDesc("created_at")));
    }

    @GetMapping("/ster/{id}/items")
    public ApiResult<?> sterItems(@PathVariable String id) {
        return ApiResult.ok(loadItemMapper.selectList(new QueryWrapper<CssdSterLoadItem>().eq("load_id", id)));
    }

    @PostMapping("/monitor")
    public ApiResult<?> monitor(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(sterMonitorService.submitMonitor(body));
    }

    @GetMapping("/monitor")
    public ApiResult<?> monitorList(PageQuery q) {
        return ApiResult.ok(monitorMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdMonitor>().orderByDesc("created_at")));
    }

    @PostMapping("/store")
    public ApiResult<?> store(@RequestBody Map<String, String> body) {
        return ApiResult.ok(sterMonitorService.store(body.get("barcode")));
    }

    @PostMapping("/apply")
    public ApiResult<?> apply(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(distUseService.submitApply(body));
    }

    @GetMapping("/apply")
    public ApiResult<?> applyList(PageQuery q) {
        return ApiResult.ok(applyBillMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdApplyBill>().orderByDesc("created_at")));
    }

    @PostMapping("/dist")
    public ApiResult<?> dist(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(distUseService.issue(body));
    }

    @PostMapping("/dist/{id}/receive")
    public ApiResult<?> receive(@PathVariable String id) {
        return ApiResult.ok(distUseService.receive(id));
    }

    @PostMapping("/dist/return")
    public ApiResult<?> ret(@RequestBody Map<String, String> body) {
        return ApiResult.ok(distUseService.returnUnused(body.get("barcode")));
    }

    @GetMapping("/dist")
    public ApiResult<?> distList(PageQuery q) {
        return ApiResult.ok(distBillMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdDistBill>().orderByDesc("created_at")));
    }

    @PostMapping("/use/pre")
    public ApiResult<?> pre(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(distUseService.preCheck(body));
    }

    @PostMapping("/use/post")
    public ApiResult<?> post(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(distUseService.postCheck(body));
    }

    @PostMapping("/use/correct-patient")
    public ApiResult<?> correct(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(distUseService.correctPatient(String.valueOf(body.get("instanceId")), body));
    }

    @PostMapping("/loan")
    public ApiResult<?> loan(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(bypassService.loan(body));
    }

    @PostMapping("/external")
    public ApiResult<?> external(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(bypassService.externalRegister(body));
    }

    @PostMapping("/external/vendor-take")
    public ApiResult<?> vendorTake(@RequestBody Map<String, String> body) {
        return ApiResult.ok(bypassService.vendorTake(body.get("barcode")));
    }

    @PostMapping("/once/io")
    public ApiResult<?> onceIo(@RequestBody Map<String, Object> body) {
        return ApiResult.ok(bypassService.onceIo(body));
    }

    @PostMapping("/once/lots/{id}/block")
    public ApiResult<?> blockLot(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ApiResult.ok(bypassService.blockLot(id, body.get("reason")));
    }

    @PostMapping("/print")
    public ApiResult<?> print(@RequestBody Map<String, Object> body) {
        String barcode = String.valueOf(body.get("barcode"));
        String phase = String.valueOf(body.getOrDefault("phase", "process"));
        boolean rfid = Boolean.TRUE.equals(body.get("writeRfid"));
        return ApiResult.ok(printService.printInstance(barcode, phase, rfid));
    }

    @GetMapping("/trace")
    public ApiResult<?> trace(@RequestParam String code) {
        return ApiResult.ok(traceQueryService.byCode(code));
    }

    @GetMapping("/alerts")
    public ApiResult<?> alerts() {
        return ApiResult.ok(traceQueryService.alerts());
    }

    @GetMapping("/recall")
    public ApiResult<?> recalls(PageQuery q) {
        return ApiResult.ok(recallMapper.selectPage(new Page<>(q.getPage(), q.getSize()),
                new QueryWrapper<CssdRecall>().orderByDesc("created_at")));
    }

    @GetMapping("/recall/{id}/items")
    public ApiResult<?> recallItems(@PathVariable String id) {
        return ApiResult.ok(recallItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.cssd.infra.entity.CssdRecallItem>()
                        .eq("recall_id", id)));
    }

    @PostMapping("/recall/return")
    public ApiResult<?> recallReturn(@RequestBody Map<String, String> body) {
        return ApiResult.ok(sterMonitorService.confirmReturn(body.get("barcode")));
    }

    @GetMapping("/his/patients")
    public ApiResult<?> hisPatients(@RequestParam(required = false) String keyword) {
        return ApiResult.ok(hisAdapter.searchPatients(keyword));
    }

    @GetMapping("/eq/protocols")
    public ApiResult<?> protocols() {
        return ApiResult.ok(protocolRegistry.list());
    }

    @PostMapping("/demo/run-closed-loop")
    public ApiResult<?> demo() {
        return ApiResult.ok(demoClosedLoopService.run());
    }
}
