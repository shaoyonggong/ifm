package com.syg.ifmacl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 将mapper打上这个注解表示这个mapper被ACL组建管理
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IFMDataPermissionACL {
}
