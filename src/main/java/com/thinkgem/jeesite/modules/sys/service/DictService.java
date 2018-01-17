/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.modules.sys.dao.DictDao;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典Service
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<DictDao, Dict> {

    /**
     * 查询字段类型列表
     *
     * @return
     */
    public List<String> findTypeList() {
        return dao.findTypeList(new Dict());
    }
    //app
    public List<Dict> appFindList(Dict dict) {

        return dao.appFindList(dict);
    }

    @Transactional(readOnly = false)
    public void save(Dict dict) {
        super.save(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

    @Transactional(readOnly = false)
    public void delete(Dict dict) {
        super.delete(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

    public Object getAllDicts() {
        List<Dict> dicts = dao.getAllDicts();
        JSONObject jsonObject = new JSONObject();
        for (Dict dict : dicts) {
            String type = dict.getType();
            JSONObject dictByType = getDictByType(dicts, type);
            jsonObject.put(type, dictByType);
        }
        return jsonObject;
    }

    private JSONObject getDictByType(List<Dict> dicts, String type) {
        JSONObject jsonObject = new JSONObject();
        for (Dict dict : dicts) {
            if (dict.getType().equals(type)) {
                jsonObject.put(dict.getValue(), dict.getLabel());
            }
        }
        return jsonObject;
    }
}
