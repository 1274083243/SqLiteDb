package com.ike.sqlitedb.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限申请失败的注解
 */
@Target(ElementType.METHOD)//代表注解作用的位置
@Retention(RetentionPolicy.RUNTIME)//代表运行时检测
public @interface PermissionFailed {
    public int requestCode();
}
