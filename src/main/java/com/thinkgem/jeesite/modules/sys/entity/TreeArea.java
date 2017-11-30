/**
 * 
 */
package com.thinkgem.jeesite.modules.sys.entity;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * apiservice com.thinkgem.jeesite.modules.sys.entity TreeArea.java
 *
 * @author hsl
 *
 * 2017年11月30日 下午4:12:11
 *
 * desc:
 */
public class TreeArea extends DataEntity<TreeArea> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8217662232904534202L;
	private String id;
	private String name;
	private List<TreeArea> subs;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TreeArea> getSubs() {
		return subs;
	}
	public void setSubs(List<TreeArea> subs) {
		this.subs = subs;
	}

}
