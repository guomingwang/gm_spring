package com.gm_spring.formework.context.support;

import com.gm_spring.formework.beans.config.GMBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找、读取、解析
 *
 * @author WangGuoMing
 * @since 2019/10/29
 */
public class GMBeanDefinitionReader {

    //扫描包路径下的类的全类名列表
    private List<String> registyBeanClasses = new ArrayList<String>();
    //属性配置
    private Properties config = new Properties();
    //固定配置文件中的key，相对于 XML 的规范
    private final String SCAN_PAKAGE = "scanPackage";

    public GMBeanDefinitionReader(String... locations) {
        //通过 URL 定位找到其对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PAKAGE));
    }

    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/
        URL url = this.getClass().getClassLoader()
                .getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file :
                classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    //把配置文件中扫描到的所有配置信息转换为 GMBeanDefinition 对象，以便于之后的 IoC 操作
    public List<GMBeanDefinition> loadBeanDefinitions() {
        List<GMBeanDefinition> result = new ArrayList<GMBeanDefinition>();
        try {
            for (String className :
                    registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                //类，key 存简单类名
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i :
                        interfaces) {
                    //接口，key 存全类名
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //把每一个配置信息解析成一个 BeanDefinition
    private GMBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        GMBeanDefinition beanDefinition = new GMBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    //将类名首字母改为小写
    //为了简化程序逻辑，就不做其他判断了，大家了解就好
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //因为大小写字母的 ascii 码相差 32
        //而且大写字母的 ascii 码要小于小写字母的 ascii 码
        //在 Java 中，对 char 做算术运算，实际上就是对 ascii 码做算术运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
