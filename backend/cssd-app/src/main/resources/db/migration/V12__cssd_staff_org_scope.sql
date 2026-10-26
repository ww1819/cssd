-- 人员归属组织机构（数据隔离）
ALTER TABLE cssd.cssd_staff
    ADD COLUMN IF NOT EXISTS org_id   varchar(36) NULL,
    ADD COLUMN IF NOT EXISTS org_code varchar(64) NULL;

COMMENT ON COLUMN cssd.cssd_staff.org_id IS '所属组织机构ID（cssd_campus.id，org_id为空的机构行）';
COMMENT ON COLUMN cssd.cssd_staff.org_code IS '所属组织机构编码（冗余，便于隔离与展示）';

-- 回填：历史初始化账号 remark=ORG:{campusId}
UPDATE cssd.cssd_staff s
SET org_id = substr(s.remark, 5)
WHERE s.remark LIKE 'ORG:%'
  AND (s.org_id IS NULL OR s.org_id = '');

UPDATE cssd.cssd_staff s
SET org_code = c.campus_code
FROM cssd.cssd_campus c
WHERE s.org_id = c.id
  AND (s.org_code IS NULL OR s.org_code = '');

CREATE INDEX IF NOT EXISTS idx_cssd_staff_org_id ON cssd.cssd_staff (org_id);
