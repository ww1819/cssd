-- 院区归属组织机构：org_id 为空表示组织机构，非空表示院区
ALTER TABLE cssd.cssd_campus ADD COLUMN IF NOT EXISTS org_id varchar(36) NULL;
COMMENT ON COLUMN cssd.cssd_campus.org_id IS '所属组织机构ID；空=组织机构，非空=院区';
CREATE INDEX IF NOT EXISTS idx_cssd_campus_org_id ON cssd.cssd_campus (org_id);
