-- 设备：组织机构隔离 + 台账扩展字段（保留 device_kind / device_no / ster_temp_class 供业务使用）
ALTER TABLE cssd.cssd_device
    ADD COLUMN IF NOT EXISTS org_id varchar(36),
    ADD COLUMN IF NOT EXISTS org_code varchar(32),
    ADD COLUMN IF NOT EXISTS spec varchar(128),
    ADD COLUMN IF NOT EXISTS unit varchar(32),
    ADD COLUMN IF NOT EXISTS serial_no varchar(64),
    ADD COLUMN IF NOT EXISTS device_type varchar(64),
    ADD COLUMN IF NOT EXISTS install_location varchar(128),
    ADD COLUMN IF NOT EXISTS area_name varchar(128),
    ADD COLUMN IF NOT EXISTS capability_params varchar(500),
    ADD COLUMN IF NOT EXISTS enabled_date date,
    ADD COLUMN IF NOT EXISTS device_status varchar(32),
    ADD COLUMN IF NOT EXISTS operator_name varchar(64);

COMMENT ON COLUMN cssd.cssd_device.org_id IS '所属组织机构ID';
COMMENT ON COLUMN cssd.cssd_device.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_device.spec IS '规格';
COMMENT ON COLUMN cssd.cssd_device.unit IS '单位';
COMMENT ON COLUMN cssd.cssd_device.serial_no IS '序列号';
COMMENT ON COLUMN cssd.cssd_device.device_type IS '设备类型（台账展示）';
COMMENT ON COLUMN cssd.cssd_device.install_location IS '安装位置';
COMMENT ON COLUMN cssd.cssd_device.area_name IS '所在区域';
COMMENT ON COLUMN cssd.cssd_device.capability_params IS '设备能力参数';
COMMENT ON COLUMN cssd.cssd_device.enabled_date IS '启用日期';
COMMENT ON COLUMN cssd.cssd_device.device_status IS '设备状态';
COMMENT ON COLUMN cssd.cssd_device.operator_name IS '设备操作责任人';

UPDATE cssd.cssd_device d
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
WHERE d.org_id IS NULL OR d.org_id = '';

UPDATE cssd.cssd_device
SET device_type = CASE lower(coalesce(device_kind, ''))
        WHEN 'washer' THEN '清洗机'
        WHEN 'sterilizer' THEN '灭菌器'
        WHEN 'sealer' THEN '封口机'
        ELSE coalesce(device_kind, device_type)
    END
WHERE device_type IS NULL OR device_type = '';

UPDATE cssd.cssd_device
SET device_status = '空闲'
WHERE device_status IS NULL OR device_status = '';

DROP INDEX IF EXISTS cssd.uk_cssd_device_code;
CREATE INDEX IF NOT EXISTS idx_cssd_device_org_id ON cssd.cssd_device (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_device_org_code
    ON cssd.cssd_device (org_id, device_code) WHERE is_deleted = 0;
