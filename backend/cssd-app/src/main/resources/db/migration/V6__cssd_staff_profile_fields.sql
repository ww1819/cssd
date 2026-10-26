-- 人员档案扩展：年龄、学历、专业、职称、能级（层级）
ALTER TABLE cssd.cssd_staff ADD COLUMN IF NOT EXISTS age int NULL;
ALTER TABLE cssd.cssd_staff ADD COLUMN IF NOT EXISTS education varchar(64) NULL;
ALTER TABLE cssd.cssd_staff ADD COLUMN IF NOT EXISTS major varchar(64) NULL;
ALTER TABLE cssd.cssd_staff ADD COLUMN IF NOT EXISTS job_title varchar(64) NULL;
ALTER TABLE cssd.cssd_staff ADD COLUMN IF NOT EXISTS energy_level varchar(64) NULL;

COMMENT ON COLUMN cssd.cssd_staff.age IS '年龄';
COMMENT ON COLUMN cssd.cssd_staff.education IS '学历';
COMMENT ON COLUMN cssd.cssd_staff.major IS '专业';
COMMENT ON COLUMN cssd.cssd_staff.job_title IS '职称';
COMMENT ON COLUMN cssd.cssd_staff.energy_level IS '能级（层级）';
