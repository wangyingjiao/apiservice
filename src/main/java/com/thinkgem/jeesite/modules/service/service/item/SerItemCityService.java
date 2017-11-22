/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCity;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCityDao;

/**
 * 服务项目定向城市信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemCityService extends CrudService<SerItemCityDao, SerItemCity> {

	public SerItemCity get(String id) {
		return super.get(id);
	}

	public List<SerItemCity> findList(SerItemCity serItemCity) {
		return super.findList(serItemCity);
	}

	public Page<SerItemCity> findPage(Page<SerItemCity> page, SerItemCity serItemCity) {
		return super.findPage(page, serItemCity);
	}

	@Transactional(readOnly = false)
	public void save(SerItemCity serItemCity) {
		super.save(serItemCity);
	}

	@Transactional(readOnly = false)
	public void delete(SerItemCity serItemCity) {
		super.delete(serItemCity);
	}

}