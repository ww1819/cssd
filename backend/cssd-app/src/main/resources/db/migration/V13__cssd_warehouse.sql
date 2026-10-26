-- 仓库：归属组织机构，可选挂院区；按 org_id 隔离
CREATE TABLE IF NOT EXISTS cssd.cssd_warehouse (
    id              varchar(36)  PRIMARY KEY,
    org_id          varchar(36)  NOT NULL,
    org_code        varchar(32)  NULL,
    campus_id       varchar(36)  NULL,
    warehouse_code  varchar(32)  NOT NULL,
    warehouse_name  varchar(64)  NOT NULL,
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

COMMENT ON TABLE cssd.cssd_warehouse IS '仓库（归属组织机构，可选所属院区）';
COMMENT ON COLUMN cssd.cssd_warehouse.org_id IS '所属组织机构ID（cssd_campus.id）';
COMMENT ON COLUMN cssd.cssd_warehouse.org_code IS '所属组织机构编码（冗余）';
COMMENT ON COLUMN cssd.cssd_warehouse.campus_id IS '所属院区ID（cssd_area.id，可空）';
COMMENT ON COLUMN cssd.cssd_warehouse.warehouse_code IS '仓库编码';
COMMENT ON COLUMN cssd.cssd_warehouse.warehouse_name IS '仓库名称';

CREATE INDEX IF NOT EXISTS idx_cssd_warehouse_org_id ON cssd.cssd_warehouse (org_id);
CREATE INDEX IF NOT EXISTS idx_cssd_warehouse_campus_id ON cssd.cssd_warehouse (campus_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cssd_warehouse_org_code ON cssd.cssd_warehouse (org_id, warehouse_code) WHERE is_deleted = 0;
