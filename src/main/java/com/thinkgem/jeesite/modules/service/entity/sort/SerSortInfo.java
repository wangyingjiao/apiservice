/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务分类Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSortInfo extends DataEntity<SerSortInfo> {
	
	private static final long serialVersionUID = 1L;
	private String majorSort;		// 分类：保洁、家修
	private String name;		// 服务分类名称
	
	public SerSortInfo() {
		super();
	}

	public SerSortInfo(String id){
		super(id);
	}

	@Length(min=0, max=1, message="分类：保洁、家修长度必须介于 0 和 1 之间")
	public String getMajorSort() {
		return majorSort;
	}

	public void setMajorSort(String majorSort) {
		this.majorSort = majorSort;
	}
	
	@Length(min=0, max=64, message="服务分类名称长度必须介于 0 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}