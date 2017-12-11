package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务项目信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_item_info")
public class ItemInfo extends BaseEntity<ItemInfo> {

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
     * 所属分类编号
     */
	@TableField("sort_id")
	private String sortId;
    /**
     * 服务项目名称
     */
	private String name;
    /**
     * 服务图片缩略图
     */
	private String picture;
    /**
     * 图文详情
     */
	@TableField("picture_detail")
	private String pictureDetail;
    /**
     * 服务描述
     */
	private String description;
    /**
     * 是否上架(yes:是，no:否)
     */
	private String sale;
    /**
     * 排序号
     */
	@TableField("sort_num")
	private Integer sortNum;
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

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPictureDetail() {
		return pictureDetail;
	}

	public void setPictureDetail(String pictureDetail) {
		this.pictureDetail = pictureDetail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
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
		return "ItemInfo{" +
			", orgId=" + orgId +
			", majorSort=" + majorSort +
			", sortId=" + sortId +
			", name=" + name +
			", picture=" + picture +
			", pictureDetail=" + pictureDetail +
			", description=" + description +
			", sale=" + sale +
			", sortNum=" + sortNum +
			", allCity=" + allCity +
			"}";
	}
}
