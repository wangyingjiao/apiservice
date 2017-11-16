/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;

/**
 * 服务技师基础信息Service
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class ServiceTechnicianInfoService extends CrudService<ServiceTechnicianInfoDao, ServiceTechnicianInfo> {

	public ServiceTechnicianInfo get(String id) {
		return super.get(id);
	}
	
	public List<ServiceTechnicianInfo> findList(ServiceTechnicianInfo serviceTechnicianInfo) {
		return super.findList(serviceTechnicianInfo);
	}
	
	public Page<ServiceTechnicianInfo> findPage(Page<ServiceTechnicianInfo> page, ServiceTechnicianInfo serviceTechnicianInfo) {
		return super.findPage(page, serviceTechnicianInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(ServiceTechnicianInfo serviceTechnicianInfo) {
		super.save(serviceTechnicianInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(ServiceTechnicianInfo serviceTechnicianInfo) {
		super.delete(serviceTechnicianInfo);
	}
	
}