/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicGasqEshopDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.open.entity.OpenEshopInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * eshop Service
 * @author a
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class OpenEshopService extends CrudService<BasicGasqEshopDao, BasicGasqEshop> {

	@Autowired
	private BasicGasqEshopDao basicGasqEshopDao;

	@Transactional(readOnly = false)
	public void delete(OpenEshopInfoRequest openEshopInfoRequest) {
		BasicGasqEshop basicGasqEshop = new BasicGasqEshop();
		basicGasqEshop.setCode(openEshopInfoRequest.getEshop_code());//E店编码
		basicGasqEshopDao.delete(basicGasqEshop);
	}

	@Transactional(readOnly = false)
	public void save(OpenEshopInfoRequest info) {
		BasicGasqEshop gasqEshop = basicGasqEshopDao.getInfoByEshopCode(info);
		BasicGasqEshop basicGasqEshop = new BasicGasqEshop();
		basicGasqEshop.setName(info.getName());//E店名称
		basicGasqEshop.setBusinessModelId(info.getBusiness_model_id());//业务模式ID
		basicGasqEshop.setSellerId(info.getSeller_id());//商家ID
		basicGasqEshop.setCode(info.getEshop_code());//E店编码
		basicGasqEshop.setOperationBaseStatus(info.getOperation_base_status());//运营基本信息审核状态：no未审核；submit审核中；fail审核未通过；yes审核通过'
		basicGasqEshop.setThirdPart(info.getThird_part());//第三方对接平台：''未选择、XMGJ小马管家、selfService自营服务
		basicGasqEshop.setPublish(info.getPublish());//店铺审核状态：yes 审核通过 no 未审核 fail 未通过
		if(gasqEshop == null) {//create新增
			User user = new User();
			user.setId("gasq001");
			basicGasqEshop.setId(IdGen.uuid());
			basicGasqEshop.setCreateBy(user);
			basicGasqEshop.setCreateDate(new Date());
			basicGasqEshop.setUpdateBy(user);
			basicGasqEshop.setUpdateDate(basicGasqEshop.getCreateDate());

			basicGasqEshopDao.insert(basicGasqEshop);
		}else{//update修改
			User user = new User();
			user.setId("gasq001");
			basicGasqEshop.setUpdateBy(user);
			basicGasqEshop.setUpdateDate(new Date());

			basicGasqEshopDao.update(basicGasqEshop);
		}

	}
}