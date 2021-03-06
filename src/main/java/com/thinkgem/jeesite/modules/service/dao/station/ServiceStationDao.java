/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.station;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 服务站DAO接口
 * @author x
 * @version 2017-12-01
 */
@MyBatisDao
public interface ServiceStationDao extends CrudDao<ServiceStation> {

    ServiceStation findStationByUser(User user);
}