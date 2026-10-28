-- 器械基础档案：计量单位（取自单位维护）
ALTER TABLE cssd.cssd_instrument
    ADD COLUMN IF NOT EXISTS unit_id varchar(36),
    ADD COLUMN IF NOT EXISTS unit_name varchar(64);

COMMENT ON COLUMN cssd.cssd_instrument.cost_price IS '单价';
COMMENT ON COLUMN cssd.cssd_instrument.unit_id IS '计量单位ID（cssd_unit.id）';
COMMENT ON COLUMN cssd.cssd_instrument.unit_name IS '计量单位名称快照';

CREATE INDEX IF NOT EXISTS idx_cssd_instrument_unit_id ON cssd.cssd_instrument (unit_id);
