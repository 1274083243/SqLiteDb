package com.ike.sqlitedb.sqllitedb.db.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* author ike
* create time 22:19 2017/8/8
* function: 表名的注解
**/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
    String values();
}
