-- 供应商 / 生产厂家：组织机构隔离 + 档案扩展字段
ALTER TABLE cssd.cssd_vendor
    ADD COLUMN IF NOT EXISTS org_id varchar(36),
    ADD COLUMN IF NOT EXISTS org_code varchar(32),
    ADD COLUMN IF NOT EXISTS short_code varchar(64),
    ADD COLUMN IF NOT EXISTS social_credit_code varchar(64),
    ADD COLUMN IF NOT EXISTS legal_person varchar(64),
    ADD COLUMN IF NOT EXISTS salesman varchar(64),
    ADD COLUMN IF NOT EXISTS contact_phone varchar(32),
    ADD COLUMN IF NOT EXISTS company_address varchar(255),
    ADD COLUMN IF NOT EXISTS email varchar(128);

ALTER TABLE cssd.cssd_manufacturer
    ADD COLUMN IF NOT EXISTS org_id varchar(36),
    ADD COLUMN IF NOT EXISTS org_code varchar(32),
    ADD COLUMN IF NOT EXISTS short_code varchar(64),
    ADD COLUMN IF NOT EXISTS social_credit_code varchar(64),
    ADD COLUMN IF NOT EXISTS legal_person varchar(64),
    ADD COLUMN IF NOT EXISTS salesman varchar(64),
    ADD COLUMN IF NOT EXISTS contact_phone varchar(32),
    ADD COLUMN IF NOT EXISTS company_address varchar(255),
    ADD COLUMN IF NOT EXISTS email varchar(128);

COMMENT ON COLUMN cssd.cssd_vendor.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_vendor.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_vendor.short_code IS '简码';
COMMENT ON COLUMN cssd.cssd_vendor.social_credit_code IS '统一社会信用代码';
COMMENT ON COLUMN cssd.cssd_vendor.legal_person IS '法人';
COMMENT ON COLUMN cssd.cssd_vendor.salesman IS '业务员';
COMMENT ON COLUMN cssd.cssd_vendor.contact_phone IS '联系电话';
COMMENT ON COLUMN cssd.cssd_vendor.company_address IS '公司地址';
COMMENT ON COLUMN cssd.cssd_vendor.email IS '邮箱';

COMMENT ON COLUMN cssd.cssd_manufacturer.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_manufacturer.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_manufacturer.short_code IS '简码';
COMMENT ON COLUMN cssd.cssd_manufacturer.social_credit_code IS '统一社会信用代码';
COMMENT ON COLUMN cssd.cssd_manufacturer.legal_person IS '法人';
COMMENT ON COLUMN cssd.cssd_manufacturer.salesman IS '业务员';
COMMENT ON COLUMN cssd.cssd_manufacturer.contact_phone IS '联系电话';
COMMENT ON COLUMN cssd.cssd_manufacturer.company_address IS '公司地址';
COMMENT ON COLUMN cssd.cssd_manufacturer.email IS '邮箱';

-- 存量数据回填到首个组织机构
UPDATE cssd.cssd_vendor v
SET org_id = c.id,
    org_code = c.campus_code
FROM (
    SELECT id, campus_code
    FROM cssd.cssd_campus
    WHERE (org_id IS NULL OR org_id = '')
      AND is_deleted = 0
    ORDER BY campus_code NULLS LAST, created_at
    LIMIT 1
) c
WHERE v.org_id IS NULL OR v.org_id = '';

UPDATE cssd.cssd_manufacturer m
SET org_id = c.id,
    org_code = c.campus_code
FROM (
    SELECT id, campus_code
    FROM cssd.cssd_campus
    WHERE (org_id IS NULL OR org_id = '')
      AND is_deleted = 0
    ORDER BY campus_code NULLS LAST, created_at
    LIMIT 1
) c
WHERE m.org_id IS NULL OR m.org_id = '';

DROP INDEX IF EXISTS cssd.uk_cssd_vendor_code;
DROP INDEX IF EXISTS cssd.uk_cssd_mfr_code;

CREATE INDEX IF NOT EXISTS idx_cssd_vendor_org_id ON cssd.cssd_vendor (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_vendor_org_code
    ON cssd.cssd_vendor (org_id, vendor_code) WHERE is_deleted = 0;

CREATE INDEX IF NOT EXISTS idx_cssd_mfr_org_id ON cssd.cssd_manufacturer (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_mfr_org_code
    ON cssd.cssd_manufacturer (org_id, mfr_code) WHERE is_deleted = 0;
