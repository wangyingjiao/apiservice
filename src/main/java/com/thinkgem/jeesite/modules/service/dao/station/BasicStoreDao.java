/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.station;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;

/**
 * 门店数据DAO接口
 * @author 门店数据
 * @version 2017-12-20
 */
@MyBatisDao
public interface BasicStoreDao extends CrudDao<BasicStore> {
	//add by wyr
	List<BasicStore> findListNotIn(BasicStore basicStore);

	List<BasicStore> findListIn(BasicStore basicStore);

	List<String> getInIds(@Param("orgId")String orgId);

	
}