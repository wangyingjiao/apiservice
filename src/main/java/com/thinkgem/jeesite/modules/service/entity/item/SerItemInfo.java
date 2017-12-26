/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务项目Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemInfo extends DataEntity<SerItemInfo> {
	
	private static final long serialVersionUID = 1L;
	private String majorSort;		// 分类(all:全部 clean:保洁 repair:家修)
	private String sortId;		// 所属分类编号
	private String name;		// 服务项目名称
	private String picture;		// 服务图片缩略图
	private List<String> pictures;		// 服务图片缩略图
	private String pictureDetail; //图文详情
	private List<String> pictureDetails; //图文详情
	private String description;		// 服务描述
	private String sale;		// 是否上架(yes:是，no:否)
	private int sortNum;		// 排序号
	private String allCity;//全部城市(yes:是，no:否)
	private String orgId;//机构ID

	public SerItemInfo() {
		super();
	}

	public SerItemInfo(String id){
		super(id);
	}

	@NotBlank(message = "服务分类：保洁、家修不可为空")
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

	@NotBlank(message = "服务项目名称不可为空")
	@Length(min=2, max=10, message="服务项目名称长度必须介于 2 和 10 之间")
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

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}

	public String getPictureDetail() {
		return pictureDetail;
	}

	public void setPictureDetail(String pictureDetail) {
		this.pictureDetail = pictureDetail;
	}

	public List<String> getPictureDetails() {
		return pictureDetails;
	}

	public void setPictureDetails(List<String> pictureDetails) {
		this.pictureDetails = pictureDetails;
	}

	@NotBlank(message = "服务描述不可为空")
	@Length(min=0, max=255, message="服务描述长度必须介于 0 和 255 之间")
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
	
	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getAllCity() {
		return allCity;
	}

	public void setAllCity(String allCity) {
		this.allCity = allCity;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	private String cityCode;        // 城市编号 查询用
	private List<String> cityCodes = new ArrayList<String>(); // 城市 编辑选中用
	private List<SerCityScope> citys = new ArrayList<SerCityScope>();
	private List<SerCityScope> allCitys = new ArrayList<SerCityScope>();
	private List<SerItemCommodity> commoditys;
	private String sortName;		// 所属分类

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@JsonInclude
	public List<String> getCityCodes() {
		return cityCodes;
	}

	public void setCityCodes(List<String> cityCodes) {
		this.cityCodes = cityCodes;
	}

	@JsonInclude
	public List<SerCityScope> getCitys() {
		return citys;
	}

	public void setCitys(List<SerCityScope> citys) {
		this.citys = citys;
	}

	public List<SerCityScope> getAllCitys() {
		return allCitys;
	}

	public void setAllCitys(List<SerCityScope> allCitys) {
		this.allCitys = allCitys;
	}

	@JsonInclude
	public List<SerItemCommodity> getCommoditys() {return commoditys;}
	public void setCommoditys(List<SerItemCommodity> commoditys) {this.commoditys = commoditys;}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	private Boolean itemChecked = false;
	@JsonInclude
	public Boolean getItemChecked() {
		return itemChecked;
	}

	public void setItemChecked(Boolean itemChecked) {
		this.itemChecked = itemChecked;
	}

	private List<Object> checkedCities = new ArrayList<Object>();
	@JsonInclude
	public List<Object> getCheckedCities() {
		return checkedCities;
	}

	public void setCheckedCities(List<Object> checkedCities) {
		this.checkedCities = checkedCities;
	}
}