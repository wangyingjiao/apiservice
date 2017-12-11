/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.basic;

import java.util.List;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;

/**
 * 机构Service
 * @author a
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class BasicOrganizationService extends CrudService<BasicOrganizationDao, BasicOrganization> {

	@Autowired
	BasicServiceCityService basicServiceCityService;

	public Page<BasicOrganization> findPage(Page<BasicOrganization> page, BasicOrganization office){
		office.setPage(page);
		/*User user = UserUtils.getUser();
		if (user.isAdmin()){
			page.setList(dao.findAllList(new BasicOrganization()));
		}else{
			office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));

		}*/
		page.setList(dao.findList(office));
		return page;
	}

	/**
	 * 验证名称是否重复
	 * @param name
	 * @return
	 */
	public boolean getByName(String name) {
		BasicOrganization basicOrganization = dao.getByName(name);
		return basicOrganization != null;
	}

	@Transactional(readOnly = false)
	public void save(BasicOrganization basicOrganization) {
		if(StringUtils.isNotBlank(basicOrganization.getId())){
			//更新
			deleteCitysByOrgId(basicOrganization);
		}

		super.save(basicOrganization);

		List<BasicServiceCity> cityCodes = basicOrganization.getCityCodes();
		if(null != cityCodes){
			for(BasicServiceCity city : cityCodes ){
				city.setOrgId(basicOrganization.getId());
				basicServiceCityService.save(city);
			}
		}
	}

	private void deleteCitysByOrgId(BasicOrganization basicOrganization) {
		dao.deleteCitysByOrgId(basicOrganization);
	}

	public BasicOrganization get(String id) {
		return super.get(id);
	}
	
	public List<BasicOrganization> findList(BasicOrganization basicOrganization) {
		return super.findList(basicOrganization);
	}

	public BasicOrganization formData(BasicOrganization basicOrganization) {
		if(null == basicOrganization){
			return null;
		}
		BasicOrganization basicOrganizationRe = get(basicOrganization.getId());

		List<BasicServiceCity> cityCodes = basicServiceCityService.getCityCodesByOrgId(basicOrganization.getId());
		basicOrganizationRe.setCityCodes(cityCodes);
		return basicOrganizationRe;
	}
}