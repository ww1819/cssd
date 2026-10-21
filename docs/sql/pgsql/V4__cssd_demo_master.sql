-- 闭环演示主数据（一院一套种子，可重复跑 Flyway 仅首次）
INSERT INTO cssd.cssd_campus (id, campus_code, campus_name, name_pinyin, is_active, is_deleted) VALUES
('10000000-0000-7000-8000-000000000001','C01','总院','ZY',1,0);

INSERT INTO cssd.cssd_dept (id, dept_code, dept_name, name_pinyin, campus_id, dept_kind, is_active, is_deleted) VALUES
('10000000-0000-7000-8000-000000000011','CSSD','消毒供应中心','XDGYYZX','10000000-0000-7000-8000-000000000001','cssd',1,0),
('10000000-0000-7000-8000-000000000012','OR01','中心手术室','ZXSSS','10000000-0000-7000-8000-000000000001','or',1,0),
('10000000-0000-7000-8000-000000000013','SURG','外科病区','WKBQ','10000000-0000-7000-8000-000000000001','clinical',1,0);

INSERT INTO cssd.cssd_staff (id, staff_no, staff_name, name_pinyin, dept_id, staff_role, is_active, is_deleted) VALUES
('20000000-0000-7000-8000-000000000001','ADMIN','系统管理员','XTGLY','10000000-0000-7000-8000-000000000011','admin',1,0),
('20000000-0000-7000-8000-000000000002','NL001','王护士长','WHSZ','10000000-0000-7000-8000-000000000011','nurse_lead',1,0),
('20000000-0000-7000-8000-000000000003','OP001','李配包','LPB','10000000-0000-7000-8000-000000000011','operator',1,0),
('20000000-0000-7000-8000-000000000004','OP002','张核包','ZHB','10000000-0000-7000-8000-000000000011','operator',1,0),
('20000000-0000-7000-8000-000000000005','QC001','赵质检','ZZJ','10000000-0000-7000-8000-000000000011','qc',1,0),
('20000000-0000-7000-8000-000000000006','DL001','钱下送','QXS','10000000-0000-7000-8000-000000000011','delivery',1,0),
('20000000-0000-7000-8000-000000000007','CL001','孙护士','SHS','10000000-0000-7000-8000-000000000012','clinical',1,0),
('20000000-0000-7000-8000-000000000008','SG001','周医师','ZYS','10000000-0000-7000-8000-000000000012','surgeon',1,0);

INSERT INTO cssd.cssd_auth_account (id, staff_id, staff_no, password_hash, is_deleted) VALUES
('21000000-0000-7000-8000-000000000001','20000000-0000-7000-8000-000000000001','ADMIN','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000002','20000000-0000-7000-8000-000000000002','NL001','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000003','20000000-0000-7000-8000-000000000003','OP001','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000004','20000000-0000-7000-8000-000000000004','OP002','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000005','20000000-0000-7000-8000-000000000005','QC001','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000006','20000000-0000-7000-8000-000000000006','DL001','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000007','20000000-0000-7000-8000-000000000007','CL001','PLAIN:123456',0),
('21000000-0000-7000-8000-000000000008','20000000-0000-7000-8000-000000000008','SG001','PLAIN:123456',0);

INSERT INTO cssd.cssd_vendor (id, vendor_code, vendor_name, name_pinyin, is_active, is_deleted) VALUES
('30000000-0000-7000-8000-000000000001','V01','演示供应商','YSGYS',1,0);
INSERT INTO cssd.cssd_manufacturer (id, mfr_code, mfr_name, name_pinyin, is_active, is_deleted) VALUES
('30000000-0000-7000-8000-000000000011','M01','演示厂家','YSCJ',1,0);

INSERT INTO cssd.cssd_pack_material (id, material_code, material_name, name_pinyin, shelf_life_days, ster_method, is_active, is_deleted) VALUES
('40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装','WFBBZ',14,'high',1,0);

INSERT INTO cssd.cssd_instrument (id, instrument_code, instrument_name, name_pinyin, spec, model, barcode, is_active, is_deleted) VALUES
('50000000-0000-7000-8000-000000000001','INS-CZQ','持针器','CZQ','18cm','A1','INS-CZQ-001',1,0),
('50000000-0000-7000-8000-000000000002','INS-JW','组织剪','ZZJ','14cm','B1','INS-JW-001',1,0),
('50000000-0000-7000-8000-000000000003','INS-ZW','敷料镊','FLN','12cm','C1','INS-ZW-001',1,0);

INSERT INTO cssd.cssd_pack_template (id, template_code, template_name, name_pinyin, pack_category, owner_dept_id, owner_dept_code, owner_dept_name,
    material_id, material_code, material_name, shelf_life_days, basket_load_qty, safety_stock_qty,
    need_wash, wash_mode, need_pack, need_store, need_recycle, is_implant, ster_method, is_active, is_deleted)
VALUES
('60000000-0000-7000-8000-000000000001','PKG-KHB','口护包','KHB','cssd_set','10000000-0000-7000-8000-000000000012','OR01','中心手术室',
 '40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装',14,8,2,1,'scatter',1,1,1,0,'high',1,0),
('60000000-0000-7000-8000-000000000002','PKG-OR','手术室整篮包','SSSZLB','or_set','10000000-0000-7000-8000-000000000012','OR01','中心手术室',
 '40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装',7,1,1,1,'whole_basket',1,0,1,0,'high',1,0),
('60000000-0000-7000-8000-000000000003','PKG-LOAN','寄消包','JXB','loan','10000000-0000-7000-8000-000000000013','SURG','外科病区',
 '40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装',14,1,0,0,'scatter',0,0,0,0,'high',1,0),
('60000000-0000-7000-8000-000000000004','PKG-EXT','外来器械包','WLQXB','external','10000000-0000-7000-8000-000000000012','OR01','中心手术室',
 '40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装',1,4,0,1,'scatter',1,0,0,0,'high',1,0),
('60000000-0000-7000-8000-000000000005','PKG-BD','BD试验包','BDSYB','test_bd','10000000-0000-7000-8000-000000000011','CSSD','消毒供应中心',
 '40000000-0000-7000-8000-000000000001','MAT-NWB','无纺布包装',1,1,0,0,'scatter',1,0,0,0,'high',1,0);

INSERT INTO cssd.cssd_pack_template_item (id, template_id, template_code, template_name, line_no, instrument_id, instrument_code, instrument_name, spec, model, qty, is_deleted) VALUES
('61000000-0000-7000-8000-000000000001','60000000-0000-7000-8000-000000000001','PKG-KHB','口护包',1,'50000000-0000-7000-8000-000000000001','INS-CZQ','持针器','18cm','A1',2,0),
('61000000-0000-7000-8000-000000000002','60000000-0000-7000-8000-000000000001','PKG-KHB','口护包',2,'50000000-0000-7000-8000-000000000002','INS-JW','组织剪','14cm','B1',1,0),
('61000000-0000-7000-8000-000000000003','60000000-0000-7000-8000-000000000001','PKG-KHB','口护包',3,'50000000-0000-7000-8000-000000000003','INS-ZW','敷料镊','12cm','C1',2,0),
('61000000-0000-7000-8000-000000000004','60000000-0000-7000-8000-000000000002','PKG-OR','手术室整篮包',1,'50000000-0000-7000-8000-000000000001','INS-CZQ','持针器','18cm','A1',4,0);

INSERT INTO cssd.cssd_device (id, device_code, device_name, name_pinyin, device_kind, device_no, model, ster_temp_class, is_active, is_deleted) VALUES
('70000000-0000-7000-8000-000000000001','WASH-01','1号清洗机','1HQXJ','washer','W1','WD290',NULL,1,0),
('70000000-0000-7000-8000-000000000002','STER-01','1号高温灭菌器','1HGWMJQ','sterilizer','S1','HS-66','high',1,0),
('70000000-0000-7000-8000-000000000003','SEAL-01','1号封口机','1HFKJ','sealer','F1','KS-100',NULL,1,0);

INSERT INTO cssd.cssd_device_program (id, device_id, device_code, device_name, device_no, program_code, program_name, name_pinyin, is_active, is_deleted) VALUES
('71000000-0000-7000-8000-000000000001','70000000-0000-7000-8000-000000000001','WASH-01','1号清洗机','W1','P-WASH','标准清洗','BZQX',1,0),
('71000000-0000-7000-8000-000000000002','70000000-0000-7000-8000-000000000002','STER-01','1号高温灭菌器','S1','P-134','134℃脉动真空','MDCZK',1,0);

INSERT INTO cssd.cssd_basket (id, basket_code, basket_name, name_pinyin, basket_kind, color_code, basket_status, is_active, is_deleted) VALUES
('80000000-0000-7000-8000-000000000001','BK-01','1号清洗篮','1HQXL','wash','white','idle',1,0),
('80000000-0000-7000-8000-000000000002','BK-02','2号清洗篮','2HQXL','wash','green','idle',1,0);

INSERT INTO cssd.cssd_cart (id, cart_code, cart_name, name_pinyin, is_active, is_deleted) VALUES
('80000000-0000-7000-8000-000000000011','CART-01','1号下送车','1HXSC',1,0);

INSERT INTO cssd.cssd_once_item (id, item_code, item_name, name_pinyin, spec, unit_name, mfr_id, mfr_code, mfr_name, vendor_id, vendor_code, vendor_name, min_stock, is_active, is_deleted) VALUES
('90000000-0000-7000-8000-000000000001','ONCE-GLOVE','无菌手套','WJST','7.5','副','30000000-0000-7000-8000-000000000011','M01','演示厂家','30000000-0000-7000-8000-000000000001','V01','演示供应商',20,1,0);

INSERT INTO cssd.cssd_once_lot (id, item_id, item_code, item_name, spec, unit_name, mfr_id, mfr_code, mfr_name, vendor_id, vendor_code, vendor_name, lot_no, ster_lot_no, expire_date, qty_on_hand, is_blocked, is_deleted) VALUES
('91000000-0000-7000-8000-000000000001','90000000-0000-7000-8000-000000000001','ONCE-GLOVE','无菌手套','7.5','副','30000000-0000-7000-8000-000000000011','M01','演示厂家','30000000-0000-7000-8000-000000000001','V01','演示供应商','LOT-202609','STER-001','2027-09-01',100,0,0);
