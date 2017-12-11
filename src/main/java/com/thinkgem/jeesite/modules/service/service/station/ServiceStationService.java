/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.station;

import java.util.List;

import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
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
public class ServiceStationService extends CrudService<BasicServiceStationDao, BasicServiceStation> {

	@Autowired
	BasicServiceStationDao basicServiceStationDao;


	@Override
	public BasicServiceStation get(String id) {
		return super.get(id);
	}
	
	@Override
	public List<BasicServiceStation> findList(BasicServiceStation serviceStation) {
		return super.findList(serviceStation);
	}
	
	@Override
	public Page<BasicServiceStation> findPage(Page<BasicServiceStation> page, BasicServiceStation serviceStation) {
		return super.findPage(page, serviceStation);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(BasicServiceStation serviceStation) {
		super.save(serviceStation);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(BasicServiceStation serviceStation) {
		super.delete(serviceStation);
	}


}