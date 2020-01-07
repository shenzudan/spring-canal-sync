package com.stanwind.sync.anno;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 * EnableSyncListener 开启同步监听
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 14:54
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SyncListenerAutoConfiguration.class)
public @interface EnableSyncListener {

}
