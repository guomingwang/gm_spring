package com.gm_spring.formework.beans.config;

/**
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMBeanPostProcessor {

    //为在 Bean 的初始化之前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    //为在 Bean 的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
