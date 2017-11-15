/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;

/**
 * 服务项目商品信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemCommodityService extends CrudService<SerItemCommodityDao, SerItemCommodity> {

	public SerItemCommodity get(String id) {
		return super.get(id);
	}
	
	public List<SerItemCommodity> findList(SerItemCommodity serItemCommodity) {
		return super.findList(serItemCommodity);
	}
	
	public Page<SerItemCommodity> findPage(Page<SerItemCommodity> page, SerItemCommodity serItemCommodity) {
		return super.findPage(page, serItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void save(SerItemCommodity serItemCommodity) {
		super.save(serItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerItemCommodity serItemCommodity) {
		super.delete(serItemCommodity);
	}
	
}