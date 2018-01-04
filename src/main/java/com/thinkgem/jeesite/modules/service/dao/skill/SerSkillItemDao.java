/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.skill;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;

import java.util.List;

/**
 * 技能服务项目关联DAO接口
 * @author a
 * @version 2017-12-04
 */
@MyBatisDao
public interface SerSkillItemDao extends CrudDao<SerSkillItem> {

    void delSerSkillItemBySkill(SerSkillInfo serSkillInfo);

    List<SerSkillItem> getSerItems(SerSkillInfo serSkillInfo);

    List<SerSkillItem> getSerSkillItemBySkill(SerSkillInfo serSkillInfo);

    List<SerSkillItem> getSerSkillGoodsBySkill(SerSkillInfo serSkillInfo);
}