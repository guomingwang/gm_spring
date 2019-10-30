package com.gm_spring.demo.service.impl;

import com.gm_spring.demo.service.IModifyService;

/**
 * 增、删、改业务
 *
 * @author WangGuoMing
 * @since 2019/10/30
 */
public class ModifyService implements IModifyService {

    /**
     * 增加
     *
     * @param name
     * @param addr
     * @return
     */
    public String add(String name, String addr) {
        return "modifyService add,name=" + name + ",addr=" + addr;
    }


    /**
     * 修改
     *
     * @param id
     * @param name
     * @return
     */
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public String remove(Integer id) {
        return "modifyService remove,id=" + id;
    }
}
