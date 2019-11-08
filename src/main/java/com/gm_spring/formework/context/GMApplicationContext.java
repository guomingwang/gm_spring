package com.gm_spring.formework.context;

import com.gm_spring.formework.annotation.GMAutowired;
import com.gm_spring.formework.annotation.GMController;
import com.gm_spring.formework.annotation.GMService;
import com.gm_spring.formework.beans.GMBeanWrapper;
import com.gm_spring.formework.beans.config.GMBeanDefinition;
import com.gm_spring.formework.beans.config.GMBeanPostProcessor;
import com.gm_spring.formework.context.support.GMBeanDefinitionReader;
import com.gm_spring.formework.context.support.GMDefaultListableBeanFactory;
import com.gm_spring.formework.core.GMBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangGuoMing
 * @since 2019/10/29
 */
@Slf4j
public class GMApplicationContext extends GMDefaultListableBeanFactory implements GMBeanFactory {

    //配置文件所在路径的数组
    private String[] configLocations;
    //Bean 定义读取器
    private GMBeanDefinitionReader reader;
    //单例的 IoC 容器缓存， map<factoryBeanName, instance>
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的 IoC 容器， map<factoryBeanName, beanWrapper>
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
        GMBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            GMBeanPostProcessor beanPostProcessor = new GMBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            GMBeanWrapper beanWrapper = new GMBeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
            //在实例化初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            //依赖注入
            populateBean(beanName, instance);
            //通过这样调用，相当于给我们自己留有了可操作的空间
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(GMController.class) || clazz.isAnnotationPresent(GMService.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field :
                fields) {
            if (!field.isAnnotationPresent(GMAutowired.class)) {
                continue;
            }
            GMAutowired autowired = field.getAnnotation(GMAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                GMBeanWrapper beanWrapper = this.factoryBeanInstanceCache.get(autowiredBeanName);
                try {
                    field.set(instance, beanWrapper.getWrappedInstance());
                } catch (NullPointerException e) {
                    log.error("实例化 bean，并注入依赖，而被注入的依赖还没被实例化，所以抛出 nullPointerException");
                }
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            }
        }
    }

    //传一个 BeanDefinition，就返回一个实例 Bean
    private Object instantiateBean(GMBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            //因为根据 Class 才能确定一个类是否有实例
            if (this.factoryBeanObjectCache.containsKey(className)) {
                //全类名？
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                //简单类名或全类名？
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
