package com.cssd.common;

import lombok.Data;

@Data
public class PageQuery {
    private long page = 1;
    private long size = 20;
    private String keyword;
    /** org=组织机构；campus=院区；空=全部 */
    private String kind;
    private String status;
    private String deptId;
    /** 人员岗位编码（staff_role） */
    private String staffRole;
    /** 院区/科室所属院区 id */
    private String campusId;
    /** 组织机构 id（科室列表按院区归属过滤） */
    private String orgId;
    private String templateId;
    private String deviceId;
    private String from;
    private String to;
}
