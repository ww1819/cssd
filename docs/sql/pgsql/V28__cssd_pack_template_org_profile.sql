-- 器械包档案：组织机构隔离 + 简码
ALTER TABLE cssd.cssd_pack_template
    ADD COLUMN IF NOT EXISTS org_id varchar(36),
    ADD COLUMN IF NOT EXISTS org_code varchar(32),
    ADD COLUMN IF NOT EXISTS short_code varchar(64);

COMMENT ON COLUMN cssd.cssd_pack_template.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_pack_template.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_pack_template.short_code IS '简码';

UPDATE cssd.cssd_pack_template t
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
WHERE t.org_id IS NULL OR t.org_id = '';

UPDATE cssd.cssd_pack_template
SET short_code = upper(name_pinyin)
WHERE (short_code IS NULL OR short_code = '')
  AND name_pinyin IS NOT NULL
  AND name_pinyin <> '';

DROP INDEX IF EXISTS cssd.uk_cssd_template_code;

CREATE INDEX IF NOT EXISTS idx_cssd_pack_template_org_id ON cssd.cssd_pack_template (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_pack_template_org_code
    ON cssd.cssd_pack_template (org_id, template_code) WHERE is_deleted = 0;
