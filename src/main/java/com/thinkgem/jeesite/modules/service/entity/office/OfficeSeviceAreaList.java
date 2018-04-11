/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.office;

import com.thinkgem.jeesite.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.Area;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 机构服务城市列表Entity
 * @author a
 * @version 2017-11-16
 */
public class OfficeSeviceAreaList extends DataEntity<OfficeSeviceAreaList> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// 机构id
	private String officeName;		// 机构名称
	private Area area;		// 区域id
	private String areaName;		// 区域名称
	private String sort;		// 排序
	
	public OfficeSeviceAreaList() {
		super();
	}

	public OfficeSeviceAreaList(String id){
		super(id);
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=255, message="机构名称长度必须介于 0 和 255 之间")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	@Length(min=0, max=255, message="区域名称长度必须介于 0 和 255 之间")
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}