package com.cssd.common;

import lombok.Data;

@Data
public class PageQuery {
    private long page = 1;
    private long size = 20;
    private String keyword;
    private String status;
    private String deptId;
    private String templateId;
    private String deviceId;
    private String from;
    private String to;
}
