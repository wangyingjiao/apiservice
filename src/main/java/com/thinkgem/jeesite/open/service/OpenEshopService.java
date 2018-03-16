/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicGasqEshopDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.open.entity.OpenEshopInfo;
import com.thinkgem.jeesite.open.entity.OpenEshopInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		basicGasqEshop.setCode(openEshopInfoRequest.getCode());//E店编码
		basicGasqEshopDao.delete(basicGasqEshop);
	}

	@Transactional(readOnly = false)
	public void save(OpenEshopInfoRequest openEshopInfoRequest) {
		List<OpenEshopInfo> eshopinfo = openEshopInfoRequest.getEshopinfo();
		if(eshopinfo != null){
			for(OpenEshopInfo info : eshopinfo){
				BasicGasqEshop basicGasqEshop = new BasicGasqEshop();
				basicGasqEshop.setName(info.getName());//E店名称
				basicGasqEshop.setBusinessModelId(info.getBusiness_model_id());//业务模式ID
				basicGasqEshop.setSellerId(info.getSeller_id());//商家ID
				basicGasqEshop.setCode(info.getCode());//E店编码
				basicGasqEshop.setOperationBaseStatus(info.getOperation_base_status());//运营基本信息审核状态：no未审核；submit审核中；fail审核未通过；yes审核通过'
				basicGasqEshop.setThirdPart(info.getThird_part());//第三方对接平台：''未选择、XMGJ小马管家、selfService自营服务
				basicGasqEshop.setPublish(info.getPublish());//店铺审核状态：yes 审核通过 no 未审核 fail 未通过
				if("create".equals(openEshopInfoRequest.getOperate())) {//create新增、update修改
					basicGasqEshop.preInsert();
					basicGasqEshopDao.insert(basicGasqEshop);
				}else if("update".equals(openEshopInfoRequest.getOperate())){
					basicGasqEshop.preInsert();
					basicGasqEshopDao.update(basicGasqEshop);
				}
			}
		}
	}
}