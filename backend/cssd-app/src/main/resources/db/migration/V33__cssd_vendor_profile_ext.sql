-- 供应商档案：扩展基础信息字段
ALTER TABLE cssd.cssd_vendor
    ADD COLUMN IF NOT EXISTS registered_capital varchar(64),
    ADD COLUMN IF NOT EXISTS company_principal varchar(64),
    ADD COLUMN IF NOT EXISTS business_scope varchar(500),
    ADD COLUMN IF NOT EXISTS fax varchar(64),
    ADD COLUMN IF NOT EXISTS website varchar(255);

COMMENT ON COLUMN cssd.cssd_vendor.registered_capital IS '注册资金';
COMMENT ON COLUMN cssd.cssd_vendor.company_principal IS '公司负责人';
COMMENT ON COLUMN cssd.cssd_vendor.business_scope IS '经营范围';
COMMENT ON COLUMN cssd.cssd_vendor.fax IS '传真';
COMMENT ON COLUMN cssd.cssd_vendor.website IS '网址';
