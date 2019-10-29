package com.gm_spring.formework.core;

/**
 * 单例工厂的顶层设计
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
public interface GMBeanFactory {

    /**
     * 根据 beanName 从 IoC 容器中获得一个实例 Bean
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
