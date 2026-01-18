package com.gdut.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author liujunliang
 * @date 2026/1/18
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 新增时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 填充默认状态（1-启用）
        strictInsertFill(metaObject, "status", Integer.class, 1);
    }

    // 更新时无需填充
    @Override
    public void updateFill(MetaObject metaObject) {}
}
