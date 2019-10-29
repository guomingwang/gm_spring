package com.gm_spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求 URL
 *
 * @author WangGuoMing
 * @since 2019/10/22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GMRequestMapping {

    String value() default "";
}
