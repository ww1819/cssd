-- 科室：上级科室、位置、责任人、电话
ALTER TABLE cssd.cssd_dept
    ADD COLUMN IF NOT EXISTS parent_id       varchar(36)  NULL,
    ADD COLUMN IF NOT EXISTS location        varchar(200) NULL,
    ADD COLUMN IF NOT EXISTS contact_name    varchar(64)  NULL,
    ADD COLUMN IF NOT EXISTS contact_phone   varchar(32)  NULL,
    ADD COLUMN IF NOT EXISTS dept_phone      varchar(32)  NULL;

COMMENT ON COLUMN cssd.cssd_dept.parent_id IS '上级科室ID';
COMMENT ON COLUMN cssd.cssd_dept.location IS '位置';
COMMENT ON COLUMN cssd.cssd_dept.contact_name IS '责任人';
COMMENT ON COLUMN cssd.cssd_dept.contact_phone IS '责任人电话';
COMMENT ON COLUMN cssd.cssd_dept.dept_phone IS '科室电话';

CREATE INDEX IF NOT EXISTS idx_cssd_dept_parent_id ON cssd.cssd_dept (parent_id);
