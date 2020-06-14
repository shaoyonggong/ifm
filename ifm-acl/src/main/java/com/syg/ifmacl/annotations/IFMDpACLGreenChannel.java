package com.syg.ifmacl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 打上这个标记说明不让组件管理这个方法
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IFMDpACLGreenChannel {
}
