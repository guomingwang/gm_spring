package com.gm_spring.formework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * 设计这个类的主要目的是：
 * 1.将一个静态文件变为一个动态文件
 * 2.根据用户传送不同的参数，产生不同的结果
 * 最终输出字符串，交给 Response 输出
 *
 * @author WangGuoMing
 * @since 2019/10/30
 */
public class GMViewResolver {

    //后缀
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    //文件
    private File templateRootDir;
    //
    private String viewName;

    public GMViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public GMView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File(
                (templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new GMView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
