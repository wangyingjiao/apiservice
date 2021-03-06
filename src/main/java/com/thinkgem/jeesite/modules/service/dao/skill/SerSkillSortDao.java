/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.skill;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;

import java.util.List;

/**
 * 技能服务项目关联DAO接口
 * @author a
 * @version 2017-12-04
 */
@MyBatisDao
public interface SerSkillSortDao extends CrudDao<SerSkillSort> {

    void delSerSkillSortBySkill(SerSkillInfo serSkillInfo);

    List<String> findSortIdList(SerSortInfo serSortInfo);
}