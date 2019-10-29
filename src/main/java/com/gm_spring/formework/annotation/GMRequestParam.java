package com.gm_spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求参数映射
 *
 * @author WangGuoMing
 * @since 2019/10/22
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GMRequestParam {

    String value() default "";
}
