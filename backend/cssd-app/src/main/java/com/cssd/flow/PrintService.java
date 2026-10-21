package com.cssd.flow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.BizException;
import com.cssd.common.IdUtils;
import com.cssd.infra.entity.CssdPackInstance;
import com.cssd.infra.entity.CssdPrintLog;
import com.cssd.infra.entity.CssdPrintTemplate;
import com.cssd.infra.entity.CssdRfidBindLog;
import com.cssd.infra.mapper.CssdPackInstanceMapper;
import com.cssd.infra.mapper.CssdPrintLogMapper;
import com.cssd.infra.mapper.CssdPrintTemplateMapper;
import com.cssd.infra.mapper.CssdRfidBindLogMapper;
import com.cssd.security.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PrintService {
    private final CssdPrintTemplateMapper templateMapper;
    private final CssdPrintLogMapper logMapper;
    private final CssdPackInstanceMapper instanceMapper;
    private final CssdRfidBindLogMapper rfidMapper;
    private final boolean rfidMock;

    public PrintService(CssdPrintTemplateMapper templateMapper, CssdPrintLogMapper logMapper,
                        CssdPackInstanceMapper instanceMapper, CssdRfidBindLogMapper rfidMapper,
                        @Value("${cssd.rfid.mock:true}") boolean rfidMock) {
        this.templateMapper = templateMapper;
        this.logMapper = logMapper;
        this.instanceMapper = instanceMapper;
        this.rfidMapper = rfidMapper;
        this.rfidMock = rfidMock;
    }

    @Transactional
    public Map<String, Object> printInstance(String barcode, String phase, boolean writeRfid) {
        LoginUser user = com.cssd.security.SecurityUtils.requireUser();
        CssdPackInstance inst = instanceMapper.selectOne(new LambdaQueryWrapper<CssdPackInstance>()
                .eq(CssdPackInstance::getBarcode, barcode));
        if (inst == null) {
            throw new BizException("包不存在");
        }
        CssdPrintTemplate tpl = templateMapper.selectOne(new LambdaQueryWrapper<CssdPrintTemplate>()
                .eq(CssdPrintTemplate::getLabelPhase, phase).eq(CssdPrintTemplate::getIsActive, 1)
                .last("limit 1"));
        if (tpl == null) {
            throw new BizException("无可用打印模板: " + phase);
        }
        if ("legal".equals(phase)) {
            validateLegal(tpl, inst);
            inst.setLegalPrintedAt(java.time.OffsetDateTime.now());
        }
        if ("process".equals(phase)) {
            inst.setProcessPrintedAt(java.time.OffsetDateTime.now());
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("barcode", inst.getBarcode());
        payload.put("packName", inst.getTemplateName());
        payload.put("packer", inst.getPackerName());
        payload.put("checker", inst.getCheckerName());
        payload.put("deviceNo", inst.getDeviceNo());
        payload.put("loadNo", inst.getSterLoadNo());
        payload.put("sterilizeDate", inst.getSterilizeDate());
        payload.put("expireDate", inst.getExpireDate());
        payload.put("paperWidthMm", tpl.getPaperWidthMm());
        payload.put("paperHeightMm", tpl.getPaperHeightMm());
        boolean rfidOk = false;
        String rfidUid = null;
        if (writeRfid) {
            if (rfidMock) {
                rfidUid = "RFID-" + inst.getBarcode();
                rfidOk = true;
                inst.setRfidUid(rfidUid);
                inst.setRfidBoundAt(java.time.OffsetDateTime.now());
                inst.setRfidBinderId(user.getStaffId());
                inst.setRfidBinderNo(user.getStaffNo());
                inst.setRfidBinderName(user.getStaffName());
                CssdRfidBindLog bind = new CssdRfidBindLog();
                bind.setId(IdUtils.uuid7());
                bind.setInstanceId(inst.getId());
                bind.setBarcode(inst.getBarcode());
                bind.setTemplateCode(inst.getTemplateCode());
                bind.setTemplateName(inst.getTemplateName());
                bind.setRfidUid(rfidUid);
                bind.setBindAction("print_write");
                bind.setOperatorId(user.getStaffId());
                bind.setOperatorNo(user.getStaffNo());
                bind.setOperatorName(user.getStaffName());
                bind.setBoundAt(java.time.OffsetDateTime.now());
                rfidMapper.insert(bind);
            } else {
                payload.put("rfidError", "本机RFID代理不可用，二维码仍打印成功");
            }
        }
        instanceMapper.updateById(inst);
        CssdPrintLog log = new CssdPrintLog();
        log.setId(IdUtils.uuid7());
        log.setEntityType("pack_instance");
        log.setEntityId(inst.getId());
        log.setInstanceId(inst.getId());
        log.setBarcode(inst.getBarcode());
        log.setLabelPhase(phase);
        log.setTemplateCode(tpl.getTemplateCode());
        log.setSnapshotCode(inst.getTemplateCode());
        log.setSnapshotName(inst.getTemplateName());
        log.setPrinterId(user.getStaffId());
        log.setPrinterNo(user.getStaffNo());
        log.setPrinterName(user.getStaffName());
        log.setPrintedAt(java.time.OffsetDateTime.now());
        logMapper.insert(log);
        payload.put("rfidOk", rfidOk);
        payload.put("rfidUid", rfidUid);
        payload.put("printLogId", log.getId());
        return payload;
    }

    private void validateLegal(CssdPrintTemplate tpl, CssdPackInstance inst) {
        String layout = tpl.getLayoutJson() == null ? "" : tpl.getLayoutJson();
        if (layout.contains("requiredFields")) {
            if (inst.getCheckerName() == null) {
                throw new BizException("法规签缺检查打包者");
            }
            if (inst.getDeviceNo() == null || inst.getSterLoadNo() == null
                    || inst.getSterilizeDate() == null || inst.getExpireDate() == null) {
                throw new BizException("法规签须在灭菌完成锁定失效日之后打印");
            }
        }
        if (inst.getExpireDate() == null) {
            throw new BizException("失效日未锁定，禁止打法规签");
        }
    }
}
