package com.syg.ifmacl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description ACL后置处理脱敏必须注解
 * @Author shaoyonggong
 * @Date 2020/6/15
 * <p>
 * 注意：这个标记要打在查询数据返回类型的po上
 * 需要控制权限的敏感字段（BigDecimal，String，基本数据类型及其包装类型会做相应处理，其他类型如果为敏感字段全置为null）
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IFMSensitiveField {
}
