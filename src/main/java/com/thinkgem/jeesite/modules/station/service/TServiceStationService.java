/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.station.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.station.entity.TServiceStation;
import com.thinkgem.jeesite.modules.station.dao.TServiceStationDao;

/**
 * 服务站Service
 * @author x
 * @version 2017-10-27
 */
@Service
@Transactional(readOnly = true)
public class TServiceStationService extends CrudService<TServiceStationDao, TServiceStation> {

	public TServiceStation get(String id) {
		return super.get(id);
	}
	
	public List<TServiceStation> findList(TServiceStation tServiceStation) {
		return super.findList(tServiceStation);
	}
	
	public Page<TServiceStation> findPage(Page<TServiceStation> page, TServiceStation tServiceStation) {
		return super.findPage(page, tServiceStation);
	}
	
	@Transactional(readOnly = false)
	public void save(TServiceStation tServiceStation) {
		super.save(tServiceStation);
	}
	
	@Transactional(readOnly = false)
	public void delete(TServiceStation tServiceStation) {
		super.delete(tServiceStation);
	}
	
}