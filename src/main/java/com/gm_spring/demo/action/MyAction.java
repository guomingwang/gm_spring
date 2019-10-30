package com.gm_spring.demo.action;

import com.gm_spring.demo.service.IModifyService;
import com.gm_spring.demo.service.IQueryService;
import com.gm_spring.formework.annotation.GMAutowired;
import com.gm_spring.formework.annotation.GMController;
import com.gm_spring.formework.annotation.GMRequestMapping;
import com.gm_spring.formework.annotation.GMRequestParam;
import com.gm_spring.formework.webmvc.GMModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口 URL
 *
 * @author WangGuoMing
 * @since 2019/10/30
 */
@GMController
@GMRequestMapping("/web")
public class MyAction {

    @GMAutowired
    IQueryService queryService;

    @GMAutowired
    IModifyService modifyService;

    @GMRequestMapping("/query.json")
    public GMModelAndView query(
            HttpServletRequest request, HttpServletResponse response, @GMRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response, result);
    }

    @GMRequestMapping("/add*.json")
    public GMModelAndView add(
            HttpServletRequest request, HttpServletResponse response, @GMRequestParam("name") String name,
            @GMRequestParam("addr") String addr) {
        String result = modifyService.add(name, addr);
        return out(response, result);
    }

    @GMRequestMapping("/remove.json")
    public GMModelAndView remove(
            HttpServletRequest request, HttpServletResponse response, @GMRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @GMRequestMapping("/edit.json")
    public GMModelAndView edit(
            HttpServletRequest request, HttpServletResponse response, @GMRequestParam("id") Integer id,
            @GMRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }

    private GMModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
