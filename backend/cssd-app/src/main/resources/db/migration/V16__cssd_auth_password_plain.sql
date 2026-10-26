-- 登录账号保留明文密码，供人员档案展示/维护（哈希仍用于登录校验）
ALTER TABLE cssd.cssd_auth_account
    ADD COLUMN IF NOT EXISTS password_plain varchar(64) NULL;

COMMENT ON COLUMN cssd.cssd_auth_account.password_plain IS '明文密码（管理端展示；登录仍用 password_hash）';

-- 历史数据：机构初始化账号等密码默认同工号
UPDATE cssd.cssd_auth_account a
SET password_plain = s.staff_no
FROM cssd.cssd_staff s
WHERE a.staff_id = s.id
  AND (a.password_plain IS NULL OR btrim(a.password_plain) = '')
  AND s.staff_no IS NOT NULL
  AND btrim(s.staff_no) <> '';
