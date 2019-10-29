package com.gm_spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑，注入接口
 *
 * @author WangGuoMing
 * @since 2019/10/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GMService {

    String value() default "";
}
