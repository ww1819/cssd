-- 登录账号绑定菜单授权（组织机构初始化写入）
ALTER TABLE cssd.cssd_auth_account
    ADD COLUMN IF NOT EXISTS menu_auth text NULL;

COMMENT ON COLUMN cssd.cssd_auth_account.menu_auth IS '菜单授权 JSON 数组；空=不限制（演示账号）';
