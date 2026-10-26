-- 院区独立表：组织机构(cssd_campus) 为上一级，院区(cssd_area) 为下一级
CREATE TABLE IF NOT EXISTS cssd.cssd_area (
    id              varchar(36)  PRIMARY KEY,
    org_id          varchar(36)  NOT NULL,
    area_code       varchar(32)  NOT NULL,
    area_name       varchar(64)  NOT NULL,
    name_pinyin     varchar(128) NULL,
    is_active       int          NOT NULL DEFAULT 1,
    remark          varchar(500) NULL,
    created_at      timestamptz  NULL,
    created_by      varchar(36)  NULL,
    created_by_no   varchar(64)  NULL,
    created_by_name varchar(64)  NULL,
    updated_at      timestamptz  NULL,
    updated_by      varchar(36)  NULL,
    updated_by_no   varchar(64)  NULL,
    updated_by_name varchar(64)  NULL,
    is_deleted      int          NOT NULL DEFAULT 0,
    deleted_at      timestamptz  NULL,
    deleted_by      varchar(36)  NULL,
    deleted_by_no   varchar(64)  NULL,
    deleted_by_name varchar(64)  NULL,
    create_channel  varchar(32)  NULL,
    update_channel  varchar(32)  NULL,
    delete_channel  varchar(32)  NULL
);

COMMENT ON TABLE cssd.cssd_area IS '院区（下一级，归属组织机构）';
COMMENT ON COLUMN cssd.cssd_area.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_area.area_code IS '院区编码';
COMMENT ON COLUMN cssd.cssd_area.area_name IS '院区名称';

CREATE INDEX IF NOT EXISTS idx_cssd_area_org_id ON cssd.cssd_area (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_area_org_code ON cssd.cssd_area (org_id, area_code) WHERE is_deleted = 0;
