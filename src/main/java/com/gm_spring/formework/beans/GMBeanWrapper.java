package com.gm_spring.formework.beans;

/**
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public GMBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    //返回代理以后的 Class
    //可能会是这个 $Proxy0
    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }
}
