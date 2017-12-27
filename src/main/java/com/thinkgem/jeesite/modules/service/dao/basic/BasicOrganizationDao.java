/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.basic;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;

import java.util.List;

/**
 * 机构DAO接口
 * @author a
 * @version 2017-12-11
 */
@MyBatisDao
public interface BasicOrganizationDao extends CrudDao<BasicOrganization> {

    List<BasicOrganization> getByName(BasicOrganization organization);

    void deleteCitysByOrgId(BasicOrganization basicOrganization);
}