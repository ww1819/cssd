-- 设备：维保信息 + 资质证件
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS maintain_cycle varchar(64),
    ADD COLUMN IF NOT EXISTS maintain_plan varchar(500),
    ADD COLUMN IF NOT EXISTS last_maintain_at date,
    ADD COLUMN IF NOT EXISTS maintain_expire_warn varchar(64),
    ADD COLUMN IF NOT EXISTS inspect_cert_no varchar(64),
    ADD COLUMN IF NOT EXISTS inspect_valid_until date,
    ADD COLUMN IF NOT EXISTS cert_attachment varchar(255),
    ADD COLUMN IF NOT EXISTS cert_attachment_data text;

COMMENT ON COLUMN cssd.cssd_device.maintain_cycle IS '维保周期';
COMMENT ON COLUMN cssd.cssd_device.maintain_plan IS '维保计划';
COMMENT ON COLUMN cssd.cssd_device.last_maintain_at IS '上次维保时间';
COMMENT ON COLUMN cssd.cssd_device.maintain_expire_warn IS '维保到期预警';
COMMENT ON COLUMN cssd.cssd_device.inspect_cert_no IS '检定证书编号';
COMMENT ON COLUMN cssd.cssd_device.inspect_valid_until IS '检定有效期（计量强检设备）';
COMMENT ON COLUMN cssd.cssd_device.cert_attachment IS '证书附件文件名';
COMMENT ON COLUMN cssd.cssd_device.cert_attachment_data IS '证书附件内容（base64，可选）';
