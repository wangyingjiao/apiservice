/**
 * 
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * apiservice com.thinkgem.jeesite.modules.sys.entity AreaTree.java
 *
 * @author a
 *
 * 2017年11月30日 下午4:12:11
 *
 * desc:
 */
public class SortTree extends DataEntity<SortTree> {
	private static final long serialVersionUID = -8217662232904534202L;

	private String value;  //等级
	private String label;	//name
	private List<SortTree> children;


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<SortTree> getChildren() {
		return children;
	}

	public void setChildren(List<SortTree> children) {
		this.children = children;
	}
}
