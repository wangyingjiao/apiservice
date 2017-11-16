/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianImages;

/**
 * 服务技师关联图片DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface ServiceTechnicianImagesDao extends CrudDao<ServiceTechnicianImages> {
	
}