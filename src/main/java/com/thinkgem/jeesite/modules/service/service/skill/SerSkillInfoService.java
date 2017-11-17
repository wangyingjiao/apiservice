/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.List;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillInfoDao;

/**
 * 技能管理Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSkillInfoService extends CrudService<SerSkillInfoDao, SerSkillInfo> {
	@Autowired
	SerSkillInfoDao serSkillInfoDao;

	public SerSkillInfo get(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void save(SerSkillInfo serSkillInfo) {
		super.save(serSkillInfo);
	}

	public List<SerSkillInfo> findList(SerSkillInfo serSkillInfo) {
		return super.findList(serSkillInfo);
	}
	
	public Page<SerSkillInfo> findPage(Page<SerSkillInfo> page, SerSkillInfo serSkillInfo) {
		return super.findPage(page, serSkillInfo);
	}

	public SerSkillInfo getData(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void delete(SerSkillInfo serSkillInfo) {
		super.delete(serSkillInfo);
	}


	/**
	 * 检查技能名是否重复
	 * @param serSkillInfo
	 * @return
	 */
	public int checkDataName(SerSkillInfo serSkillInfo) {
		User user = UserUtils.getUser();
		if (null != user) {
			//serSkillInfo.setOfficeId(user.getOfficeId());
		}
		return serSkillInfoDao.checkDataName(serSkillInfo);
	}
}