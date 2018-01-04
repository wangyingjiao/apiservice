/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;

import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillSortDao;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortInfoDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 技能服务项目关联Service
 * @author a
 * @version 2017-12-04
 */
@Service
@Transactional(readOnly = true)
public class SerSkillSortService extends CrudService<SerSkillSortDao, SerSkillSort> {

	public SerSkillSort get(String id) {
		return super.get(id);
	}
	
	public List<SerSkillSort> findList(SerSkillSort serSkillSort) {
		return  super.findList(serSkillSort);
	}
	
	public Page<SerSkillSort> findPage(Page<SerSkillSort> page, SerSkillSort serSkillSort) {
		return super.findPage(page, serSkillSort);
	}
	
	@Transactional(readOnly = false)
	public void save(SerSkillSort serSkillSort) {
		super.save(serSkillSort);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerSkillSort serSkillSort) {
		super.delete(serSkillSort);
	}
	
}