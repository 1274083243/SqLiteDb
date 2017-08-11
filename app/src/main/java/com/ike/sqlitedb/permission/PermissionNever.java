package com.ike.sqlitedb.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 永远不提示权限申请的注解
 */
@Target(ElementType.METHOD)//代表注解作用的位置
@Retention(RetentionPolicy.RUNTIME)//代表运行时检测
public @interface PermissionNever {
    public int requestCode();
}
