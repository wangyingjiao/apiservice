/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.station;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStore;

/**
 * 门店与服务站DAO接口
 * @author a
 * @version 2017-12-20
 */
@MyBatisDao
public interface BasicServiceStoreDao extends CrudDao<BasicServiceStore> {

    void deletebyStation(BasicServiceStation station);

    void saveStationStore(BasicServiceStation station);
}