-- 器械基础档案：图片、附件
ALTER TABLE cssd.cssd_instrument
    ADD COLUMN IF NOT EXISTS instrument_images text,
    ADD COLUMN IF NOT EXISTS instrument_attachments text;

COMMENT ON COLUMN cssd.cssd_instrument.instrument_images IS '器械图片JSON[{name,dataUrl}]';
COMMENT ON COLUMN cssd.cssd_instrument.instrument_attachments IS '器械附件JSON[{name,dataUrl}]';
