/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.skill;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItemCommodity;

/**
 * 技能服务项目商品关联DAO接口
 * @author a
 * @version 2017-12-04
 */
@MyBatisDao
public interface SerSkillItemCommodityDao extends CrudDao<SerSkillItemCommodity> {

    void delSerSkillItemCommodity(SerSkillItem serSkillItem);
}