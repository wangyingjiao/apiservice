/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import com.thinkgem.jeesite.modules.service.dao.sort.SerCityScopeDao;

/**
 * 服务对应的城市Service
 * @author a
 * @version 2017-12-12
 */
@Service
@Transactional(readOnly = true)
public class SerCityScopeService extends CrudService<SerCityScopeDao, SerCityScope> {

	public SerCityScope get(String id) {
		return super.get(id);
	}
	
	public List<SerCityScope> findList(SerCityScope serCityScope) {
		return super.findList(serCityScope);
	}
	
	public Page<SerCityScope> findPage(Page<SerCityScope> page, SerCityScope serCityScope) {
		return super.findPage(page, serCityScope);
	}
	
	@Transactional(readOnly = false)
	public void save(SerCityScope serCityScope) {
		super.save(serCityScope);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerCityScope serCityScope) {
		super.delete(serCityScope);
	}
	
}