/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.List;

import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemCommodityDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItemCommodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemDao;

/**
 * 技能服务项目关联Service
 * @author a
 * @version 2017-12-04
 */
@Service
@Transactional(readOnly = true)
public class SerSkillItemService extends CrudService<SerSkillItemDao, SerSkillItem> {

	@Autowired
	SerSkillItemCommodityDao serSkillItemCommodityDao;
	@Autowired
	SerSkillItemCommodityService serSkillItemCommodityService;

	public SerSkillItem get(String id) {
		return super.get(id);
	}
	
	public List<SerSkillItem> findList(SerSkillItem serSkillItem) {
		return super.findList(serSkillItem);
	}
	
	public Page<SerSkillItem> findPage(Page<SerSkillItem> page, SerSkillItem serSkillItem) {
		return super.findPage(page, serSkillItem);
	}
	
	@Transactional(readOnly = false)
	public void save(SerSkillItem serSkillItem) {
		//删除项目商品信息
		serSkillItemCommodityDao.delSerSkillItemCommodity(serSkillItem);
		super.save(serSkillItem);
		List<SerSkillItemCommodity> serItemCommoditys = serSkillItem.getSerItemCommoditys();
		//批量插入商品信息
		for(SerSkillItemCommodity serItemCommodity : serItemCommoditys){
			serItemCommodity.setItemId(serSkillItem.getItemId());
			serItemCommodity.setItemName(serSkillItem.getItemName());
			serSkillItemCommodityService.save(serItemCommodity);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(SerSkillItem serSkillItem) {
		//删除项目商品信息
		serSkillItemCommodityDao.delSerSkillItemCommodity(serSkillItem);

		super.delete(serSkillItem);
	}
	
}