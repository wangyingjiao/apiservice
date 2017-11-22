/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityPersonsDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityPersons;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务项目派人数量信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemCommodityPersonsService extends CrudService<SerItemCommodityPersonsDao, SerItemCommodityPersons> {

	public SerItemCommodityPersons get(String id) {
		return super.get(id);
	}

	public List<SerItemCommodityPersons> findList(SerItemCommodityPersons serItemCommodityPersons) {
		return super.findList(serItemCommodityPersons);
	}

	public Page<SerItemCommodityPersons> findPage(Page<SerItemCommodityPersons> page, SerItemCommodityPersons serItemCommodityPersons) {
		return super.findPage(page, serItemCommodityPersons);
	}

	@Transactional(readOnly = false)
	public void save(SerItemCommodityPersons serItemCommodityPersons) {
		super.save(serItemCommodityPersons);
	}

	@Transactional(readOnly = false)
	public void delete(SerItemCommodityPersons serItemCommodityPersons) {
		super.delete(serItemCommodityPersons);
	}

}