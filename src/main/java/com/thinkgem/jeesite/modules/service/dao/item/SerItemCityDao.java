/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;

import java.util.List;

/**
 * 服务项目对应的定向城市DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerItemCityDao extends CrudDao<SerItemCity> {

    void delSerItemCityByItem(SerItemInfo serItemInfo);

    List<SerItemCity> getSortCitys(SerItemInfo serItemInfo);

    List<SerItemCity> getOfficeCitys(SerItemInfo serItemInfo);
}