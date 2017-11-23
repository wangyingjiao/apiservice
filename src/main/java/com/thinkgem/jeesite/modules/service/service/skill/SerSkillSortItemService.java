/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillSortItemDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSortItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 技能管理商品信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSkillSortItemService extends CrudService<SerSkillSortItemDao, SerSkillSortItem> {
	public SerSkillSortItem get(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void save(SerSkillSortItem serSkillSortItem) {
		super.save(serSkillSortItem);
	}

	@Transactional(readOnly = false)
	public void delete(SerSkillSortItem serSkillSortItem) {
		super.delete(serSkillSortItem);
	}
}