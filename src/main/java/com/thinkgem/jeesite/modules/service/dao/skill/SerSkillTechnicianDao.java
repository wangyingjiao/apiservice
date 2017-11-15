/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.skill;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;

/**
 * 技师技能关联DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerSkillTechnicianDao extends CrudDao<SerSkillTechnician> {
	
}