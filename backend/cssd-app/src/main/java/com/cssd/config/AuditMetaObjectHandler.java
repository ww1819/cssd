package com.cssd.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cssd.security.LoginUser;
import com.cssd.security.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now();
        strictInsertFill(metaObject, "createdAt", OffsetDateTime.class, now);
        strictInsertFill(metaObject, "updatedAt", OffsetDateTime.class, now);
        strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        strictInsertFill(metaObject, "createChannel", String.class, "web");
        LoginUser user = SecurityUtils.current();
        if (user != null) {
            strictInsertFill(metaObject, "createdBy", String.class, user.getStaffId());
            strictInsertFill(metaObject, "createdByNo", String.class, user.getStaffNo());
            strictInsertFill(metaObject, "createdByName", String.class, user.getStaffName());
            strictInsertFill(metaObject, "updatedBy", String.class, user.getStaffId());
            strictInsertFill(metaObject, "updatedByNo", String.class, user.getStaffNo());
            strictInsertFill(metaObject, "updatedByName", String.class, user.getStaffName());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", OffsetDateTime.class, OffsetDateTime.now());
        strictUpdateFill(metaObject, "updateChannel", String.class, "web");
        LoginUser user = SecurityUtils.current();
        if (user != null) {
            strictUpdateFill(metaObject, "updatedBy", String.class, user.getStaffId());
            strictUpdateFill(metaObject, "updatedByNo", String.class, user.getStaffNo());
            strictUpdateFill(metaObject, "updatedByName", String.class, user.getStaffName());
        }
    }
}
