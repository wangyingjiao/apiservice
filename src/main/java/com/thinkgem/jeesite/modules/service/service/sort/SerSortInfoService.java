/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortInfoDao;

/**
 * 服务分类Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSortInfoService extends CrudService<SerSortInfoDao, SerSortInfo> {

	public SerSortInfo get(String id) {
		return super.get(id);
	}
	
	public List<SerSortInfo> findList(SerSortInfo serSortInfo) {
		return super.findList(serSortInfo);
	}
	
	public Page<SerSortInfo> findPage(Page<SerSortInfo> page, SerSortInfo serSortInfo) {
		return super.findPage(page, serSortInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(SerSortInfo serSortInfo) {
		super.save(serSortInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerSortInfo serSortInfo) {
		super.delete(serSortInfo);
	}
	
}