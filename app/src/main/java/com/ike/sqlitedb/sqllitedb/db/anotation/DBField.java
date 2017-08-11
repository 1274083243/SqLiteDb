package com.ike.sqlitedb.sqllitedb.db.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* author ike
* create time 22:21 2017/8/8
* function: 数据库字段注解
**/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {
     String values();
}
