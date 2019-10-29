package com.gm_spring.formework.context.support;

/**
 * IoC 容器实现的顶层设计
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMAbstractApplicationContext {

    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
