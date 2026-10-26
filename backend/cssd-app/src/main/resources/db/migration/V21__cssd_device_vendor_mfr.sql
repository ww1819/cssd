-- 设备：生产厂家、供应商（关联本机构字典）
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS manufacturer_id varchar(36),
    ADD COLUMN IF NOT EXISTS manufacturer_name varchar(200),
    ADD COLUMN IF NOT EXISTS vendor_id varchar(36),
    ADD COLUMN IF NOT EXISTS vendor_name varchar(200);

COMMENT ON COLUMN cssd.cssd_device.manufacturer_id IS '生产厂家ID（cssd_manufacturer）';
COMMENT ON COLUMN cssd.cssd_device.manufacturer_name IS '生产厂家名称（冗余）';
COMMENT ON COLUMN cssd.cssd_device.vendor_id IS '供应商ID（cssd_vendor）';
COMMENT ON COLUMN cssd.cssd_device.vendor_name IS '供应商名称（冗余）';
