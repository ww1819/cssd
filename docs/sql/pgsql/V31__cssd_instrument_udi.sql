-- 器械基础档案：UDI
ALTER TABLE cssd.cssd_instrument
    ADD COLUMN IF NOT EXISTS udi varchar(128);

COMMENT ON COLUMN cssd.cssd_instrument.udi IS 'UDI 唯一器械标识';

CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_instrument_org_udi
    ON cssd.cssd_instrument (org_id, udi) WHERE is_deleted = 0 AND udi IS NOT NULL AND udi <> '';
