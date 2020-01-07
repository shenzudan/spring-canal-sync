package com.stanwind.sync.anno;

import com.stanwind.sync.CanalConstant;
import com.stanwind.sync.CanalConstant.EventType;
import java.lang.annotation.*;

/**
 * SyncListener
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 14:44
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SyncListener {

    /**
     * 数据库名
     */
    String db() default "";

    /**
     * 表名
     */
    String[] table() default {""};

    /**
     * 主键 和表一一对应
     * @return
     */
    String[] key() default {};

    /**
     * 数据库操作类型 默认增删改都响应
     */
    CanalConstant.EventType type() default EventType.ALL;
}
