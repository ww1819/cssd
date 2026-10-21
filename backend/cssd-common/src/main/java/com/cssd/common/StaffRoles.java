package com.cssd.common;

/**
 * SYS-F-009 岗位码。消毒员映射为 operator；质检员为 qc。
 */
public final class StaffRoles {
    public static final String ADMIN = "admin";
    public static final String NURSE_LEAD = "nurse_lead";
    public static final String OPERATOR = "operator";
    public static final String QC = "qc";
    public static final String DELIVERY = "delivery";
    public static final String CLINICAL = "clinical";
    public static final String SURGEON = "surgeon";

    private StaffRoles() {
    }

    public static String springRole(String staffRole) {
        return "ROLE_" + (staffRole == null ? OPERATOR : staffRole).toUpperCase();
    }
}
