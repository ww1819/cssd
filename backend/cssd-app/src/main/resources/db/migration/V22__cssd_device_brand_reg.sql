-- 设备：品牌、国别、医疗器械注册证号、生产日期
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS brand_name varchar(128),
    ADD COLUMN IF NOT EXISTS country_name varchar(64),
    ADD COLUMN IF NOT EXISTS device_reg_no varchar(128),
    ADD COLUMN IF NOT EXISTS production_date date;

COMMENT ON COLUMN cssd.cssd_device.brand_name IS '品牌';
COMMENT ON COLUMN cssd.cssd_device.country_name IS '国别';
COMMENT ON COLUMN cssd.cssd_device.device_reg_no IS '医疗器械注册证号';
COMMENT ON COLUMN cssd.cssd_device.production_date IS '生产日期';
