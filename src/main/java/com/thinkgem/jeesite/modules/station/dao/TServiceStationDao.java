/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.station.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.station.entity.TServiceStation;

/**
 * 服务站DAO接口
 * @author x
 * @version 2017-10-27
 */
@MyBatisDao
public interface TServiceStationDao extends CrudDao<TServiceStation> {
	
}