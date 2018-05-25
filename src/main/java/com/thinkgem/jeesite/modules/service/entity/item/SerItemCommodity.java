/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 服务项目商品信息Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemCommodity extends DataEntity<SerItemCommodity> {
	
	private static final long serialVersionUID = 1L;
	private String itemId;		// 服务项目编号
	private String name;		// 商品名称
	private String unit;		// 商品单位
	private String type;		// 计量方式(num：按数量 area：按面积 house：按居室)
	private BigDecimal price;		// 价格
	private String doublePrice;
	private Double convertHours;		// 折算时长
	private int minPurchase;		// 起购数量
	private int startPerNum;   		//起步人数（第一个4小时时长派人数量）
	private int cappingPerNum;		//封项人数

	private String sortId;//分类ID
	private String sortName;//分类名
	private String itemName; //项目名
	private String majorSort;//分类(all:全部 clean:保洁 repair:家修)
	private String eshopCode; //E店code
    private String itemGoodName;  //拼接后的名字
    private String priceUnit;		//拼接后的单价
	private List<String> goodIds;


	private List<SerItemCommodityEshop> commodityEshops;

	private String jointCode;
	private String orgId;

	private String goodsType;  //商品类型
	private List<CombinationCommodity> combinationCommodities;
	private String serviceType;    //服务类型
	private List<SerItemInfo> serItemInfos;

	private String jointEshopFlag;  //用于判断是否显示已对接E店按钮

	public List<SerItemInfo> getSerItemInfos() {
		return serItemInfos;
	}

	public void setSerItemInfos(List<SerItemInfo> serItemInfos) {
		this.serItemInfos = serItemInfos;
	}

	private String jointGoodsCode;		//对接方商品CODE
	private String selfCode;		//自营商品CODE

	private Boolean commodityChecked = false;

	private int goodsNum;		// 商品数量
	private BigDecimal goodsPrice;		// 商品价格

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public List<CombinationCommodity> getCombinationCommodities() {
		return combinationCommodities;
	}

	public void setCombinationCommodities(List<CombinationCommodity> combinationCommodities) {
		this.combinationCommodities = combinationCommodities;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getDoublePrice() {
		return doublePrice;
	}

	public void setDoublePrice(String doublePrice) {
		this.doublePrice = doublePrice;
	}

	public String getJointEshopFlag() {
		return jointEshopFlag;
	}

	public void setJointEshopFlag(String jointEshopFlag) {
		this.jointEshopFlag = jointEshopFlag;
	}

	public List<String> getGoodIds() {
		return goodIds;
	}

	public void setGoodIds(List<String> goodIds) {
		this.goodIds = goodIds;
	}

	public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getItemGoodName() {
        return itemGoodName;
    }

    public void setItemGoodName(String itemGoodName) {
        this.itemGoodName = itemGoodName;
    }

    public String getEshopCode() {
		return eshopCode;
	}

	public void setEshopCode(String eshopCode) {
		this.eshopCode = eshopCode;
	}

	public String getJointCode() {
		return jointCode;
	}

	public void setJointCode(String jointCode) {
		this.jointCode = jointCode;
	}

	public List<SerItemCommodityEshop> getCommodityEshops() {
		return commodityEshops;
	}

	public void setCommodityEshops(List<SerItemCommodityEshop> commodityEshops) {
		this.commodityEshops = commodityEshops;
	}

	public int getStartPerNum() {
		return startPerNum;
	}

	public void setStartPerNum(int startPerNum) {
		this.startPerNum = startPerNum;
	}

	public int getCappingPerNum() {
		return cappingPerNum;
	}

	public void setCappingPerNum(int cappingPerNum) {
		this.cappingPerNum = cappingPerNum;
	}

	public String getJointGoodsCode() {
		return jointGoodsCode;
	}

	public void setJointGoodsCode(String jointGoodsCode) {
		this.jointGoodsCode = jointGoodsCode;
	}

	public String getSelfCode() {
		return selfCode;
	}

	public void setSelfCode(String selfCode) {
		this.selfCode = selfCode;
	}

	public SerItemCommodity() {
		super();
	}

	public SerItemCommodity(String id){
		super(id);
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@NotBlank(message = "商品名称不可为空")
	//@Length(min=2, max=10, message="商品名称长度必须介于 2 和 10 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank(message = "商品单位不可为空")
	@Length(min=1, max=10, message="商品单位长度必须介于 1 和 10 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@NotBlank(message = "计量方式不可为空")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@NotBlank(message = "价格不可为空")
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@NotBlank(message = "折算时长不可为空")
	public Double getConvertHours() {
		return convertHours;
	}

	public void setConvertHours(Double convertHours) {
		this.convertHours = convertHours;
	}
	
	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	@JsonInclude
	public Boolean getCommodityChecked() {
		return commodityChecked;
	}

	public void setCommodityChecked(Boolean commodityChecked) {
		this.commodityChecked = commodityChecked;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getMajorSort() {
		return majorSort;
	}

	public void setMajorSort(String majorSort) {
		this.majorSort = majorSort;
	}

	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

	public BigDecimal getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(BigDecimal goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
}