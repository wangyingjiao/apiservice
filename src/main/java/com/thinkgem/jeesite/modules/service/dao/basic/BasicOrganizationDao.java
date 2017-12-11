/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.basic;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;

/**
 * 机构DAO接口
 * @author a
 * @version 2017-12-11
 */
@MyBatisDao
public interface BasicOrganizationDao extends CrudDao<BasicOrganization> {

    BasicOrganization getByName(String name);

    void deleteCitysByOrgId(BasicOrganization basicOrganization);
}