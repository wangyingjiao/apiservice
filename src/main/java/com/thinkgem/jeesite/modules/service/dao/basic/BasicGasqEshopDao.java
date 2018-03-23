/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.basic;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.open.entity.OpenEshopInfoRequest;

/**
 * E店DAO接口
 * @author a
 * @version 2017-12-11
 */
@MyBatisDao
public interface BasicGasqEshopDao extends CrudDao<BasicGasqEshop> {

    BasicGasqEshop getInfoByEshopCode(OpenEshopInfoRequest info);
}