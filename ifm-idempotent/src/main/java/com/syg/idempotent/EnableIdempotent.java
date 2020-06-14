package com.syg.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableIdempotent {

    String[] value() default "";
    long expire() default 0;
    long synctime() default 0;
    //redis过期时间单位，默认为分钟
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    //设置是否进行list单个参数级验证，默认为false
    boolean validateList() default false;
    //设置幂等优先级，如果为messageId则先走messageId，若messageId没有，走param，如果设置为param则直接走param幂等
    ValidateType validateType() default ValidateType.MESSAGEID;

}
