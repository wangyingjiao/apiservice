/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.sort;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;

import java.util.List;

/**
 * 服务对应的城市DAO接口
 * @author a
 * @version 2017-12-12
 */
@MyBatisDao
public interface SerCityScopeDao extends CrudDao<SerCityScope> {

    void delSerCityScopeByMaster(String id);

    List<SerCityScope> getSerCityScopeByMaster(String id);
}