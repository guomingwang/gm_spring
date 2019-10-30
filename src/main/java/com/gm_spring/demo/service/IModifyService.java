package com.gm_spring.demo.service;

/**
 * 增、删、改业务
 *
 * @author WangGuoMing
 * @since 2019/10/30
 */
public interface IModifyService {

    /**
     * 增加
     */
    String add(String name, String addr);

    /**
     * 修改
     */
    String edit(Integer id, String name);

    /**
     * 删除
     */
    String remove(Integer id);
}
