-- 人员档案：联系信息 / 用户类型
ALTER TABLE cssd.cssd_staff
    ADD COLUMN IF NOT EXISTS contact_name  varchar(64)  NULL,
    ADD COLUMN IF NOT EXISTS contact_phone varchar(32)  NULL,
    ADD COLUMN IF NOT EXISTS qq            varchar(32)  NULL,
    ADD COLUMN IF NOT EXISTS email         varchar(128) NULL,
    ADD COLUMN IF NOT EXISTS user_type     varchar(32)  NULL;

COMMENT ON COLUMN cssd.cssd_staff.contact_name IS '联系人';
COMMENT ON COLUMN cssd.cssd_staff.contact_phone IS '联系电话';
COMMENT ON COLUMN cssd.cssd_staff.qq IS 'QQ';
COMMENT ON COLUMN cssd.cssd_staff.email IS '电子邮箱';
COMMENT ON COLUMN cssd.cssd_staff.user_type IS '用户类型';
