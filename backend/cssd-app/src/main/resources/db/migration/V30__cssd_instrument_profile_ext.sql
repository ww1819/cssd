-- 器械基础档案：扩展基础信息字段
ALTER TABLE cssd.cssd_instrument
    ADD COLUMN IF NOT EXISTS alias_name varchar(200),
    ADD COLUMN IF NOT EXISTS english_name varchar(200),
    ADD COLUMN IF NOT EXISTS brand_name varchar(128),
    ADD COLUMN IF NOT EXISTS manufacturer_id varchar(36),
    ADD COLUMN IF NOT EXISTS manufacturer_name varchar(200),
    ADD COLUMN IF NOT EXISTS register_valid_until date,
    ADD COLUMN IF NOT EXISTS material_name varchar(128),
    ADD COLUMN IF NOT EXISTS instrument_category varchar(64),
    ADD COLUMN IF NOT EXISTS instrument_type varchar(64),
    ADD COLUMN IF NOT EXISTS ster_method varchar(32),
    ADD COLUMN IF NOT EXISTS wash_method varchar(64),
    ADD COLUMN IF NOT EXISTS pack_requirement varchar(255),
    ADD COLUMN IF NOT EXISTS cost_price numeric(14, 4),
    ADD COLUMN IF NOT EXISTS vendor_id varchar(36),
    ADD COLUMN IF NOT EXISTS vendor_name varchar(200),
    ADD COLUMN IF NOT EXISTS storage_attr varchar(128),
    ADD COLUMN IF NOT EXISTS is_high_value smallint NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS is_key_monitor smallint NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS current_status varchar(32);

COMMENT ON COLUMN cssd.cssd_instrument.alias_name IS '别名';
COMMENT ON COLUMN cssd.cssd_instrument.english_name IS '英文名称';
COMMENT ON COLUMN cssd.cssd_instrument.brand_name IS '品牌';
COMMENT ON COLUMN cssd.cssd_instrument.manufacturer_id IS '生产厂家ID';
COMMENT ON COLUMN cssd.cssd_instrument.manufacturer_name IS '生产厂家名称快照';
COMMENT ON COLUMN cssd.cssd_instrument.register_valid_until IS '注册证有效期';
COMMENT ON COLUMN cssd.cssd_instrument.material_name IS '材质';
COMMENT ON COLUMN cssd.cssd_instrument.instrument_category IS '器械分类';
COMMENT ON COLUMN cssd.cssd_instrument.instrument_type IS '器械类型';
COMMENT ON COLUMN cssd.cssd_instrument.ster_method IS '灭菌方式';
COMMENT ON COLUMN cssd.cssd_instrument.wash_method IS '清洗方式';
COMMENT ON COLUMN cssd.cssd_instrument.pack_requirement IS '包装要求';
COMMENT ON COLUMN cssd.cssd_instrument.cost_price IS '成本单价';
COMMENT ON COLUMN cssd.cssd_instrument.vendor_id IS '供应商ID';
COMMENT ON COLUMN cssd.cssd_instrument.vendor_name IS '供应商名称快照';
COMMENT ON COLUMN cssd.cssd_instrument.storage_attr IS '存放属性';
COMMENT ON COLUMN cssd.cssd_instrument.is_high_value IS '高值器械：1是/0否';
COMMENT ON COLUMN cssd.cssd_instrument.is_key_monitor IS '重点监控器械：1是/0否';
COMMENT ON COLUMN cssd.cssd_instrument.current_status IS '当前状态';

CREATE INDEX IF NOT EXISTS idx_cssd_instrument_mfr_id ON cssd.cssd_instrument (manufacturer_id);
CREATE INDEX IF NOT EXISTS idx_cssd_instrument_vendor_id ON cssd.cssd_instrument (vendor_id);
