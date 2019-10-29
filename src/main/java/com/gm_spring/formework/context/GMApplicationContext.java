package com.gm_spring.formework.context;

import com.gm_spring.formework.beans.GMBeanWrapper;
import com.gm_spring.formework.beans.config.GMBeanDefinition;
import com.gm_spring.formework.context.support.GMBeanDefinitionReader;
import com.gm_spring.formework.context.support.GMDefaultListableBeanFactory;
import com.gm_spring.formework.core.GMBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMApplicationContext extends GMDefaultListableBeanFactory implements GMBeanFactory {

    private String[] configLocations;

    private GMBeanDefinitionReader reader;

    //单例的 IoC 容器缓存
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    //通用的 IoC 容器
    private Map<String, GMBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GMBeanWrapper>();

    public GMApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.定位，定位配置文件
        reader = new GMBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关的类，把它们封装成 BeanDefinition
        List<GMBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3.注册，把配置信息放到容器里面（伪 IoC 容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, GMBeanDefinition> beanDefinitionEntry :
                super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<GMBeanDefinition> beanDefinitions) throws Exception {
        for (GMBeanDefinition beanDefinition :
                beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exist!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，读取 BeanDefinition 中的信息
    //然后通过反射机制创建一个实例并返回
    //Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
    //装饰者模式：
    //1.保留原来的 OOP 关系
    //2.需要对它进行扩展、增强（为了以后的 AOP 打基础）
    public Object getBean(String beanName) throws Exception {
        return null;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
