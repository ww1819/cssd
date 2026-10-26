-- 用户类型：普通用户 / 管理员；机构初始化账号与平台 admin 默认管理员
COMMENT ON COLUMN cssd.cssd_staff.user_type IS '用户类型：normal=普通用户，admin=管理员';

UPDATE cssd.cssd_staff
SET user_type = 'admin'
WHERE lower(staff_no) = 'admin'
   OR staff_no ~* '^cssd_[0-9]+$';

UPDATE cssd.cssd_staff
SET user_type = 'normal'
WHERE user_type IS NULL
   OR btrim(user_type) = ''
   OR lower(user_type) NOT IN ('admin', 'normal');
