package com.gm_spring.formework.context.support;

import com.gm_spring.formework.beans.config.GMBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMDefaultListableBeanFactory extends GMAbstractApplicationContext {

    //存储注册信息的 BeanDefinition
    protected final Map<String, GMBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GMBeanDefinition>();
}
