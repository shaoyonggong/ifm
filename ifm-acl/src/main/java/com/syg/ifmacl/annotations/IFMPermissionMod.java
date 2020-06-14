package com.syg.ifmacl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 前置后置组件都必须有这个脱敏规则
 * @Author shaoyonggong
 * @Date 2020/6/15
 * <p>
 * 注意：这个标记可以打在类和方法上,前置后置组件都必须有这个脱敏规则
 * 进行脱敏权限认定，默认为敏感字段不可读（前两个00表示角色，中间两个表示上级，后面两个表示同僚）
 * 01表示只读。10表示读写，11表示记录创建人，00表示无权限
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IFMPermissionMod {
    public int value() default 0b000000;
}
