-- 人员授权：科室权限、仓库权限（JSON 数组，存 id）
ALTER TABLE cssd.cssd_auth_account
    ADD COLUMN IF NOT EXISTS dept_auth      text NULL,
    ADD COLUMN IF NOT EXISTS warehouse_auth text NULL;

COMMENT ON COLUMN cssd.cssd_auth_account.dept_auth IS '科室授权 JSON 数组（cssd_dept.id）';
COMMENT ON COLUMN cssd.cssd_auth_account.warehouse_auth IS '仓库授权 JSON 数组（cssd_warehouse.id）';
