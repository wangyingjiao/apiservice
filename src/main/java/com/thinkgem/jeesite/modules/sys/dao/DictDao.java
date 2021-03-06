/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Dict;

/**
 * 字典DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {

	public List<String> findTypeList(Dict dict);

    //app
    public List<Dict> appFindList(Dict dict);

    //app根据code取name
    Dict findName(Dict dict);

    List<Dict> getAllDicts();

    List<Dict> getGroupDicts();

    List<Dict> dictListData(Dict entity);

    List<Dict> dictListDataByType(Dict dict);

    Dict dictListDataById(Dict dict);

    void upData(Dict dict);
}
