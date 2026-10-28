-- 器械基础档案：组织机构隔离 + 简码
ALTER TABLE cssd.cssd_instrument
    ADD COLUMN IF NOT EXISTS org_id varchar(36),
    ADD COLUMN IF NOT EXISTS org_code varchar(32),
    ADD COLUMN IF NOT EXISTS short_code varchar(64);

COMMENT ON COLUMN cssd.cssd_instrument.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_instrument.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_instrument.short_code IS '简码';

-- 存量数据回填到首个组织机构
UPDATE cssd.cssd_instrument i
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
WHERE i.org_id IS NULL OR i.org_id = '';

-- 简码兜底：沿用名称拼音简码
UPDATE cssd.cssd_instrument
SET short_code = upper(name_pinyin)
WHERE (short_code IS NULL OR short_code = '')
  AND name_pinyin IS NOT NULL
  AND name_pinyin <> '';

DROP INDEX IF EXISTS cssd.uk_cssd_instrument_code;
DROP INDEX IF EXISTS cssd.uk_cssd_instrument_barcode;

CREATE INDEX IF NOT EXISTS idx_cssd_instrument_org_id ON cssd.cssd_instrument (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_instrument_org_code
    ON cssd.cssd_instrument (org_id, instrument_code) WHERE is_deleted = 0;
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_instrument_org_barcode
    ON cssd.cssd_instrument (org_id, barcode) WHERE is_deleted = 0 AND barcode IS NOT NULL;
