package com.cssd.infra.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "cssd_print_log", schema = "cssd")
public class CssdPrintLog extends BaseEntity {
    private String entityType;
    private String entityId;
    private String instanceId;
    private String barcode;
    private String labelPhase;
    private String templateCode;
    private String bizType;
    private String bizId;
    private String bizNo;
    private String bizItemId;
    private String snapshotCode;
    private String snapshotName;
    private String printerId;
    private String printerNo;
    private String printerName;
    private OffsetDateTime printedAt;
}