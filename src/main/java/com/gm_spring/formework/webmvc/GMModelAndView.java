package com.gm_spring.formework.webmvc;

import java.util.Map;

/**
 * @author WangGuoMing
 * @since 2019/10/30
 */
public class GMModelAndView {

    //页面模板的名称
    private String viewName;

    //往页面传送的参数
    private Map<String, ?> model;

    public GMModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public GMModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
