-- 组织机构扩展：机构代码、统一社会信用代码、地址、联系人、联系电话
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS org_code varchar(64) NULL;
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS social_credit_code varchar(32) NULL;
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS address varchar(500) NULL;
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS contact_name varchar(64) NULL;
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS contact_phone varchar(32) NULL;

COMMENT ON COLUMN cssd.cssd_campus.org_code IS '机构代码';
COMMENT ON COLUMN cssd.cssd_campus.social_credit_code IS '社会统一信用代码';
COMMENT ON COLUMN cssd.cssd_campus.address IS '地址';
COMMENT ON COLUMN cssd.cssd_campus.contact_name IS '联系人';
COMMENT ON COLUMN cssd.cssd_campus.contact_phone IS '联系电话';
