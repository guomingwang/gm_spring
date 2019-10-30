package com.gm_spring.demo.action;

import com.gm_spring.demo.service.IQueryService;
import com.gm_spring.formework.annotation.GMAutowired;
import com.gm_spring.formework.annotation.GMController;
import com.gm_spring.formework.annotation.GMRequestMapping;
import com.gm_spring.formework.annotation.GMRequestParam;
import com.gm_spring.formework.webmvc.GMModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口 URL
 *
 * @author WangGuoMing
 * @since 2019/10/30
 */
@GMController
@GMRequestMapping("/")
public class PageAction {

    @GMAutowired
    IQueryService queryService;

    @GMRequestMapping("/first.html")
    public GMModelAndView query(@GMRequestParam("teacher") String teacher) {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GMModelAndView("first.html", model);
    }
}
