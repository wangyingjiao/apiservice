/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.office;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;

import java.util.List;

/**
 * 机构服务城市列表DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface OfficeSeviceAreaListDao extends CrudDao<OfficeSeviceAreaList> {

    List<OfficeSeviceAreaList> getOfficeCitys(String officeId);
}