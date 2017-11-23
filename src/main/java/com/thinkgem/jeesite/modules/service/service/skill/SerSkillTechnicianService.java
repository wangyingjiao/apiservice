/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 技能管理技师信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSkillTechnicianService extends CrudService<SerSkillTechnicianDao, SerSkillTechnician> {
	public SerSkillTechnician get(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void save(SerSkillTechnician serSkillTechnician) {
		super.save(serSkillTechnician);
	}

	@Transactional(readOnly = false)
	public void delete(SerSkillTechnician serSkillTechnician) {
		super.delete(serSkillTechnician);
	}
}