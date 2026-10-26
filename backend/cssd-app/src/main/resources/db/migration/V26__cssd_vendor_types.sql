-- 供应商类型（可多选，逗号分隔）
ALTER TABLE cssd.cssd_vendor
    ADD COLUMN IF NOT EXISTS vendor_types varchar(255);

COMMENT ON COLUMN cssd.cssd_vendor.vendor_types IS '供应商类型（多选，逗号分隔编码）';
