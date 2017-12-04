/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItemCommodity;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemCommodityDao;

/**
 * 技能服务项目商品关联Service
 * @author a
 * @version 2017-12-04
 */
@Service
@Transactional(readOnly = true)
public class SerSkillItemCommodityService extends CrudService<SerSkillItemCommodityDao, SerSkillItemCommodity> {

	public SerSkillItemCommodity get(String id) {
		return super.get(id);
	}
	
	public List<SerSkillItemCommodity> findList(SerSkillItemCommodity serSkillItemCommodity) {
		return super.findList(serSkillItemCommodity);
	}
	
	public Page<SerSkillItemCommodity> findPage(Page<SerSkillItemCommodity> page, SerSkillItemCommodity serSkillItemCommodity) {
		return super.findPage(page, serSkillItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void save(SerSkillItemCommodity serSkillItemCommodity) {
		super.save(serSkillItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerSkillItemCommodity serSkillItemCommodity) {
		super.delete(serSkillItemCommodity);
	}
	
}