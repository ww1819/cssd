-- 设备：操作责任人关联人员档案
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS operator_id varchar(36);

COMMENT ON COLUMN cssd.cssd_device.operator_id IS '操作责任人（cssd_staff.id）';
