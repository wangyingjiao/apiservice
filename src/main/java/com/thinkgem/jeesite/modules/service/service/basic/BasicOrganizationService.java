/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.basic;

import java.util.ArrayList;
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
		User user = UserUtils.getUser();
		office.getSqlMap().put("dsf", BaseService.dataOrganFilter(user, "a"));
		page.setList(dao.findList(office));
		return page;
	}

	/**
	 * 验证名称是否重复
	 * @param name
	 * @return
	 */
	public boolean getByName(String name) {
		List<BasicOrganization> basicOrganization = dao.getByName(name);
		return basicOrganization.size() != 0;
	}

	@Transactional(readOnly = false)
	public void save(BasicOrganization basicOrganization) {
		if(StringUtils.isNotBlank(basicOrganization.getId())){
			//更新
			deleteCitysByOrgId(basicOrganization);
		}

		super.save(basicOrganization);

		List<String> cityCodes = basicOrganization.getCityCodes();
		if(null != cityCodes){
			for(String cityCode : cityCodes ){
                BasicServiceCity city = new BasicServiceCity();
				city.setOrgId(basicOrganization.getId());
				city.setCityCode(cityCode);
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

	/**
	 * 机构详情
	 * @param basicOrganization
	 * @return
	 */
	public BasicOrganization formData(BasicOrganization basicOrganization) {
		if(null == basicOrganization){
			return null;
		}
		BasicOrganization basicOrganizationRe = get(basicOrganization.getId());
        List<String> cityCodes = new ArrayList<String>();//服务城市
		List<BasicServiceCity> cityCodeList = basicServiceCityService.getCityCodesByOrgId(basicOrganization.getId());
		if(null != cityCodeList){
            for(BasicServiceCity city : cityCodeList){
                cityCodes.add(city.getCityCode());
            }
        }
		basicOrganizationRe.setCityCodes(cityCodes);
		return basicOrganizationRe;
	}

	/**
	 * 获取当前机构下所有城市
	 * @param orgId
	 * @return
	 */
    public List<BasicServiceCity> getOrgCityCodes(String orgId) {
		List<BasicServiceCity> cityCodeList = basicServiceCityService.getCityCodesByOrgId(orgId);
		return cityCodeList;
    }
}