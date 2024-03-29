package com.gm_spring.formework.beans.config;

/**
 * 用来存储配置文件中的信息
 * 相当于保存在内存中的配置
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMBeanDefinition {

    //bean 的全类名
    private String beanClassName;
    //延时加载标识
    private boolean lazyInit = false;
    //beanName 在 IoC 容器中存储的 key
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
