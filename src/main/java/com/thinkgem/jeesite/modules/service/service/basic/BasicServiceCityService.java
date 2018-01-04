/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.basic;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicServiceCityDao;

/**
 * 机构服务城市Service
 * @author a
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class BasicServiceCityService extends CrudService<BasicServiceCityDao, BasicServiceCity> {

	public BasicServiceCity get(String id) {
		return super.get(id);
	}
	
	public List<BasicServiceCity> findList(BasicServiceCity basicServiceCity) {
		return super.findList(basicServiceCity);
	}
	
	public Page<BasicServiceCity> findPage(Page<BasicServiceCity> page, BasicServiceCity basicServiceCity) {
		return super.findPage(page, basicServiceCity);
	}
	
	@Transactional(readOnly = false)
	public void save(BasicServiceCity basicServiceCity) {
		super.save(basicServiceCity);
	}
	
	@Transactional(readOnly = false)
	public void delete(BasicServiceCity basicServiceCity) {
		super.delete(basicServiceCity);
	}

    public List<BasicServiceCity> getCityCodesByOrgId(String id) {
		List<BasicServiceCity> list = dao.getCityCodesByOrgId(id);
		return list;
    }

}