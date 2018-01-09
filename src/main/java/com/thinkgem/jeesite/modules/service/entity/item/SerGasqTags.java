/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 标签Entity
 * @author a
 * @version 2017-11-15
 */
public class SerGasqTags extends DataEntity<SerGasqTags> {

	private static final long serialVersionUID = 1L;
	private String sortId;		// 分类id
	private int refCount;
	private String tagName;	// 名称
	private int sort;		// 排序
	private int version;		// 操作版本

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}