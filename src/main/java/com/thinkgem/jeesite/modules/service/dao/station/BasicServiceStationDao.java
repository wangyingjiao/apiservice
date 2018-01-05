/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.station;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;

import java.util.List;

/**
 * 服务站DAO接口
 * @author 服务站
 * @version 2017-12-11
 */
@MyBatisDao
public interface BasicServiceStationDao extends CrudDao<BasicServiceStation> {
    //查询服务站人员人数
    int getCount(BasicServiceStation station);
    //根据权限获取服务站
    List<BasicServiceStation> getServiceStationList(BasicServiceStation station);
}