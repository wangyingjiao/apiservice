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
	private Area area;
	private List<TreeArea> subs;
	
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public List<TreeArea> getSubs() {
		return subs;
	}
	public void setSubs(List<TreeArea> subs) {
		this.subs = subs;
	}

}
