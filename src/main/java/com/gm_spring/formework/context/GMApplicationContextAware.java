package com.gm_spring.formework.context;

/**
 * 通过解耦方式获得 IoC 容器的顶层设计
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 * 将自动调用 setApplicationContext() 方法，从而将 IoC 容器注入目标类中
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
public interface GMApplicationContextAware {

    void setApplicationContext(GMApplicationContext applicationContext);
}
