package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务分类基本信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_sort_info")
public class SortInfo extends BaseEntity<SortInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 分类(all:全部 clean:保洁 repair:家修)
     */
	@TableField("major_sort")
	private String majorSort;
    /**
     * 服务分类名称
     */
	private String name;
    /**
     * 全部城市(yes:是，no:否)
     */
	@TableField("all_city")
	private String allCity;


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getMajorSort() {
		return majorSort;
	}

	public void setMajorSort(String majorSort) {
		this.majorSort = majorSort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAllCity() {
		return allCity;
	}

	public void setAllCity(String allCity) {
		this.allCity = allCity;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SortInfo{" +
			", orgId=" + orgId +
			", majorSort=" + majorSort +
			", name=" + name +
			", allCity=" + allCity +
			"}";
	}
}
