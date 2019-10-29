package com.gm_spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 自动注入
 *
 * @author WangGuoMing
 * @since 2019/10/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GMAutowired {

    String value() default "";
}
