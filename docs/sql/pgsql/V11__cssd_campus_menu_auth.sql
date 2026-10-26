-- 组织机构菜单授权（授权弹窗持久化，不依赖浏览器 localStorage）
ALTER TABLE cssd.cssd_campus
    ADD COLUMN IF NOT EXISTS menu_auth text NULL;

COMMENT ON COLUMN cssd.cssd_campus.menu_auth IS '机构菜单授权 JSON 数组';
