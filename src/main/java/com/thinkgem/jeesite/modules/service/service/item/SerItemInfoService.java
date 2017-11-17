/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;

/**
 * 服务项目Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemInfoService extends CrudService<SerItemInfoDao, SerItemInfo> {
	@Autowired
	SerItemInfoDao serItemInfoDao;

	public SerItemInfo get(String id) {
		return super.get(id);
	}

	/**
	 * 保存
	 * @param serItemInfo
	 */
	@Transactional(readOnly = false)
	public void save(SerItemInfo serItemInfo) {
		super.save(serItemInfo);
	}

	public List<SerItemInfo> findList(SerItemInfo serItemInfo) {
		return super.findList(serItemInfo);
	}
	
	public Page<SerItemInfo> findPage(Page<SerItemInfo> page, SerItemInfo serItemInfo) {
		return super.findPage(page, serItemInfo);
	}

	/**
	 * 根据ID获取服务项目
	 * @param id
	 * @return
	 */
	public SerItemInfo getData(String id) {
		return super.get(id);
	}

	
	@Transactional(readOnly = false)
	public void delete(SerItemInfo serItemInfo) {
		super.delete(serItemInfo);
	}

	/**
	 * 检查服务项目名是否重复
	 * @param serItemInfo
	 * @return
	 */
	public int checkDataName(SerItemInfo serItemInfo) {
		User user = UserUtils.getUser();
		if (null != user) {
			serItemInfo.setOfficeId(user.getOfficeId());
		}
		return serItemInfoDao.checkDataName(serItemInfo);
	}
}