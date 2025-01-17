package com.chaty.components;

import java.util.Date;
import java.util.Optional;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chaty.security.AuthUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DbMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        Optional.ofNullable(AuthUtil.getLoginUser()).ifPresent(user -> {
            this.strictInsertFill(metaObject, "creator", String.class, user.getId());
        });
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

}
