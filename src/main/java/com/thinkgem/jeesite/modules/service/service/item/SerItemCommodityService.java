/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityPersons;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	SerItemCommodityDao serItemCommodityDao;
	@Autowired
	SerItemCommodityPersonsService serItemCommodityPersonsService;

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
		//删除商品信息派人数量
		serItemCommodityDao.delSerItemCommodityPersons(serItemCommodity);
		super.save(serItemCommodity);
		List<SerItemCommodityPersons> persons = serItemCommodity.getPersons();
		//批量插入派人数量
		for(SerItemCommodityPersons person : persons){
			person.setCommodityId(serItemCommodity.getId());
			person.setCommodityName(serItemCommodity.getName());
			serItemCommodityPersonsService.save(person);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(SerItemCommodity serItemCommodity) {
		super.delete(serItemCommodity);
	}

    public void delSerItemCommodityPersons(SerItemInfo serItemInfo) {
		List<SerItemCommodity> list = serItemCommodityDao.getSerItemCommodityByItem(serItemInfo);
		for(SerItemCommodity info : list){
			serItemCommodityDao.delSerItemCommodityPersons(info);
		}
    }
}