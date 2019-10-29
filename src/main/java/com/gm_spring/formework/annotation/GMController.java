package com.gm_spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 *
 * @author WangGuoMing
 * @since 2019/10/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GMController {

    String value() default "";
}
