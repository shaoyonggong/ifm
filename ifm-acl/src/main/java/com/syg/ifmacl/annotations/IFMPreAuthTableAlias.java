package com.syg.ifmacl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description ACL组件数据前置处理必须的注解
 * @Author shaoyonggong
 * @Date 2020/6/15
 * <p>
 * 用指定过滤哪张表的表别名mapper上必须要有这个别名，可在方法上进行覆盖
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface IFMPreAuthTableAlias {
    String value() default "";
}
