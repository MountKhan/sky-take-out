package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个自定义注解，用于标识某个方法需要自动填充某些字段
 * This is a custom annotation that identifies a method that needs to auto-fill certain fields
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型：update insert
    //operation type: update insert
    OperationType value();
}
