/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分类Entity
 * @author a
 * @version 2017-11-15
 */
public class SerGasqSort extends DataEntity<SerGasqSort> {

	private static final long serialVersionUID = 1L;
	private String pid;		// 他的父级id  0:顶级,否则记录他的父级
	private String name;		// 名称
	private String contentImg;
	private int level;		// 分类等级
	private int sort;		// 排序
	private int version;		// 操作版本
	private int tagRefCount;
	private String hidden;		//是否隐藏 yes隐藏 no不隐藏

	private List<SerGasqSort> sortList;
	private List<SerGasqTags> tagsList;

	public List<SerGasqTags> getTagsList() {
		return tagsList;
	}

	public void setTagsList(List<SerGasqTags> tagsList) {
		this.tagsList = tagsList;
	}

	public List<SerGasqSort> getSortList() {
		return sortList;
	}

	public void setSortList(List<SerGasqSort> sortList) {
		this.sortList = sortList;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public int getTagRefCount() {
		return tagRefCount;
	}

	public void setTagRefCount(int tagRefCount) {
		this.tagRefCount = tagRefCount;
	}

	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
}