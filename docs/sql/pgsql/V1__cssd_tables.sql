-- CSSD 追溯系统 · PostgreSQL 全量建表
-- 槽位：V1（约定包 §2.1）
-- 主键 UUID7 varchar(36)，应用层生成；禁止 serial
-- 质量/追溯表禁止业务物理删除（is_deleted）
-- 生成日期：2026-08-27

CREATE SCHEMA IF NOT EXISTS cssd;


-- 字典（状态/分类码，展示走中文标签）
CREATE TABLE cssd.cssd_dict (
    id                  varchar(36)     NOT NULL,
    dict_type           varchar(64)     NOT NULL,
    dict_code           varchar(64)     NOT NULL,
    dict_label          varchar(128)    NOT NULL,
    sort_no             integer         NOT NULL DEFAULT 0,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_dict PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_dict IS '字典（状态/分类码，展示走中文标签）';


-- 院区
CREATE TABLE cssd.cssd_campus (
    id                  varchar(36)     NOT NULL,
    campus_code         varchar(64)     NOT NULL,
    campus_name         varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_campus PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_campus IS '院区';


-- 科室
CREATE TABLE cssd.cssd_dept (
    id                  varchar(36)     NOT NULL,
    dept_code           varchar(64)     NOT NULL,
    dept_name           varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    campus_id           varchar(36)     NULL,
    dept_kind           varchar(32)     NOT NULL DEFAULT 'clinical',
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_dept PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_dept IS '科室';


-- 人员（CSSD 操作员 / 术者等）
CREATE TABLE cssd.cssd_staff (
    id                  varchar(36)     NOT NULL,
    staff_no            varchar(32)     NOT NULL,
    staff_name          varchar(64)     NOT NULL,
    name_pinyin         varchar(64)     NULL,
    dept_id             varchar(36)     NULL,
    staff_role          varchar(32)     NOT NULL DEFAULT 'cssd',
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_staff PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_staff IS '人员（CSSD 操作员 / 术者等）';


-- 供应商
CREATE TABLE cssd.cssd_vendor (
    id                  varchar(36)     NOT NULL,
    vendor_code         varchar(64)     NOT NULL,
    vendor_name         varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_vendor PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_vendor IS '供应商';


-- 生产厂家
CREATE TABLE cssd.cssd_manufacturer (
    id                  varchar(36)     NOT NULL,
    mfr_code            varchar(64)     NOT NULL,
    mfr_name            varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_manufacturer PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_manufacturer IS '生产厂家';


-- 包装材料
CREATE TABLE cssd.cssd_pack_material (
    id                  varchar(36)     NOT NULL,
    material_code       varchar(64)     NOT NULL,
    material_name       varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    shelf_life_days     integer         NOT NULL,
    ster_method         varchar(32)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_pack_material PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_pack_material IS '包装材料';


-- 器械目录
CREATE TABLE cssd.cssd_instrument (
    id                  varchar(36)     NOT NULL,
    instrument_code     varchar(64)     NOT NULL,
    instrument_name     varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    spec                varchar(128)    NULL,
    model               varchar(128)    NULL,
    register_no         varchar(128)    NULL,
    barcode             varchar(64)     NULL,
    owner_dept_id       varchar(36)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_instrument PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_instrument IS '器械目录';


-- 器械包模板（种类档案）
CREATE TABLE cssd.cssd_pack_template (
    id                  varchar(36)     NOT NULL,
    template_code       varchar(64)     NOT NULL,
    template_name       varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    pack_category       varchar(32)     NOT NULL,
    owner_dept_id       varchar(36)     NULL,
    owner_dept_code     varchar(64)     NULL,
    owner_dept_name     varchar(200)    NULL,
    material_id         varchar(36)     NULL,
    material_code       varchar(64)     NULL,
    material_name       varchar(200)    NULL,
    shelf_life_days     integer         NULL,
    basket_load_qty     integer         NULL,
    safety_stock_qty    integer         NULL,
    need_wash           smallint        NOT NULL DEFAULT 1,
    wash_mode           varchar(32)     NOT NULL DEFAULT 'scatter',
    need_pack           smallint        NOT NULL DEFAULT 1,
    need_store          smallint        NOT NULL DEFAULT 1,
    need_recycle        smallint        NOT NULL DEFAULT 1,
    is_implant          smallint        NOT NULL DEFAULT 0,
    image_url           varchar(500)    NULL,
    check_image_url     varchar(500)    NULL,
    ster_method         varchar(32)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_pack_template PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_pack_template IS '器械包模板（种类档案）';


-- 包模板明细
CREATE TABLE cssd.cssd_pack_template_item (
    id                  varchar(36)     NOT NULL,
    template_id         varchar(36)     NOT NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instrument_id       varchar(36)     NOT NULL,
    instrument_code     varchar(64)     NOT NULL,
    instrument_name     varchar(200)    NOT NULL,
    spec                varchar(128)    NULL,
    model               varchar(128)    NULL,
    qty                 integer         NOT NULL DEFAULT 1,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_pack_template_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_pack_template_item IS '包模板明细';


-- 清洗机 / 灭菌器等设备
CREATE TABLE cssd.cssd_device (
    id                  varchar(36)     NOT NULL,
    device_code         varchar(64)     NOT NULL,
    device_name         varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    device_kind         varchar(32)     NOT NULL,
    device_no           varchar(64)     NOT NULL,
    model               varchar(128)    NULL,
    ster_temp_class     varchar(32)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_device PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_device IS '清洗机 / 灭菌器等设备';


-- 设备程序
CREATE TABLE cssd.cssd_device_program (
    id                  varchar(36)     NOT NULL,
    device_id           varchar(36)     NOT NULL,
    device_code         varchar(64)     NULL,
    device_name         varchar(200)    NULL,
    device_no           varchar(64)     NULL,
    program_code        varchar(64)     NOT NULL,
    program_name        varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_device_program PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_device_program IS '设备程序';


-- 清洗篮 / 装载篮 / 装载架
CREATE TABLE cssd.cssd_basket (
    id                  varchar(36)     NOT NULL,
    basket_code         varchar(64)     NOT NULL,
    basket_name         varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    basket_kind         varchar(32)     NOT NULL,
    color_code          varchar(32)     NULL,
    basket_status       varchar(32)     NOT NULL DEFAULT 'idle',
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_basket PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_basket IS '清洗篮 / 装载篮 / 装载架';


-- 下送车
CREATE TABLE cssd.cssd_cart (
    id                  varchar(36)     NOT NULL,
    cart_code           varchar(64)     NOT NULL,
    cart_name           varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_cart PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_cart IS '下送车';


-- 一次性物品品规
CREATE TABLE cssd.cssd_once_item (
    id                  varchar(36)     NOT NULL,
    item_code           varchar(64)     NOT NULL,
    item_name           varchar(200)    NOT NULL,
    name_pinyin         varchar(64)     NULL,
    spec                varchar(128)    NULL,
    unit_name           varchar(32)     NULL,
    mfr_id              varchar(36)     NULL,
    mfr_code            varchar(64)     NULL,
    mfr_name            varchar(200)    NULL,
    vendor_id           varchar(36)     NULL,
    vendor_code         varchar(64)     NULL,
    vendor_name         varchar(200)    NULL,
    min_stock           integer         NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_once_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_once_item IS '一次性物品品规';


-- 包实例（配包提交后诞生，唯一条码不复用）
CREATE TABLE cssd.cssd_pack_instance (
    id                  varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    label_carrier       varchar(16)     NOT NULL DEFAULT 'qr',
    template_id         varchar(36)     NOT NULL,
    template_code       varchar(64)     NOT NULL,
    template_name       varchar(200)    NOT NULL,
    pack_category       varchar(32)     NOT NULL,
    status              varchar(32)     NOT NULL,
    material_id         varchar(36)     NULL,
    material_code       varchar(64)     NULL,
    material_name       varchar(200)    NULL,
    shelf_life_days     integer         NULL,
    is_implant          smallint        NOT NULL DEFAULT 0,
    need_wash           smallint        NULL,
    wash_mode           varchar(32)     NULL,
    need_pack           smallint        NULL,
    need_store          smallint        NULL,
    need_recycle        smallint        NULL,
    ster_method         varchar(32)     NULL,
    urgent_flag         smallint        NOT NULL DEFAULT 0,
    owner_dept_id       varchar(36)     NULL,
    owner_dept_code     varchar(64)     NULL,
    owner_dept_name     varchar(200)    NULL,
    packer_id           varchar(36)     NULL,
    packer_no           varchar(32)     NULL,
    packer_name         varchar(64)     NULL,
    packed_at           timestamptz     NULL,
    checker_id          varchar(36)     NULL,
    checker_no          varchar(32)     NULL,
    checker_name        varchar(64)     NULL,
    checked_at          timestamptz     NULL,
    wash_batch_id       varchar(36)     NULL,
    wash_batch_no       varchar(64)     NULL,
    ster_load_id        varchar(36)     NULL,
    ster_load_no        varchar(64)     NULL,
    device_id           varchar(36)     NULL,
    device_code         varchar(64)     NULL,
    device_name         varchar(200)    NULL,
    device_no           varchar(64)     NULL,
    program_id          varchar(36)     NULL,
    program_code        varchar(64)     NULL,
    program_name        varchar(200)    NULL,
    sterilized_at       timestamptz     NULL,
    sterilize_date      date            NULL,
    expire_date         date            NULL,
    loc_dept_id         varchar(36)     NULL,
    loc_dept_code       varchar(64)     NULL,
    loc_dept_name       varchar(200)    NULL,
    loc_campus_id       varchar(36)     NULL,
    loc_campus_code     varchar(64)     NULL,
    loc_campus_name     varchar(200)    NULL,
    process_printed_at  timestamptz     NULL,
    legal_printed_at    timestamptz     NULL,
    stored_at           timestamptz     NULL,
    store_staff_id      varchar(36)     NULL,
    store_staff_no      varchar(32)     NULL,
    store_staff_name    varchar(64)     NULL,
    released_at         timestamptz     NULL,
    release_staff_id    varchar(36)     NULL,
    release_staff_no    varchar(32)     NULL,
    release_staff_name  varchar(64)     NULL,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    dist_bill_id        varchar(36)     NULL,
    dist_bill_no        varchar(64)     NULL,
    recycle_bill_id     varchar(36)     NULL,
    recycle_bill_no     varchar(64)     NULL,
    recycle_item_id     varchar(36)     NULL,
    patient_no          varchar(64)     NULL,
    patient_name        varchar(64)     NULL,
    bed_no              varchar(32)     NULL,
    surgeon_id          varchar(36)     NULL,
    surgeon_no          varchar(32)     NULL,
    surgeon_name        varchar(64)     NULL,
    surgery_code        varchar(64)     NULL,
    surgery_name        varchar(200)    NULL,
    ext_vendor_id       varchar(36)     NULL,
    ext_vendor_code     varchar(64)     NULL,
    ext_vendor_name     varchar(200)    NULL,
    prev_instance_id    varchar(36)     NULL,
    loan_staff_id       varchar(36)     NULL,
    loan_staff_no       varchar(32)     NULL,
    loan_staff_name     varchar(64)     NULL,
    rfid_uid            varchar(64)     NULL,
    rfid_bound_at       timestamptz     NULL,
    rfid_binder_id      varchar(36)     NULL,
    rfid_binder_no      varchar(32)     NULL,
    rfid_binder_name    varchar(64)     NULL,
    his_patient_id      varchar(64)     NULL,
    his_encounter_id    varchar(64)     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_pack_instance PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_pack_instance IS '包实例（配包提交后诞生，唯一条码不复用）';


-- 包实例当时包内清单
CREATE TABLE cssd.cssd_pack_instance_item (
    id                  varchar(36)     NOT NULL,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instrument_id       varchar(36)     NULL,
    instrument_code     varchar(64)     NOT NULL,
    instrument_name     varchar(200)    NOT NULL,
    spec                varchar(128)    NULL,
    model               varchar(128)    NULL,
    register_no         varchar(128)    NULL,
    qty                 integer         NOT NULL DEFAULT 1,
    actual_qty          integer         NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_pack_instance_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_pack_instance_item IS '包实例当时包内清单';


-- 回收单
CREATE TABLE cssd.cssd_recycle_bill (
    id                  varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    bill_status         varchar(32)     NOT NULL DEFAULT 'draft',
    dept_id             varchar(36)     NOT NULL,
    dept_code           varchar(64)     NOT NULL,
    dept_name           varchar(200)    NOT NULL,
    campus_id           varchar(36)     NULL,
    campus_code         varchar(64)     NULL,
    campus_name         varchar(200)    NULL,
    recyc_at            timestamptz     NULL,
    recyc_staff_id      varchar(36)     NULL,
    recyc_staff_no      varchar(32)     NULL,
    recyc_staff_name    varchar(64)     NULL,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    source_kind         varchar(32)     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_recycle_bill PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_recycle_bill IS '回收单';


-- 回收明细
CREATE TABLE cssd.cssd_recycle_item (
    id                  varchar(36)     NOT NULL,
    bill_id             varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    apply_item_id       varchar(36)     NULL,
    dist_bill_id        varchar(36)     NULL,
    dist_bill_no        varchar(64)     NULL,
    dist_item_id        varchar(36)     NULL,
    basket_id           varchar(36)     NULL,
    basket_code         varchar(64)     NULL,
    basket_name         varchar(200)    NULL,
    qty                 integer         NOT NULL DEFAULT 1,
    packed_qty          integer         NOT NULL DEFAULT 0,
    urgent_flag         smallint        NOT NULL DEFAULT 0,
    contamination_flag  smallint        NOT NULL DEFAULT 0,
    infection_type      varchar(32)     NULL,
    has_defect          smallint        NOT NULL DEFAULT 0,
    defect_remark       varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_recycle_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_recycle_item IS '回收明细';


-- 清洗批次（手工/机洗）
CREATE TABLE cssd.cssd_wash_batch (
    id                  varchar(36)     NOT NULL,
    batch_no            varchar(64)     NOT NULL,
    wash_mode           varchar(32)     NOT NULL,
    template_wash_mode  varchar(32)     NULL,
    mode_overridden     smallint        NOT NULL DEFAULT 0,
    override_reason     varchar(500)    NULL,
    batch_status        varchar(32)     NOT NULL DEFAULT 'draft',
    device_id           varchar(36)     NULL,
    device_code         varchar(64)     NULL,
    device_name         varchar(200)    NULL,
    device_no           varchar(64)     NULL,
    program_id          varchar(36)     NULL,
    program_code        varchar(64)     NULL,
    program_name        varchar(200)    NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    started_at          timestamptz     NULL,
    finished_at         timestamptz     NULL,
    check_result        varchar(32)     NULL,
    checker_id          varchar(36)     NULL,
    checker_no          varchar(32)     NULL,
    checker_name        varchar(64)     NULL,
    checked_at          timestamptz     NULL,
    report_url          varchar(500)    NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_wash_batch PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_wash_batch IS '清洗批次（手工/机洗）';


-- 清洗明细
CREATE TABLE cssd.cssd_wash_item (
    id                  varchar(36)     NOT NULL,
    batch_id            varchar(36)     NOT NULL,
    batch_no            varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    basket_id           varchar(36)     NULL,
    basket_code         varchar(64)     NULL,
    basket_name         varchar(200)    NULL,
    recycle_bill_id     varchar(36)     NULL,
    recycle_bill_no     varchar(64)     NULL,
    recycle_item_id     varchar(36)     NULL,
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_wash_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_wash_item IS '清洗明细';


-- 灭菌锅次
CREATE TABLE cssd.cssd_ster_load (
    id                  varchar(36)     NOT NULL,
    load_no             varchar(64)     NOT NULL,
    load_status         varchar(32)     NOT NULL DEFAULT 'draft',
    device_id           varchar(36)     NOT NULL,
    device_code         varchar(64)     NOT NULL,
    device_name         varchar(200)    NOT NULL,
    device_no           varchar(64)     NOT NULL,
    program_id          varchar(36)     NULL,
    program_code        varchar(64)     NULL,
    program_name        varchar(200)    NULL,
    ster_temp_class     varchar(32)     NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    started_at          timestamptz     NULL,
    finished_at         timestamptz     NULL,
    sterilize_date      date            NULL,
    params_json         jsonb           NULL,
    report_url          varchar(500)    NULL,
    main_load_desc      varchar(500)    NULL,
    has_implant         smallint        NOT NULL DEFAULT 0,
    bd_result           varchar(32)     NULL,
    pcd_result          varchar(32)     NULL,
    bio_result          varchar(32)     NULL,
    chem_result         varchar(32)     NULL,
    release_status      varchar(32)     NOT NULL DEFAULT 'pending',
    released_at         timestamptz     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_ster_load PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_ster_load IS '灭菌锅次';


-- 灭菌装载明细
CREATE TABLE cssd.cssd_ster_load_item (
    id                  varchar(36)     NOT NULL,
    load_id             varchar(36)     NOT NULL,
    load_no             varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    pack_category       varchar(32)     NULL,
    is_implant          smallint        NOT NULL DEFAULT 0,
    sterilize_date      date            NULL,
    expire_date         date            NULL,
    device_id           varchar(36)     NULL,
    device_code         varchar(64)     NULL,
    device_name         varchar(200)    NULL,
    device_no           varchar(64)     NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_ster_load_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_ster_load_item IS '灭菌装载明细';


-- 监测记录（BD / 批量PCD / 生物）
CREATE TABLE cssd.cssd_monitor (
    id                  varchar(36)     NOT NULL,
    monitor_no          varchar(64)     NOT NULL,
    monitor_kind        varchar(32)     NOT NULL,
    monitor_status      varchar(32)     NOT NULL DEFAULT 'draft',
    device_id           varchar(36)     NOT NULL,
    device_code         varchar(64)     NOT NULL,
    device_name         varchar(200)    NOT NULL,
    device_no           varchar(64)     NOT NULL,
    load_id             varchar(36)     NULL,
    load_no             varchar(64)     NULL,
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    monitor_date        date            NOT NULL,
    result_code         varchar(32)     NULL,
    result_at           timestamptz     NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    report_url          varchar(500)    NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_monitor PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_monitor IS '监测记录（BD / 批量PCD / 生物）';


-- 申领单
CREATE TABLE cssd.cssd_apply_bill (
    id                  varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    bill_status         varchar(32)     NOT NULL DEFAULT 'draft',
    dept_id             varchar(36)     NOT NULL,
    dept_code           varchar(64)     NOT NULL,
    dept_name           varchar(200)    NOT NULL,
    campus_id           varchar(36)     NULL,
    campus_code         varchar(64)     NULL,
    campus_name         varchar(200)    NULL,
    apply_type          varchar(32)     NOT NULL DEFAULT 'dept',
    surgery_code        varchar(64)     NULL,
    surgery_name        varchar(200)    NULL,
    patient_no          varchar(64)     NULL,
    patient_name        varchar(64)     NULL,
    bed_no              varchar(32)     NULL,
    his_patient_id      varchar(64)     NULL,
    his_encounter_id    varchar(64)     NULL,
    his_apply_id        varchar(64)     NULL,
    need_at             timestamptz     NULL,
    applicant_id        varchar(36)     NULL,
    applicant_no        varchar(32)     NULL,
    applicant_name      varchar(64)     NULL,
    submitted_at        timestamptz     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_apply_bill PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_apply_bill IS '申领单';


-- 申领明细
CREATE TABLE cssd.cssd_apply_item (
    id                  varchar(36)     NOT NULL,
    bill_id             varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    template_id         varchar(36)     NOT NULL,
    template_code       varchar(64)     NOT NULL,
    template_name       varchar(200)    NOT NULL,
    qty                 integer         NOT NULL DEFAULT 1,
    dist_qty            integer         NOT NULL DEFAULT 0,
    recycle_qty         integer         NOT NULL DEFAULT 0,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_apply_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_apply_item IS '申领明细';


-- 发放/下送单
CREATE TABLE cssd.cssd_dist_bill (
    id                  varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    bill_status         varchar(32)     NOT NULL DEFAULT 'draft',
    dist_mode           varchar(32)     NOT NULL DEFAULT 'direct',
    dept_id             varchar(36)     NOT NULL,
    dept_code           varchar(64)     NOT NULL,
    dept_name           varchar(200)    NOT NULL,
    campus_id           varchar(36)     NULL,
    campus_code         varchar(64)     NULL,
    campus_name         varchar(200)    NULL,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    cart_id             varchar(36)     NULL,
    cart_code           varchar(64)     NULL,
    cart_name           varchar(200)    NULL,
    dist_staff_id       varchar(36)     NULL,
    dist_staff_no       varchar(32)     NULL,
    dist_staff_name     varchar(64)     NULL,
    dist_at             timestamptz     NULL,
    receiver_id         varchar(36)     NULL,
    receiver_no         varchar(32)     NULL,
    receiver_name       varchar(64)     NULL,
    received_at         timestamptz     NULL,
    receive_place       varchar(200)    NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_dist_bill PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_dist_bill IS '发放/下送单';


-- 发放明细
CREATE TABLE cssd.cssd_dist_item (
    id                  varchar(36)     NOT NULL,
    bill_id             varchar(36)     NOT NULL,
    bill_no             varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    apply_item_id       varchar(36)     NULL,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    sterilize_date      date            NULL,
    expire_date         date            NULL,
    device_no           varchar(64)     NULL,
    load_no             varchar(64)     NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_dist_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_dist_item IS '发放明细';


-- 术前/术后核准
CREATE TABLE cssd.cssd_use_check (
    id                  varchar(36)     NOT NULL,
    check_no            varchar(64)     NOT NULL,
    check_kind          varchar(32)     NOT NULL,
    check_status        varchar(32)     NOT NULL DEFAULT 'draft',
    dept_id             varchar(36)     NULL,
    dept_code           varchar(64)     NULL,
    dept_name           varchar(200)    NULL,
    campus_id           varchar(36)     NULL,
    campus_code         varchar(64)     NULL,
    campus_name         varchar(200)    NULL,
    apply_bill_id       varchar(36)     NULL,
    apply_bill_no       varchar(64)     NULL,
    patient_no          varchar(64)     NULL,
    patient_name        varchar(64)     NULL,
    bed_no              varchar(32)     NULL,
    surgeon_id          varchar(36)     NULL,
    surgeon_no          varchar(32)     NULL,
    surgeon_name        varchar(64)     NULL,
    surgery_code        varchar(64)     NULL,
    surgery_name        varchar(200)    NULL,
    his_patient_id      varchar(64)     NULL,
    his_encounter_id    varchar(64)     NULL,
    idempotency_key     varchar(64)     NULL,
    checker_id          varchar(36)     NULL,
    checker_no          varchar(32)     NULL,
    checker_name        varchar(64)     NULL,
    checked_at          timestamptz     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_use_check PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_use_check IS '术前/术后核准';


-- 核准明细
CREATE TABLE cssd.cssd_use_check_item (
    id                  varchar(36)     NOT NULL,
    check_id            varchar(36)     NOT NULL,
    check_no            varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    sterilize_date      date            NULL,
    expire_date         date            NULL,
    device_no           varchar(64)     NULL,
    qty_ok              smallint        NOT NULL DEFAULT 1,
    ster_ok             smallint        NOT NULL DEFAULT 1,
    item_qty            integer         NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_use_check_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_use_check_item IS '核准明细';


-- 召回单
CREATE TABLE cssd.cssd_recall (
    id                  varchar(36)     NOT NULL,
    recall_no           varchar(64)     NOT NULL,
    recall_status       varchar(32)     NOT NULL DEFAULT 'draft',
    recall_reason       varchar(32)     NOT NULL,
    device_id           varchar(36)     NULL,
    device_code         varchar(64)     NULL,
    device_name         varchar(200)    NULL,
    device_no           varchar(64)     NULL,
    load_id             varchar(36)     NULL,
    load_no             varchar(64)     NULL,
    monitor_id          varchar(36)     NULL,
    since_time          timestamptz     NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    recalled_at         timestamptz     NULL,
    remark              varchar(1000)   NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_recall PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_recall IS '召回单';


-- 召回明细
CREATE TABLE cssd.cssd_recall_item (
    id                  varchar(36)     NOT NULL,
    recall_id           varchar(36)     NOT NULL,
    recall_no           varchar(64)     NOT NULL,
    line_no             integer         NOT NULL DEFAULT 0,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    loc_dept_id         varchar(36)     NULL,
    loc_dept_code       varchar(64)     NULL,
    loc_dept_name       varchar(200)    NULL,
    loc_campus_id       varchar(36)     NULL,
    loc_campus_code     varchar(64)     NULL,
    loc_campus_name     varchar(200)    NULL,
    used_flag           smallint        NOT NULL DEFAULT 0,
    patient_no          varchar(64)     NULL,
    patient_name        varchar(64)     NULL,
    bed_no              varchar(32)     NULL,
    surgeon_id          varchar(36)     NULL,
    surgeon_no          varchar(32)     NULL,
    surgeon_name        varchar(64)     NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_recall_item PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_recall_item IS '召回明细';


-- 缺损登记
CREATE TABLE cssd.cssd_defect (
    id                  varchar(36)     NOT NULL,
    defect_no           varchar(64)     NOT NULL,
    recycle_bill_id     varchar(36)     NULL,
    recycle_bill_no     varchar(64)     NULL,
    recycle_item_id     varchar(36)     NULL,
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    template_id         varchar(36)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    dept_id             varchar(36)     NULL,
    dept_code           varchar(64)     NULL,
    dept_name           varchar(200)    NULL,
    instrument_id       varchar(36)     NULL,
    instrument_code     varchar(64)     NULL,
    instrument_name     varchar(200)    NULL,
    spec                varchar(128)    NULL,
    model               varchar(128)    NULL,
    defect_kind         varchar(32)     NOT NULL,
    qty                 integer         NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_defect PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_defect IS '缺损登记';


-- 一次性物品批次库存
CREATE TABLE cssd.cssd_once_lot (
    id                  varchar(36)     NOT NULL,
    item_id             varchar(36)     NOT NULL,
    item_code           varchar(64)     NOT NULL,
    item_name           varchar(200)    NOT NULL,
    spec                varchar(128)    NULL,
    unit_name           varchar(32)     NULL,
    mfr_id              varchar(36)     NULL,
    mfr_code            varchar(64)     NULL,
    mfr_name            varchar(200)    NULL,
    vendor_id           varchar(36)     NULL,
    vendor_code         varchar(64)     NULL,
    vendor_name         varchar(200)    NULL,
    lot_no              varchar(64)     NOT NULL,
    ster_lot_no         varchar(64)     NULL,
    produce_date        date            NULL,
    expire_date         date            NULL,
    qty_on_hand         integer         NOT NULL DEFAULT 0,
    is_blocked          smallint        NOT NULL DEFAULT 0,
    block_reason        varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_once_lot PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_once_lot IS '一次性物品批次库存';


-- 一次性物品出入库
CREATE TABLE cssd.cssd_once_io (
    id                  varchar(36)     NOT NULL,
    io_no               varchar(64)     NOT NULL,
    io_kind             varchar(32)     NOT NULL,
    io_status           varchar(32)     NOT NULL DEFAULT 'draft',
    lot_id              varchar(36)     NOT NULL,
    item_id             varchar(36)     NOT NULL,
    item_code           varchar(64)     NOT NULL,
    item_name           varchar(200)    NOT NULL,
    spec                varchar(128)    NULL,
    unit_name           varchar(32)     NULL,
    mfr_id              varchar(36)     NULL,
    mfr_code            varchar(64)     NULL,
    mfr_name            varchar(200)    NULL,
    vendor_id           varchar(36)     NULL,
    vendor_code         varchar(64)     NULL,
    vendor_name         varchar(200)    NULL,
    lot_no              varchar(64)     NOT NULL,
    ster_lot_no         varchar(64)     NULL,
    expire_date         date            NULL,
    dept_id             varchar(36)     NULL,
    dept_code           varchar(64)     NULL,
    dept_name           varchar(200)    NULL,
    qty                 integer         NOT NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    io_at               timestamptz     NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_once_io PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_once_io IS '一次性物品出入库';


-- 标签/单据打印流水
CREATE TABLE cssd.cssd_print_log (
    id                  varchar(36)     NOT NULL,
    entity_type         varchar(64)     NOT NULL,
    entity_id           varchar(36)     NOT NULL,
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    label_phase         varchar(16)     NULL,
    template_code       varchar(64)     NOT NULL,
    biz_type            varchar(32)     NULL,
    biz_id              varchar(36)     NULL,
    biz_no              varchar(64)     NULL,
    biz_item_id         varchar(36)     NULL,
    snapshot_code       varchar(64)     NULL,
    snapshot_name       varchar(200)    NULL,
    printer_id          varchar(36)     NULL,
    printer_no          varchar(32)     NULL,
    printer_name        varchar(64)     NULL,
    printed_at          timestamptz     NOT NULL DEFAULT now(),
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_print_log PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_print_log IS '标签/单据打印流水';


-- 主数据变更记录
CREATE TABLE cssd.cssd_entity_change_log (
    id                  varchar(36)     NOT NULL,
    entity_type         varchar(64)     NOT NULL,
    entity_id           varchar(36)     NOT NULL,
    action              varchar(32)     NOT NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    client              varchar(16)     NULL,
    diff_json           jsonb           NULL,
    snapshot_json       jsonb           NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_entity_change_log PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_entity_change_log IS '主数据变更记录';


-- 打印模板（尺寸与版式可配，不写死标签规格）
CREATE TABLE cssd.cssd_print_template (
    id                  varchar(36)     NOT NULL,
    template_code       varchar(64)     NOT NULL,
    template_name       varchar(200)    NOT NULL,
    carrier_kind        varchar(16)     NOT NULL DEFAULT 'qr',
    label_phase         varchar(16)     NOT NULL DEFAULT 'process',
    paper_width_mm      numeric(8,2)    NULL,
    paper_height_mm     numeric(8,2)    NULL,
    layout_json         jsonb           NULL,
    is_active           smallint        NOT NULL DEFAULT 1,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_print_template PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_print_template IS '打印模板（尺寸与版式可配）';


-- RFID 写入绑卡流水（操作繁琐，须独立记录，禁止静默改绑）
CREATE TABLE cssd.cssd_rfid_bind_log (
    id                  varchar(36)     NOT NULL,
    instance_id         varchar(36)     NOT NULL,
    barcode             varchar(64)     NOT NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    rfid_uid            varchar(64)     NOT NULL,
    bind_action         varchar(16)     NOT NULL,
    operator_id         varchar(36)     NULL,
    operator_no         varchar(32)     NULL,
    operator_name       varchar(64)     NULL,
    bound_at            timestamptz     NOT NULL DEFAULT now(),
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_rfid_bind_log PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_rfid_bind_log IS 'RFID 写入绑卡流水';


-- HIS 同步流水（幂等键防重复入账，双方可对账）
CREATE TABLE cssd.cssd_his_sync_log (
    id                  varchar(36)     NOT NULL,
    direction           varchar(8)      NOT NULL,
    biz_type            varchar(32)     NOT NULL,
    idempotency_key     varchar(64)     NOT NULL,
    sync_status         varchar(16)     NOT NULL DEFAULT 'pending',
    instance_id         varchar(36)     NULL,
    barcode             varchar(64)     NULL,
    template_code       varchar(64)     NULL,
    template_name       varchar(200)    NULL,
    patient_no          varchar(64)     NULL,
    patient_name        varchar(64)     NULL,
    his_patient_id      varchar(64)     NULL,
    his_encounter_id    varchar(64)     NULL,
    his_apply_id        varchar(64)     NULL,
    payload_json        jsonb           NULL,
    result_json         jsonb           NULL,
    error_msg           varchar(1000)   NULL,
    synced_at           timestamptz     NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_his_sync_log PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_his_sync_log IS 'HIS 同步流水（幂等、对账）';


-- 院内可调参数（如非植入锅是否等生物：默认严格，上线再改）
CREATE TABLE cssd.cssd_sys_config (
    id                  varchar(36)     NOT NULL,
    config_key          varchar(64)     NOT NULL,
    config_value        varchar(500)    NOT NULL,
    value_label         varchar(200)    NULL,
    remark              varchar(500)    NULL,
    created_at          timestamptz     NOT NULL DEFAULT now(),
    created_by          varchar(36)     NULL,
    created_by_no       varchar(32)     NULL,
    created_by_name     varchar(64)     NULL,
    updated_at          timestamptz     NOT NULL DEFAULT now(),
    updated_by          varchar(36)     NULL,
    updated_by_no       varchar(32)     NULL,
    updated_by_name     varchar(64)     NULL,
    is_deleted          smallint        NOT NULL DEFAULT 0,
    deleted_at          timestamptz     NULL,
    deleted_by          varchar(36)     NULL,
    deleted_by_no       varchar(32)     NULL,
    deleted_by_name     varchar(64)     NULL,
    create_channel      varchar(16)     NULL,
    update_channel      varchar(16)     NULL,
    delete_channel      varchar(16)     NULL,
    CONSTRAINT pk_cssd_sys_config PRIMARY KEY (id)
);
COMMENT ON TABLE cssd.cssd_sys_config IS '系统参数（放行策略等，上线按院调整）';

COMMENT ON SCHEMA cssd IS '消毒供应中心追溯业务库';

-- 列注释（与表结构同批维护）

COMMENT ON COLUMN cssd.cssd_dict.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_dict.dict_type IS '字典类型';
COMMENT ON COLUMN cssd.cssd_dict.dict_code IS '字典码';
COMMENT ON COLUMN cssd.cssd_dict.dict_label IS '中文标签';
COMMENT ON COLUMN cssd.cssd_dict.sort_no IS '排序号';
COMMENT ON COLUMN cssd.cssd_dict.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_dict.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_dict.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_dict.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_dict.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_dict.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_dict.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_dict.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_dict.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_dict.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_dict.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_dict.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_dict.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_dict.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_dict.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_dict.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_dict.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_dict.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_campus.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_campus.campus_code IS '院区编码';
COMMENT ON COLUMN cssd.cssd_campus.campus_name IS '院区名称';
COMMENT ON COLUMN cssd.cssd_campus.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_campus.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_campus.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_campus.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_campus.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_campus.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_campus.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_campus.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_campus.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_campus.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_campus.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_campus.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_campus.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_campus.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_campus.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_campus.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_campus.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_campus.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_campus.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_dept.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_dept.dept_code IS '科室编码';
COMMENT ON COLUMN cssd.cssd_dept.dept_name IS '科室名称';
COMMENT ON COLUMN cssd.cssd_dept.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_dept.campus_id IS '院区 ID';
COMMENT ON COLUMN cssd.cssd_dept.dept_kind IS '科室类别 cssd/or/clinical';
COMMENT ON COLUMN cssd.cssd_dept.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_dept.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_dept.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_dept.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_dept.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_dept.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_dept.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_dept.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_dept.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_dept.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_dept.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_dept.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_dept.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_dept.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_dept.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_dept.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_dept.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_dept.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_staff.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_staff.staff_no IS '工号';
COMMENT ON COLUMN cssd.cssd_staff.staff_name IS '姓名';
COMMENT ON COLUMN cssd.cssd_staff.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_staff.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_staff.staff_role IS '角色 cssd/surgeon/nurse 等';
COMMENT ON COLUMN cssd.cssd_staff.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_staff.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_staff.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_staff.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_staff.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_staff.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_staff.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_staff.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_staff.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_staff.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_staff.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_staff.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_staff.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_staff.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_staff.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_staff.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_staff.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_staff.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_vendor.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_vendor.vendor_code IS '供应商编码快照';
COMMENT ON COLUMN cssd.cssd_vendor.vendor_name IS '供应商名称快照';
COMMENT ON COLUMN cssd.cssd_vendor.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_vendor.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_vendor.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_vendor.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_vendor.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_vendor.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_vendor.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_vendor.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_vendor.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_vendor.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_vendor.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_vendor.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_vendor.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_vendor.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_vendor.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_vendor.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_vendor.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_vendor.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_vendor.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_manufacturer.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_manufacturer.mfr_code IS '厂家编码快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.mfr_name IS '厂家名称快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_manufacturer.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_manufacturer.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_manufacturer.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_manufacturer.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_manufacturer.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_manufacturer.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_manufacturer.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_manufacturer.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_manufacturer.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_manufacturer.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_manufacturer.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_manufacturer.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_manufacturer.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_material.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_pack_material.material_code IS '包装材料编码';
COMMENT ON COLUMN cssd.cssd_pack_material.material_name IS '包装材料名称';
COMMENT ON COLUMN cssd.cssd_pack_material.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_pack_material.shelf_life_days IS '包装有效期天数快照';
COMMENT ON COLUMN cssd.cssd_pack_material.ster_method IS '适用灭菌方式';
COMMENT ON COLUMN cssd.cssd_pack_material.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_pack_material.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_pack_material.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_pack_material.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_pack_material.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_material.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_material.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_pack_material.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_pack_material.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_material.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_material.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_pack_material.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_pack_material.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_pack_material.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_material.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_material.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_pack_material.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_pack_material.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_instrument.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_instrument.instrument_code IS '器械编码快照';
COMMENT ON COLUMN cssd.cssd_instrument.instrument_name IS '器械名称快照';
COMMENT ON COLUMN cssd.cssd_instrument.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_instrument.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_instrument.model IS '型号快照';
COMMENT ON COLUMN cssd.cssd_instrument.register_no IS '注册证号';
COMMENT ON COLUMN cssd.cssd_instrument.barcode IS '器械本体条码（循环复用）';
COMMENT ON COLUMN cssd.cssd_instrument.owner_dept_id IS '所属科室 ID';
COMMENT ON COLUMN cssd.cssd_instrument.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_instrument.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_instrument.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_instrument.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_instrument.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_instrument.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_instrument.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_instrument.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_instrument.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_instrument.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_instrument.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_instrument.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_instrument.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_instrument.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_instrument.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_instrument.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_instrument.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_instrument.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_template.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_pack_template.template_code IS '包模板编码';
COMMENT ON COLUMN cssd.cssd_pack_template.template_name IS '包模板名称';
COMMENT ON COLUMN cssd.cssd_pack_template.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_pack_template.pack_category IS '包类别码';
COMMENT ON COLUMN cssd.cssd_pack_template.owner_dept_id IS '所属科室 ID';
COMMENT ON COLUMN cssd.cssd_pack_template.owner_dept_code IS '所属科室编码快照';
COMMENT ON COLUMN cssd.cssd_pack_template.owner_dept_name IS '所属科室名称快照';
COMMENT ON COLUMN cssd.cssd_pack_template.material_id IS '包装材料 ID';
COMMENT ON COLUMN cssd.cssd_pack_template.material_code IS '包装材料编码';
COMMENT ON COLUMN cssd.cssd_pack_template.material_name IS '包装材料名称';
COMMENT ON COLUMN cssd.cssd_pack_template.shelf_life_days IS '包装有效期天数快照';
COMMENT ON COLUMN cssd.cssd_pack_template.basket_load_qty IS '一篮最大装载数（辅助提示，非硬拦）';
COMMENT ON COLUMN cssd.cssd_pack_template.safety_stock_qty IS '该模板可用无菌包安全库存；空则用系统参数';
COMMENT ON COLUMN cssd.cssd_pack_template.need_wash IS '是否需要清洗：1 是';
COMMENT ON COLUMN cssd.cssd_pack_template.wash_mode IS '清洗模式 scatter/whole_basket';
COMMENT ON COLUMN cssd.cssd_pack_template.need_pack IS '是否需要配包核包：1 是';
COMMENT ON COLUMN cssd.cssd_pack_template.need_store IS '灭菌后是否入库：1 是';
COMMENT ON COLUMN cssd.cssd_pack_template.need_recycle IS '使用后是否回收：1 是';
COMMENT ON COLUMN cssd.cssd_pack_template.is_implant IS '是否植入物包：1 是';
COMMENT ON COLUMN cssd.cssd_pack_template.image_url IS '配包图谱 URL';
COMMENT ON COLUMN cssd.cssd_pack_template.check_image_url IS '核包图谱 URL';
COMMENT ON COLUMN cssd.cssd_pack_template.ster_method IS '灭菌方式 high/low，装锅须与设备 ster_temp_class 匹配';
COMMENT ON COLUMN cssd.cssd_pack_template.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_pack_template.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_pack_template.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_pack_template.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_pack_template.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_pack_template.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_pack_template.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_pack_template.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_pack_template.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_template_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_pack_template_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_pack_template_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_pack_template_item.instrument_id IS '器械 ID';
COMMENT ON COLUMN cssd.cssd_pack_template_item.instrument_code IS '器械编码快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.instrument_name IS '器械名称快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.model IS '型号快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.qty IS '数量';
COMMENT ON COLUMN cssd.cssd_pack_template_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_pack_template_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_pack_template_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_pack_template_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_pack_template_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_pack_template_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_template_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_pack_template_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_pack_template_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_device.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_device.device_code IS '设备编码';
COMMENT ON COLUMN cssd.cssd_device.device_name IS '设备名称';
COMMENT ON COLUMN cssd.cssd_device.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_device.device_kind IS '设备类型 washer/sterilizer';
COMMENT ON COLUMN cssd.cssd_device.device_no IS '设备编号/锅号（包外标识用）';
COMMENT ON COLUMN cssd.cssd_device.model IS '型号快照';
COMMENT ON COLUMN cssd.cssd_device.ster_temp_class IS '温度类别 high/low';
COMMENT ON COLUMN cssd.cssd_device.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_device.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_device.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_device.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_device.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_device.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_device.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_device.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_device.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_device.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_device.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_device.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_device.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_device.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_device.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_device.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_device.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_device.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_device_program.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_device_program.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_device_program.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_device_program.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_device_program.program_code IS '程序编码快照';
COMMENT ON COLUMN cssd.cssd_device_program.program_name IS '程序名称快照';
COMMENT ON COLUMN cssd.cssd_device_program.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_device_program.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_device_program.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_device_program.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_device_program.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_device_program.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_device_program.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_device_program.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_device_program.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_device_program.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_device_program.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_device_program.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_device_program.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_device_program.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_device_program.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_device_program.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_device_program.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_device_program.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_device_program.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_basket.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_basket.basket_code IS '篮筐编码';
COMMENT ON COLUMN cssd.cssd_basket.basket_name IS '篮筐名称';
COMMENT ON COLUMN cssd.cssd_basket.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_basket.basket_kind IS '篮筐类型 wash/load/rack';
COMMENT ON COLUMN cssd.cssd_basket.color_code IS '篮筐色谱码';
COMMENT ON COLUMN cssd.cssd_basket.basket_status IS '筐状态 idle 空闲 / pending_wash 待清洗 / washing 清洗中';
COMMENT ON COLUMN cssd.cssd_basket.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_basket.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_basket.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_basket.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_basket.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_basket.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_basket.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_basket.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_basket.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_basket.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_basket.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_basket.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_basket.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_basket.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_basket.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_basket.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_basket.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_basket.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_cart.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_cart.cart_code IS '下送车编码';
COMMENT ON COLUMN cssd.cssd_cart.cart_name IS '下送车名称';
COMMENT ON COLUMN cssd.cssd_cart.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_cart.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_cart.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_cart.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_cart.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_cart.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_cart.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_cart.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_cart.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_cart.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_cart.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_cart.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_cart.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_cart.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_cart.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_cart.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_cart.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_cart.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_cart.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_once_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_once_item.item_code IS '品规编码';
COMMENT ON COLUMN cssd.cssd_once_item.item_name IS '品规名称';
COMMENT ON COLUMN cssd.cssd_once_item.name_pinyin IS '名称拼音简码（主数据检索，不摊到单据）';
COMMENT ON COLUMN cssd.cssd_once_item.spec IS '规格';
COMMENT ON COLUMN cssd.cssd_once_item.unit_name IS '单位快照';
COMMENT ON COLUMN cssd.cssd_once_item.mfr_id IS '生产厂家 ID';
COMMENT ON COLUMN cssd.cssd_once_item.mfr_code IS '厂家编码';
COMMENT ON COLUMN cssd.cssd_once_item.mfr_name IS '厂家名称';
COMMENT ON COLUMN cssd.cssd_once_item.vendor_id IS '供应商 ID';
COMMENT ON COLUMN cssd.cssd_once_item.vendor_code IS '供应商编码';
COMMENT ON COLUMN cssd.cssd_once_item.vendor_name IS '供应商名称';
COMMENT ON COLUMN cssd.cssd_once_item.min_stock IS '最低库存告警数';
COMMENT ON COLUMN cssd.cssd_once_item.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_once_item.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_once_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_once_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_once_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_once_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_once_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_once_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_once_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_once_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_once_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_once_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_once_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_once_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_once_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_instance.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_pack_instance.barcode IS '本循环包实例条码（不复用）';
COMMENT ON COLUMN cssd.cssd_pack_instance.label_carrier IS '标识载体：barcode_1d/qr/rfid';
COMMENT ON COLUMN cssd.cssd_pack_instance.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.pack_category IS '包类别码';
COMMENT ON COLUMN cssd.cssd_pack_instance.status IS '包实例状态码';
COMMENT ON COLUMN cssd.cssd_pack_instance.material_id IS '包装材料 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.material_code IS '包装材料编码';
COMMENT ON COLUMN cssd.cssd_pack_instance.material_name IS '包装材料名称';
COMMENT ON COLUMN cssd.cssd_pack_instance.shelf_life_days IS '包装有效期天数快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.is_implant IS '是否植入物包：1 是';
COMMENT ON COLUMN cssd.cssd_pack_instance.packer_id IS '配包人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.packer_no IS '配包人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.packer_name IS '配包人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.packed_at IS '配包时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.checker_id IS '核包/检测/核准人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.checker_no IS '核包/检测/核准人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.checker_name IS '核包/检测/核准人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.checked_at IS '核包/检测/核准时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.wash_batch_id IS '清洗批次 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.wash_batch_no IS '清洗批次号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.ster_load_id IS '灭菌锅次 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.ster_load_no IS '灭菌批次号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.device_no IS '设备编号/锅号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.program_id IS '程序 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.program_code IS '程序编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.program_name IS '程序名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.sterilized_at IS '灭菌完成时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.sterilize_date IS '灭菌日期';
COMMENT ON COLUMN cssd.cssd_pack_instance.expire_date IS '失效日期';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_dept_id IS '当前所在科室 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_dept_code IS '当前所在科室编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_dept_name IS '当前所在科室名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.dist_bill_id IS '发放单 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.dist_bill_no IS '发放单号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.patient_no IS '患者住院号/病案号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.patient_name IS '患者姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.bed_no IS '当时床号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.surgeon_id IS '术者 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.surgeon_no IS '术者工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.surgeon_name IS '术者姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.surgery_code IS '手术编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.surgery_name IS '手术名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.ext_vendor_id IS '外来器械厂家 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.ext_vendor_code IS '外来器械厂家编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.ext_vendor_name IS '外来器械厂家名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.prev_instance_id IS '上一循环包实例 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.loan_staff_id IS '寄消人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.loan_staff_no IS '寄消人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.loan_staff_name IS '寄消人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.rfid_uid IS 'RFID 芯片 UID（写入后）';
COMMENT ON COLUMN cssd.cssd_pack_instance.rfid_bound_at IS 'RFID 绑卡时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.rfid_binder_id IS '绑卡人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.rfid_binder_no IS '绑卡人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.rfid_binder_name IS '绑卡人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.his_patient_id IS 'HIS 患者 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.his_encounter_id IS 'HIS 就诊/手术 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_pack_instance.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_pack_instance.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_pack_instance.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_pack_instance.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_instance_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.barcode IS '所属包实例条码冗余';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.instrument_id IS '器械 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.instrument_code IS '器械编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.instrument_name IS '器械名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.model IS '型号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.qty IS '数量';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.actual_qty IS '实点数量';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_recycle_bill.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_recycle_bill.bill_no IS '回收单号';
COMMENT ON COLUMN cssd.cssd_recycle_bill.bill_status IS '单据状态码';
COMMENT ON COLUMN cssd.cssd_recycle_bill.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.recyc_at IS '回收时间';
COMMENT ON COLUMN cssd.cssd_recycle_bill.recyc_staff_id IS '回收人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.recyc_staff_no IS '回收人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.recyc_staff_name IS '回收人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.source_kind IS '来源 pda / dept_dropoff / web';
COMMENT ON COLUMN cssd.cssd_recycle_bill.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_recycle_bill.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_recycle_bill.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_recycle_bill.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_recycle_bill.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_recycle_bill.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_recycle_bill.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_recycle_bill.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_recycle_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_recycle_item.bill_id IS '主表 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.bill_no IS '主表单号冗余';
COMMENT ON COLUMN cssd.cssd_recycle_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_recycle_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_recycle_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.apply_item_id IS '申领明细 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.qty IS '本行回收数量（打散批次账本的总数）';
COMMENT ON COLUMN cssd.cssd_recycle_item.packed_qty IS '已配包数量；剩余 = qty - packed_qty；允许部分配包';
COMMENT ON COLUMN cssd.cssd_recycle_item.urgent_flag IS '加急：1 是';
COMMENT ON COLUMN cssd.cssd_recycle_item.contamination_flag IS '明显污染：1 是';
COMMENT ON COLUMN cssd.cssd_recycle_item.infection_type IS '特殊感染类型码，可空';
COMMENT ON COLUMN cssd.cssd_recycle_item.basket_id IS '入筐 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.basket_code IS '筐编码快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.basket_name IS '筐名称快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.has_defect IS '本行是否缺损：1 是';
COMMENT ON COLUMN cssd.cssd_recycle_item.defect_remark IS '缺损说明';
COMMENT ON COLUMN cssd.cssd_recycle_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_recycle_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_recycle_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_recycle_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_recycle_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_recycle_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_recycle_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_wash_batch.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_wash_batch.batch_no IS '清洗批次号';
COMMENT ON COLUMN cssd.cssd_wash_batch.wash_mode IS '实际清洗模式 scatter/whole_basket（可临时覆盖）';
COMMENT ON COLUMN cssd.cssd_wash_batch.template_wash_mode IS '模板原清洗模式，覆盖前快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.mode_overridden IS '是否临时覆盖清洗方式：1 是';
COMMENT ON COLUMN cssd.cssd_wash_batch.override_reason IS '覆盖原因';
COMMENT ON COLUMN cssd.cssd_wash_batch.batch_status IS 'draft / running / finished / voided';
COMMENT ON COLUMN cssd.cssd_wash_batch.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.device_no IS '设备编号/锅号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.program_id IS '程序 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.program_code IS '程序编码快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.program_name IS '程序名称快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.started_at IS '开始时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.finished_at IS '结束时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.check_result IS '清洗检测结果码';
COMMENT ON COLUMN cssd.cssd_wash_batch.checker_id IS '核包/检测/核准人 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.checker_no IS '核包/检测/核准人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.checker_name IS '核包/检测/核准人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.checked_at IS '核包/检测/核准时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.report_url IS '设备报告或影像 URL';
COMMENT ON COLUMN cssd.cssd_wash_batch.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_wash_batch.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_wash_batch.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_wash_batch.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_wash_batch.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_batch.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_wash_batch.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_wash_batch.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_wash_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_wash_item.batch_id IS '清洗批次 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.batch_no IS '清洗批次号冗余';
COMMENT ON COLUMN cssd.cssd_wash_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_wash_item.basket_id IS '篮筐 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.basket_code IS '篮筐编码快照';
COMMENT ON COLUMN cssd.cssd_wash_item.basket_name IS '篮筐名称快照';
COMMENT ON COLUMN cssd.cssd_wash_item.recycle_bill_id IS '回收单 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.recycle_bill_no IS '回收单号快照';
COMMENT ON COLUMN cssd.cssd_wash_item.recycle_item_id IS '回收明细 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_wash_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_wash_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_wash_item.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_wash_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_wash_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_wash_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_wash_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_wash_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_wash_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_wash_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_wash_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_wash_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_wash_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_ster_load.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_ster_load.load_no IS '灭菌批次号（锅次）';
COMMENT ON COLUMN cssd.cssd_ster_load.load_status IS '锅次状态码';
COMMENT ON COLUMN cssd.cssd_ster_load.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_ster_load.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_ster_load.device_no IS '设备编号/锅号快照';
COMMENT ON COLUMN cssd.cssd_ster_load.program_id IS '程序 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.program_code IS '程序编码快照';
COMMENT ON COLUMN cssd.cssd_ster_load.program_name IS '程序名称快照';
COMMENT ON COLUMN cssd.cssd_ster_load.ster_temp_class IS '温度类别 high/low';
COMMENT ON COLUMN cssd.cssd_ster_load.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load.started_at IS '开始时间';
COMMENT ON COLUMN cssd.cssd_ster_load.finished_at IS '结束时间';
COMMENT ON COLUMN cssd.cssd_ster_load.sterilize_date IS '灭菌日期';
COMMENT ON COLUMN cssd.cssd_ster_load.params_json IS '运行参数 JSON';
COMMENT ON COLUMN cssd.cssd_ster_load.report_url IS '设备报告或影像 URL';
COMMENT ON COLUMN cssd.cssd_ster_load.has_implant IS '本锅是否含植入物：1 是';
COMMENT ON COLUMN cssd.cssd_ster_load.bd_result IS 'BD 结果码';
COMMENT ON COLUMN cssd.cssd_ster_load.pcd_result IS 'PCD/批量监测结果码';
COMMENT ON COLUMN cssd.cssd_ster_load.bio_result IS '生物监测结果码';
COMMENT ON COLUMN cssd.cssd_ster_load.chem_result IS '化学监测结果码（与 monitor_kind=chem 回写）';
COMMENT ON COLUMN cssd.cssd_ster_load.release_status IS '放行状态 pending/released/rejected';
COMMENT ON COLUMN cssd.cssd_ster_load.released_at IS '放行时间';
COMMENT ON COLUMN cssd.cssd_ster_load.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_ster_load.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_ster_load.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_ster_load.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_ster_load.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_ster_load.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_ster_load.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_ster_load.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_ster_load_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_ster_load_item.load_id IS '灭菌锅次 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.load_no IS '灭菌批次号冗余';
COMMENT ON COLUMN cssd.cssd_ster_load_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_ster_load_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_ster_load_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.pack_category IS '包类别码';
COMMENT ON COLUMN cssd.cssd_ster_load_item.is_implant IS '是否植入物包：1 是';
COMMENT ON COLUMN cssd.cssd_ster_load_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_ster_load_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_ster_load_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_ster_load_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_ster_load_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_ster_load_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_ster_load_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_ster_load_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_monitor.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_monitor.monitor_no IS '监测单号';
COMMENT ON COLUMN cssd.cssd_monitor.monitor_kind IS 'bd/pcd/bio/chem；chem 为化学监测，每锅人工登记';
COMMENT ON COLUMN cssd.cssd_monitor.monitor_status IS '监测单状态码';
COMMENT ON COLUMN cssd.cssd_monitor.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_monitor.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_monitor.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_monitor.device_no IS '设备编号/锅号快照';
COMMENT ON COLUMN cssd.cssd_monitor.load_id IS '灭菌锅次 ID';
COMMENT ON COLUMN cssd.cssd_monitor.load_no IS '灭菌批次号冗余';
COMMENT ON COLUMN cssd.cssd_monitor.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_monitor.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_monitor.monitor_date IS '监测日期';
COMMENT ON COLUMN cssd.cssd_monitor.result_code IS '监测结果码';
COMMENT ON COLUMN cssd.cssd_monitor.result_at IS '出结果时间';
COMMENT ON COLUMN cssd.cssd_monitor.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_monitor.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_monitor.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_monitor.report_url IS '设备报告或影像 URL';
COMMENT ON COLUMN cssd.cssd_monitor.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_monitor.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_monitor.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_monitor.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_monitor.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_monitor.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_monitor.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_monitor.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_monitor.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_monitor.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_monitor.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_monitor.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_monitor.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_monitor.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_monitor.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_monitor.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_monitor.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_apply_bill.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_apply_bill.bill_no IS '申领单号';
COMMENT ON COLUMN cssd.cssd_apply_bill.bill_status IS '单据状态码';
COMMENT ON COLUMN cssd.cssd_apply_bill.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.apply_type IS '申领类型 dept/surgery';
COMMENT ON COLUMN cssd.cssd_apply_bill.surgery_code IS '手术编码快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.surgery_name IS '手术名称快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.patient_no IS '患者住院号/病案号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.patient_name IS '患者姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.bed_no IS '当时床号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.his_patient_id IS 'HIS 患者 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.his_encounter_id IS 'HIS 就诊/手术 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.his_apply_id IS 'HIS 申领/手术申请 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.need_at IS '需求时间';
COMMENT ON COLUMN cssd.cssd_apply_bill.applicant_id IS '申领人 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.applicant_no IS '申领人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.applicant_name IS '申领人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.submitted_at IS '提交时间';
COMMENT ON COLUMN cssd.cssd_apply_bill.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_apply_bill.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_apply_bill.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_apply_bill.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_apply_bill.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_apply_bill.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_apply_bill.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_apply_bill.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_apply_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_apply_item.bill_id IS '主表 ID';
COMMENT ON COLUMN cssd.cssd_apply_item.bill_no IS '主表单号冗余';
COMMENT ON COLUMN cssd.cssd_apply_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_apply_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_apply_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_apply_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_apply_item.qty IS '数量';
COMMENT ON COLUMN cssd.cssd_apply_item.dist_qty IS '已发放数量';
COMMENT ON COLUMN cssd.cssd_apply_item.recycle_qty IS '已回收数量';
COMMENT ON COLUMN cssd.cssd_apply_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_apply_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_apply_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_apply_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_apply_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_apply_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_apply_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_apply_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_apply_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_apply_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_apply_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_apply_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_dist_bill.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_dist_bill.bill_no IS '发放单号';
COMMENT ON COLUMN cssd.cssd_dist_bill.bill_status IS '单据状态码';
COMMENT ON COLUMN cssd.cssd_dist_bill.dist_mode IS '发放模式 direct/cart';
COMMENT ON COLUMN cssd.cssd_dist_bill.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.cart_id IS '下送车 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.cart_code IS '下送车编码快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.cart_name IS '下送车名称快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.dist_staff_id IS '发放人 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.dist_staff_no IS '发放人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.dist_staff_name IS '发放人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.dist_at IS '发放时间';
COMMENT ON COLUMN cssd.cssd_dist_bill.receiver_id IS '接收人 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.receiver_no IS '接收人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.receiver_name IS '接收人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.received_at IS '接收时间';
COMMENT ON COLUMN cssd.cssd_dist_bill.receive_place IS '接收地点';
COMMENT ON COLUMN cssd.cssd_dist_bill.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_dist_bill.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_dist_bill.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_dist_bill.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_dist_bill.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_dist_bill.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_dist_bill.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_dist_bill.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_dist_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_dist_item.bill_id IS '主表 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.bill_no IS '主表单号冗余';
COMMENT ON COLUMN cssd.cssd_dist_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_dist_item.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_dist_item.apply_item_id IS '申领明细 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_dist_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_dist_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_dist_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_dist_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_dist_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_dist_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_dist_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_dist_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_dist_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_dist_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_dist_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_dist_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_use_check.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_use_check.check_no IS '核准单号';
COMMENT ON COLUMN cssd.cssd_use_check.check_kind IS 'pre 术前 / post 术后';
COMMENT ON COLUMN cssd.cssd_use_check.check_status IS '核准单状态码';
COMMENT ON COLUMN cssd.cssd_use_check.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_use_check.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_use_check.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_use_check.apply_bill_id IS '申领单 ID';
COMMENT ON COLUMN cssd.cssd_use_check.apply_bill_no IS '申领单号快照';
COMMENT ON COLUMN cssd.cssd_use_check.patient_no IS '患者住院号/病案号快照';
COMMENT ON COLUMN cssd.cssd_use_check.patient_name IS '患者姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.bed_no IS '当时床号快照';
COMMENT ON COLUMN cssd.cssd_use_check.surgeon_id IS '术者 ID';
COMMENT ON COLUMN cssd.cssd_use_check.surgeon_no IS '术者工号快照';
COMMENT ON COLUMN cssd.cssd_use_check.surgeon_name IS '术者姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.surgery_code IS '手术编码快照';
COMMENT ON COLUMN cssd.cssd_use_check.surgery_name IS '手术名称快照';
COMMENT ON COLUMN cssd.cssd_use_check.his_patient_id IS 'HIS 患者 ID';
COMMENT ON COLUMN cssd.cssd_use_check.his_encounter_id IS 'HIS 就诊/手术 ID';
COMMENT ON COLUMN cssd.cssd_use_check.idempotency_key IS '幂等键，防重复入账';
COMMENT ON COLUMN cssd.cssd_use_check.checker_id IS '核包/检测/核准人 ID';
COMMENT ON COLUMN cssd.cssd_use_check.checker_no IS '核包/检测/核准人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check.checker_name IS '核包/检测/核准人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.checked_at IS '核包/检测/核准时间';
COMMENT ON COLUMN cssd.cssd_use_check.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_use_check.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_use_check.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_use_check.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_use_check.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_use_check.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_use_check.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_use_check.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_use_check.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_use_check.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_use_check.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_use_check_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_use_check_item.check_id IS '核准单 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.check_no IS '核准单号';
COMMENT ON COLUMN cssd.cssd_use_check_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_use_check_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_use_check_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.qty_ok IS '数量是否相符：1 是';
COMMENT ON COLUMN cssd.cssd_use_check_item.ster_ok IS '灭菌标识是否合格：1 是';
COMMENT ON COLUMN cssd.cssd_use_check_item.item_qty IS '术后清点数量';
COMMENT ON COLUMN cssd.cssd_use_check_item.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_use_check_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_use_check_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_use_check_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_use_check_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_use_check_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_use_check_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_use_check_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_use_check_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_recall.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_recall.recall_no IS '召回单号';
COMMENT ON COLUMN cssd.cssd_recall.recall_status IS '召回单状态码';
COMMENT ON COLUMN cssd.cssd_recall.recall_reason IS '召回原因码';
COMMENT ON COLUMN cssd.cssd_recall.device_id IS '设备 ID';
COMMENT ON COLUMN cssd.cssd_recall.device_code IS '设备编码快照';
COMMENT ON COLUMN cssd.cssd_recall.device_name IS '设备名称快照';
COMMENT ON COLUMN cssd.cssd_recall.device_no IS '设备编号/锅号快照';
COMMENT ON COLUMN cssd.cssd_recall.load_id IS '灭菌锅次 ID';
COMMENT ON COLUMN cssd.cssd_recall.load_no IS '灭菌批次号冗余';
COMMENT ON COLUMN cssd.cssd_recall.monitor_id IS '监测记录 ID';
COMMENT ON COLUMN cssd.cssd_recall.since_time IS '召回起始时间（上次合格之后）';
COMMENT ON COLUMN cssd.cssd_recall.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_recall.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_recall.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall.recalled_at IS '召回执行时间';
COMMENT ON COLUMN cssd.cssd_recall.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_recall.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_recall.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_recall.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_recall.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_recall.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_recall.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_recall.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_recall.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_recall.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_recall.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_recall.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_recall.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_recall.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_recall_item.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_recall_item.recall_id IS '召回单 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.recall_no IS '召回单号';
COMMENT ON COLUMN cssd.cssd_recall_item.line_no IS '行号';
COMMENT ON COLUMN cssd.cssd_recall_item.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_dept_id IS '当前所在科室 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_dept_code IS '当前所在科室编码快照';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_dept_name IS '当前所在科室名称快照';
COMMENT ON COLUMN cssd.cssd_recall_item.used_flag IS '是否已使用：1 是';
COMMENT ON COLUMN cssd.cssd_recall_item.patient_no IS '患者住院号/病案号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.patient_name IS '患者姓名快照';
COMMENT ON COLUMN cssd.cssd_recall_item.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_recall_item.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall_item.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_recall_item.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall_item.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_recall_item.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_recall_item.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_recall_item.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_recall_item.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_recall_item.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_defect.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_defect.defect_no IS '缺损单号';
COMMENT ON COLUMN cssd.cssd_defect.recycle_bill_id IS '回收单 ID';
COMMENT ON COLUMN cssd.cssd_defect.recycle_bill_no IS '回收单号快照';
COMMENT ON COLUMN cssd.cssd_defect.recycle_item_id IS '回收明细 ID';
COMMENT ON COLUMN cssd.cssd_defect.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_defect.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_defect.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_defect.template_code IS '模板编码快照';
COMMENT ON COLUMN cssd.cssd_defect.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_defect.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_defect.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_defect.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_defect.instrument_id IS '器械 ID';
COMMENT ON COLUMN cssd.cssd_defect.instrument_code IS '器械编码快照';
COMMENT ON COLUMN cssd.cssd_defect.instrument_name IS '器械名称快照';
COMMENT ON COLUMN cssd.cssd_defect.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_defect.model IS '型号快照';
COMMENT ON COLUMN cssd.cssd_defect.defect_kind IS '缺损类型码';
COMMENT ON COLUMN cssd.cssd_defect.qty IS '数量';
COMMENT ON COLUMN cssd.cssd_defect.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_defect.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_defect.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_defect.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_defect.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_defect.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_defect.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_defect.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_defect.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_defect.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_defect.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_defect.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_defect.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_defect.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_defect.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_defect.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_defect.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_once_lot.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_once_lot.item_id IS '一次性品规 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.item_code IS '品规编码';
COMMENT ON COLUMN cssd.cssd_once_lot.item_name IS '品规名称';
COMMENT ON COLUMN cssd.cssd_once_lot.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_once_lot.unit_name IS '单位快照';
COMMENT ON COLUMN cssd.cssd_once_lot.mfr_id IS '生产厂家 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.mfr_code IS '厂家编码快照';
COMMENT ON COLUMN cssd.cssd_once_lot.mfr_name IS '厂家名称快照';
COMMENT ON COLUMN cssd.cssd_once_lot.lot_no IS '生产批号';
COMMENT ON COLUMN cssd.cssd_once_lot.ster_lot_no IS '灭菌批号';
COMMENT ON COLUMN cssd.cssd_once_lot.produce_date IS '生产日期';
COMMENT ON COLUMN cssd.cssd_once_lot.expire_date IS '失效日期';
COMMENT ON COLUMN cssd.cssd_once_lot.qty_on_hand IS '当前库存数量';
COMMENT ON COLUMN cssd.cssd_once_lot.is_blocked IS '批次禁用：1 禁用';
COMMENT ON COLUMN cssd.cssd_once_lot.block_reason IS '禁用原因';
COMMENT ON COLUMN cssd.cssd_once_lot.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_once_lot.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_once_lot.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_lot.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_once_lot.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_once_lot.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_lot.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_once_lot.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_once_lot.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_once_lot.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_lot.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_once_lot.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_once_lot.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_once_io.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_once_io.io_no IS '出入库单号';
COMMENT ON COLUMN cssd.cssd_once_io.io_kind IS 'in 入库 / out 出库';
COMMENT ON COLUMN cssd.cssd_once_io.io_status IS '出入库状态码';
COMMENT ON COLUMN cssd.cssd_once_io.lot_id IS '批次库存 ID';
COMMENT ON COLUMN cssd.cssd_once_io.item_id IS '一次性品规 ID';
COMMENT ON COLUMN cssd.cssd_once_io.item_code IS '品规编码';
COMMENT ON COLUMN cssd.cssd_once_io.item_name IS '品规名称';
COMMENT ON COLUMN cssd.cssd_once_io.spec IS '规格快照';
COMMENT ON COLUMN cssd.cssd_once_io.unit_name IS '单位快照';
COMMENT ON COLUMN cssd.cssd_once_io.mfr_id IS '生产厂家 ID';
COMMENT ON COLUMN cssd.cssd_once_io.mfr_code IS '厂家编码快照';
COMMENT ON COLUMN cssd.cssd_once_io.mfr_name IS '厂家名称快照';
COMMENT ON COLUMN cssd.cssd_once_io.lot_no IS '生产批号';
COMMENT ON COLUMN cssd.cssd_once_io.dept_id IS '科室 ID';
COMMENT ON COLUMN cssd.cssd_once_io.dept_code IS '科室编码快照';
COMMENT ON COLUMN cssd.cssd_once_io.dept_name IS '科室名称快照';
COMMENT ON COLUMN cssd.cssd_once_io.qty IS '数量';
COMMENT ON COLUMN cssd.cssd_once_io.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_once_io.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_once_io.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_io.io_at IS '出入库时间';
COMMENT ON COLUMN cssd.cssd_once_io.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_once_io.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_once_io.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_once_io.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_once_io.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_io.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_once_io.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_once_io.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_once_io.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_io.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_once_io.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_once_io.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_once_io.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_once_io.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_once_io.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_once_io.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_once_io.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_print_log.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_print_log.entity_type IS '实体类型';
COMMENT ON COLUMN cssd.cssd_print_log.entity_id IS '实体 ID';
COMMENT ON COLUMN cssd.cssd_print_log.template_code IS '所用打印模板编码';
COMMENT ON COLUMN cssd.cssd_print_log.biz_type IS '业务类型码';
COMMENT ON COLUMN cssd.cssd_print_log.biz_id IS '业务单据 ID';
COMMENT ON COLUMN cssd.cssd_print_log.biz_no IS '业务单号';
COMMENT ON COLUMN cssd.cssd_print_log.biz_item_id IS '业务明细 ID';
COMMENT ON COLUMN cssd.cssd_print_log.snapshot_code IS '打印时对象编码快照';
COMMENT ON COLUMN cssd.cssd_print_log.snapshot_name IS '打印时对象名称快照';
COMMENT ON COLUMN cssd.cssd_print_log.printer_id IS '打印人 ID';
COMMENT ON COLUMN cssd.cssd_print_log.printer_no IS '打印人工号快照';
COMMENT ON COLUMN cssd.cssd_print_log.printer_name IS '打印人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_log.printed_at IS '打印时间';
COMMENT ON COLUMN cssd.cssd_print_log.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_print_log.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_print_log.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_print_log.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_log.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_print_log.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_print_log.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_print_log.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_log.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_print_log.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_print_log.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_print_log.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_print_log.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_log.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_print_log.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_print_log.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_entity_change_log.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_entity_change_log.entity_type IS '实体类型';
COMMENT ON COLUMN cssd.cssd_entity_change_log.entity_id IS '实体 ID';
COMMENT ON COLUMN cssd.cssd_entity_change_log.action IS '动作 create/update/delete 等';
COMMENT ON COLUMN cssd.cssd_entity_change_log.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_entity_change_log.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.client IS '操作端 web/app/mp';
COMMENT ON COLUMN cssd.cssd_entity_change_log.diff_json IS '字段级变更 diff';
COMMENT ON COLUMN cssd.cssd_entity_change_log.snapshot_json IS '精简快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_entity_change_log.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_entity_change_log.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_entity_change_log.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_entity_change_log.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_entity_change_log.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_entity_change_log.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_entity_change_log.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_entity_change_log.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_entity_change_log.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_entity_change_log.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_print_template.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_print_template.template_code IS '打印模板编码';
COMMENT ON COLUMN cssd.cssd_print_template.template_name IS '打印模板名称';
COMMENT ON COLUMN cssd.cssd_print_template.carrier_kind IS '适用载体 qr/rfid/barcode_1d';
COMMENT ON COLUMN cssd.cssd_print_template.paper_width_mm IS '纸宽毫米（可空，上线再配）';
COMMENT ON COLUMN cssd.cssd_print_template.paper_height_mm IS '纸高毫米（可空，上线再配）';
COMMENT ON COLUMN cssd.cssd_print_template.layout_json IS '打印版式 JSON';
COMMENT ON COLUMN cssd.cssd_print_template.is_active IS '启用：1 启用 / 0 停用';
COMMENT ON COLUMN cssd.cssd_print_template.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_print_template.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_print_template.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_print_template.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_print_template.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_template.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_print_template.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_print_template.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_print_template.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_template.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_print_template.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_print_template.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_print_template.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_print_template.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_print_template.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_print_template.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_print_template.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_rfid_bind_log.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.rfid_uid IS 'RFID 芯片 UID（写入后）';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.bind_action IS 'print_write/bind/unbind/rewrite；Web 打印写入用 print_write';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.operator_id IS '操作人 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.operator_no IS '操作人工号快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.operator_name IS '操作人姓名快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.bound_at IS '绑卡时间';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_his_sync_log.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_his_sync_log.direction IS 'in 从 HIS / out 向 HIS';
COMMENT ON COLUMN cssd.cssd_his_sync_log.biz_type IS '业务类型码';
COMMENT ON COLUMN cssd.cssd_his_sync_log.idempotency_key IS '幂等键，防重复入账';
COMMENT ON COLUMN cssd.cssd_his_sync_log.sync_status IS 'pending/ok/fail';
COMMENT ON COLUMN cssd.cssd_his_sync_log.instance_id IS '包实例 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.barcode IS '条码';
COMMENT ON COLUMN cssd.cssd_his_sync_log.his_patient_id IS 'HIS 患者 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.his_encounter_id IS 'HIS 就诊/手术 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.his_apply_id IS 'HIS 申领/手术申请 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.payload_json IS '请求报文';
COMMENT ON COLUMN cssd.cssd_his_sync_log.result_json IS '响应报文';
COMMENT ON COLUMN cssd.cssd_his_sync_log.error_msg IS '失败原因';
COMMENT ON COLUMN cssd.cssd_his_sync_log.synced_at IS '同步完成时间';
COMMENT ON COLUMN cssd.cssd_his_sync_log.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_his_sync_log.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_his_sync_log.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_his_sync_log.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_his_sync_log.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_his_sync_log.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_his_sync_log.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_his_sync_log.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_sys_config.id IS '主键 UUID7';
COMMENT ON COLUMN cssd.cssd_sys_config.config_key IS '参数键';
COMMENT ON COLUMN cssd.cssd_sys_config.config_value IS '参数值';
COMMENT ON COLUMN cssd.cssd_sys_config.value_label IS '参数中文说明';
COMMENT ON COLUMN cssd.cssd_sys_config.remark IS '备注';
COMMENT ON COLUMN cssd.cssd_sys_config.created_at IS '创建时间';
COMMENT ON COLUMN cssd.cssd_sys_config.created_by IS '创建人 ID';
COMMENT ON COLUMN cssd.cssd_sys_config.created_by_no IS '创建人工号快照';
COMMENT ON COLUMN cssd.cssd_sys_config.created_by_name IS '创建人姓名快照';
COMMENT ON COLUMN cssd.cssd_sys_config.updated_at IS '更新时间';
COMMENT ON COLUMN cssd.cssd_sys_config.updated_by IS '更新人 ID';
COMMENT ON COLUMN cssd.cssd_sys_config.updated_by_no IS '更新人工号快照';
COMMENT ON COLUMN cssd.cssd_sys_config.updated_by_name IS '更新人姓名快照';
COMMENT ON COLUMN cssd.cssd_sys_config.is_deleted IS '软删：0 未删 / 1 已删';
COMMENT ON COLUMN cssd.cssd_sys_config.deleted_at IS '删除时间';
COMMENT ON COLUMN cssd.cssd_sys_config.deleted_by IS '删除人 ID';
COMMENT ON COLUMN cssd.cssd_sys_config.deleted_by_no IS '删除人工号快照';
COMMENT ON COLUMN cssd.cssd_sys_config.deleted_by_name IS '删除人姓名快照';
COMMENT ON COLUMN cssd.cssd_sys_config.create_channel IS '创建途径 web/app';
COMMENT ON COLUMN cssd.cssd_sys_config.update_channel IS '最后修改途径';
COMMENT ON COLUMN cssd.cssd_sys_config.delete_channel IS '删除途径';

COMMENT ON COLUMN cssd.cssd_pack_template_item.template_name IS '模板名称快照';
COMMENT ON COLUMN cssd.cssd_device_program.device_no IS '设备编号（锅号）快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.need_wash IS '当时是否须清洗（模板快照）';
COMMENT ON COLUMN cssd.cssd_pack_instance.wash_mode IS '当时清洗方式 scatter/whole_basket';
COMMENT ON COLUMN cssd.cssd_pack_instance.need_pack IS '当时是否须配包核包';
COMMENT ON COLUMN cssd.cssd_pack_instance.need_store IS '当时是否须无菌入库';
COMMENT ON COLUMN cssd.cssd_pack_instance.need_recycle IS '当时是否须回收';
COMMENT ON COLUMN cssd.cssd_pack_instance.ster_method IS '灭菌方式 high/low，装锅与设备 ster_temp_class 比对';
COMMENT ON COLUMN cssd.cssd_pack_instance.urgent_flag IS '加急：1 是（可从回收行带入）';
COMMENT ON COLUMN cssd.cssd_pack_instance.owner_dept_id IS '模板所属科室 ID 快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.owner_dept_code IS '模板所属科室编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.owner_dept_name IS '模板所属科室名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_campus_id IS '当时所在院区 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_campus_code IS '当时所在院区编码快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.loc_campus_name IS '当时所在院区名称快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.process_printed_at IS '过程签打印时间；未打也可有条码';
COMMENT ON COLUMN cssd.cssd_pack_instance.legal_printed_at IS '法规包外签打印时间；未打不得发放';
COMMENT ON COLUMN cssd.cssd_pack_instance.stored_at IS '无菌入库时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.store_staff_id IS '入库人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.store_staff_no IS '入库人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.store_staff_name IS '入库人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.released_at IS '放行时间';
COMMENT ON COLUMN cssd.cssd_pack_instance.release_staff_id IS '放行人 ID';
COMMENT ON COLUMN cssd.cssd_pack_instance.release_staff_no IS '放行人工号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.release_staff_name IS '放行人姓名快照';
COMMENT ON COLUMN cssd.cssd_pack_instance_item.register_no IS '注册证号快照（高值/外来）';
COMMENT ON COLUMN cssd.cssd_recycle_bill.campus_id IS '院区 ID';
COMMENT ON COLUMN cssd.cssd_recycle_bill.campus_code IS '院区编码快照';
COMMENT ON COLUMN cssd.cssd_recycle_bill.campus_name IS '院区名称快照';
COMMENT ON COLUMN cssd.cssd_recycle_item.dist_bill_id IS '原发放单 ID';
COMMENT ON COLUMN cssd.cssd_recycle_item.dist_bill_no IS '原发放单号冗余';
COMMENT ON COLUMN cssd.cssd_recycle_item.dist_item_id IS '原发放明细 ID';
COMMENT ON COLUMN cssd.cssd_ster_load.main_load_desc IS '主要装载（WS 310 点名项）';
COMMENT ON COLUMN cssd.cssd_ster_load_item.sterilize_date IS '灭菌日期快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.expire_date IS '失效日期快照（完成灭菌后回写）';
COMMENT ON COLUMN cssd.cssd_ster_load_item.device_id IS '灭菌器 ID 快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.device_code IS '灭菌器编码快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.device_name IS '灭菌器名称快照';
COMMENT ON COLUMN cssd.cssd_ster_load_item.device_no IS '灭菌器编号（锅号）快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.campus_id IS '院区 ID';
COMMENT ON COLUMN cssd.cssd_apply_bill.campus_code IS '院区编码快照';
COMMENT ON COLUMN cssd.cssd_apply_bill.campus_name IS '院区名称快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.campus_id IS '院区 ID';
COMMENT ON COLUMN cssd.cssd_dist_bill.campus_code IS '院区编码快照';
COMMENT ON COLUMN cssd.cssd_dist_bill.campus_name IS '院区名称快照';
COMMENT ON COLUMN cssd.cssd_dist_item.sterilize_date IS '灭菌日期快照';
COMMENT ON COLUMN cssd.cssd_dist_item.expire_date IS '失效日期快照';
COMMENT ON COLUMN cssd.cssd_dist_item.device_no IS '灭菌器编号快照';
COMMENT ON COLUMN cssd.cssd_dist_item.load_no IS '灭菌批次号快照';
COMMENT ON COLUMN cssd.cssd_use_check.campus_id IS '院区 ID';
COMMENT ON COLUMN cssd.cssd_use_check.campus_code IS '院区编码快照';
COMMENT ON COLUMN cssd.cssd_use_check.campus_name IS '院区名称快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.sterilize_date IS '灭菌日期快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.expire_date IS '失效日期快照';
COMMENT ON COLUMN cssd.cssd_use_check_item.device_no IS '灭菌器编号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.template_id IS '包模板 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.template_code IS '包模板编码快照';
COMMENT ON COLUMN cssd.cssd_recall_item.template_name IS '包模板名称快照';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_campus_id IS '当时所在院区 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_campus_code IS '当时所在院区编码快照';
COMMENT ON COLUMN cssd.cssd_recall_item.loc_campus_name IS '当时所在院区名称快照';
COMMENT ON COLUMN cssd.cssd_recall_item.bed_no IS '当时床号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.surgeon_id IS '术者 ID';
COMMENT ON COLUMN cssd.cssd_recall_item.surgeon_no IS '术者工号快照';
COMMENT ON COLUMN cssd.cssd_recall_item.surgeon_name IS '术者姓名快照';
COMMENT ON COLUMN cssd.cssd_print_log.instance_id IS '包实例 ID（打包装签时）';
COMMENT ON COLUMN cssd.cssd_print_log.barcode IS '当时条码快照';
COMMENT ON COLUMN cssd.cssd_print_log.label_phase IS 'process/legal/inner_list/badge/dist_bill/apply_bill';
COMMENT ON COLUMN cssd.cssd_print_template.label_phase IS 'process 过程签 / legal 法规签 / inner_list 包内清单 / badge 工号牌 / dist_bill 发放单 / apply_bill 申领单';
COMMENT ON COLUMN cssd.cssd_pack_instance.recycle_bill_id IS '本循环来源回收单 ID（打散部分配包）';
COMMENT ON COLUMN cssd.cssd_pack_instance.recycle_bill_no IS '来源回收单号快照';
COMMENT ON COLUMN cssd.cssd_pack_instance.recycle_item_id IS '来源回收明细 ID';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.template_code IS '包模板编码快照';
COMMENT ON COLUMN cssd.cssd_rfid_bind_log.template_name IS '包模板名称快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.template_code IS '包模板编码快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.template_name IS '包模板名称快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.patient_no IS '住院号快照';
COMMENT ON COLUMN cssd.cssd_his_sync_log.patient_name IS '患者姓名快照';
COMMENT ON COLUMN cssd.cssd_once_lot.vendor_id IS '供应商 ID';
COMMENT ON COLUMN cssd.cssd_once_lot.vendor_code IS '供应商编码快照';
COMMENT ON COLUMN cssd.cssd_once_lot.vendor_name IS '供应商名称快照';
COMMENT ON COLUMN cssd.cssd_once_io.vendor_id IS '供应商 ID';
COMMENT ON COLUMN cssd.cssd_once_io.vendor_code IS '供应商编码快照';
COMMENT ON COLUMN cssd.cssd_once_io.vendor_name IS '供应商名称快照';
COMMENT ON COLUMN cssd.cssd_once_io.ster_lot_no IS '灭菌批号快照';
COMMENT ON COLUMN cssd.cssd_once_io.expire_date IS '效期快照';
