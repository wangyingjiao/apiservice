/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.station;

import java.util.List;

import com.thinkgem.jeesite.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.dao.station.ServiceStationDao;

/**
 * 服务站Service
 * @author x
 * @version 2017-11-06
 */
@Service
@Transactional(readOnly = true)
public class ServiceStationService extends CrudService<ServiceStationDao, ServiceStation> {
	@Autowired
	ServiceStationDao stationDao;

	public ServiceStation get(String id) {
		return super.get(id);
	}
	
	public List<ServiceStation> findList(ServiceStation serviceStation) {
		return super.findList(serviceStation);
	}
	
	public Page<ServiceStation> findPage(Page<ServiceStation> page, ServiceStation serviceStation) {
		return super.findPage(page, serviceStation);
	}
	
	@Transactional(readOnly = false)
	public void save(ServiceStation serviceStation) {
		super.save(serviceStation);
	}
	
	@Transactional(readOnly = false)
	public void delete(ServiceStation serviceStation) {
		super.delete(serviceStation);
	}

	public Page<User> findUserData(String id) {
		return stationDao.findUserData(id);
	}
}