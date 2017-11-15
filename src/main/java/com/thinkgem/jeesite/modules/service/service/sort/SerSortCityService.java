/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortCity;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortCityDao;

/**
 * 服务分类对应的定向城市Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSortCityService extends CrudService<SerSortCityDao, SerSortCity> {

	public SerSortCity get(String id) {
		return super.get(id);
	}
	
	public List<SerSortCity> findList(SerSortCity serSortCity) {
		return super.findList(serSortCity);
	}
	
	public Page<SerSortCity> findPage(Page<SerSortCity> page, SerSortCity serSortCity) {
		return super.findPage(page, serSortCity);
	}
	
	@Transactional(readOnly = false)
	public void save(SerSortCity serSortCity) {
		super.save(serSortCity);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerSortCity serSortCity) {
		super.delete(serSortCity);
	}
	
}