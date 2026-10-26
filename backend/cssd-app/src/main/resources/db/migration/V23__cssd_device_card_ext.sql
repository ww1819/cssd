-- 设备：资产卡片扩展 + 设备图片/保养记录/不良事件（JSON）
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS original_value numeric(14, 2),
    ADD COLUMN IF NOT EXISTS purchase_date date,
    ADD COLUMN IF NOT EXISTS device_images text,
    ADD COLUMN IF NOT EXISTS maintain_logs text,
    ADD COLUMN IF NOT EXISTS adverse_events text;

COMMENT ON COLUMN cssd.cssd_device.original_value IS '原值（元）';
COMMENT ON COLUMN cssd.cssd_device.purchase_date IS '购置日期';
COMMENT ON COLUMN cssd.cssd_device.device_images IS '设备图片JSON[{name,dataUrl}]';
COMMENT ON COLUMN cssd.cssd_device.maintain_logs IS '保养记录JSON[{maintainAt,content,operator}]';
COMMENT ON COLUMN cssd.cssd_device.adverse_events IS '不良事件JSON[{eventAt,title,content,level}]';
