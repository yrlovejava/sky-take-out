package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 * 自定义这个注解的目的是在插入和更新操作的时候，能自动填充一些字段，比如create_time create_user update_time update_user
 */
@Target(ElementType.METHOD) // 指定了注解可以放置在哪些元素，这里是只能应用到方法上
@Retention(RetentionPolicy.RUNTIME) // 指定注解的保留策略，即注解在何时会被丢弃，这里是RUNTIME，在编译是将注解记录在类文件中，并且在运行时可以通过反射机制获取
public @interface AutoFill {

    //指定数据库操作类型，自定义的枚举类，有两个枚举，UPDATE INSERT
    OperationType value();
}
