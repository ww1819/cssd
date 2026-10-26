-- 计量单位字典：按组织机构隔离
CREATE TABLE IF NOT EXISTS cssd.cssd_unit (
    id              varchar(36)  PRIMARY KEY,
    org_id          varchar(36)  NOT NULL,
    org_code        varchar(32)  NULL,
    unit_code       varchar(32)  NOT NULL,
    unit_name       varchar(64)  NOT NULL,
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

COMMENT ON TABLE cssd.cssd_unit IS '计量单位（归属组织机构）';
COMMENT ON COLUMN cssd.cssd_unit.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_unit.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_unit.unit_code IS '单位编码';
COMMENT ON COLUMN cssd.cssd_unit.unit_name IS '单位名称';

CREATE INDEX IF NOT EXISTS idx_cssd_unit_org_id ON cssd.cssd_unit (org_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_unit_org_code ON cssd.cssd_unit (org_id, unit_code) WHERE is_deleted = 0;
