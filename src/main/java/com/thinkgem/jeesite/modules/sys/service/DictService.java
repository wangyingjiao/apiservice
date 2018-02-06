/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.modules.sys.dao.DictDao;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    @Autowired
    private DictDao dictDao;

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
    ////app登陆 修改用户使用 根据code取name
    public Dict findName(Dict dict){
        return dao.findName(dict);
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

    /**
     * 控制台打印前台需要的字典表
     * @return
     */
    public Object getAllDicts() {
        List<Dict> dicts = dao.getAllDicts();

        List<Dict> dictGroups = dao.getGroupDicts();
        for(Dict dict:dictGroups){
            System.out.println("    \"" + dict.getType() + "\": {");
            int dictNum = 0;
            for(int i=0; i<dicts.size(); i++ ){
                Dict dict1=dicts.get(i);
                if(dict.getType().equals(dict1.getType())){
                    dictNum++;
                    if(dictNum == dict.getDictNum()){
                        System.out.println("        \"" + dict1.getValue() + "\": \"" + dict1.getLabel() + "\"");
                    }else{
                        System.out.println("        \"" + dict1.getValue() + "\": \"" + dict1.getLabel() + "\",");
                    }
                }
            }
            System.out.println("    }, ");
        }
        String json = JsonMapper.toJsonString(dictGroups);

        return json;

       /* JSONObject jsonObject = new JSONObject();
        for (Dict dict : dicts) {
            String type = dict.getType();
            JSONObject dictByType = getDictByType(dicts, type);
            jsonObject.put(type, dictByType);
        }
        return jsonObject;*/
    }

    private JSONObject getDictByType(List<Dict> dicts, String type) {
        JSONObject jsonObject = new JSONObject();
        for (Dict dict : dicts) {
            if (dict.getType().equals(type)) {
                jsonObject.put(dict.getValue(), dict.getLabel());
            }
        }


      /*  try {
            JSONObject spec_nameJSONObject = new JSONObject(jsonObject);
            Iterator<String> spec_nameIterator = jsonObject.keys();
            while (spec_nameIterator.hasNext()) {
                String key = spec_nameIterator.next();
                SpecEntity apecEntity = new SpecEntity();
                apecEntity.setSpecKey(key);
                apecEntity.setSpecValue(spec_nameJSONObject.getString(key));
                specList.add(apecEntity);
            }
            //看到没这个才是重点！！！！！！
            Collections.sort(agentGoodsDetailSpaceBean.getSpec_name());
        } catch (Exception e) {
            DebugLogUtil.getInstance().Error(e.toString());
        }
*/
      //  JSON.toJSONStringZ(jsonObject, SerializeConfig.getGlobalInstance(), SerializerFeature.QuoteFieldNames);
        return jsonObject;
    }

    public Page<Dict> dictListData(Page<Dict> page, Dict entity) {
        entity.setPage(page);
        /*List<Dict> list = dictDao.dictListData(entity);
        for (Dict d : list){
            d.setDictList(dictDao.dictListDataByType(d));
        }
        page.setList(list);*/
        page.setList(dictDao.dictListData(entity));
        return page;
    }

    public Page<Dict> dictListDataByType(Page<Dict> dictPage, Dict dict) {
        dict.setPage(dictPage);
        dictPage.setList(dictDao.dictListDataByType(dict));
        return dictPage;
    }

    public Dict dictListDataById(Dict dict) {
        return dictDao.dictListDataById(dict);
    }

    @Transactional(readOnly = false)
    public void upData(Dict dict) {
        dict.preUpdate();
        dictDao.upData(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }
}
