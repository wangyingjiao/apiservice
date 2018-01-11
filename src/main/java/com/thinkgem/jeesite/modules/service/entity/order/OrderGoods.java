/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 订单服务关联Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderGoods extends DataEntity<OrderGoods> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String sortId;
	private String itemId;		// 服务项目ID
	private String itemName;		// 项目名称
	private String picture;			//服务图片
	private String goodsId;		// 商品ID
	private String goodsName;		// 商品名称
	private int goodsNum;		// 订购商品数
	private String originPrice;		// 单价原价
	private String payPrice;		// 单价(折后价，无折扣时和原价相同)
	private String goodsType;		// 计量方式(num：按数量 area：按面积 house：按居室)
	private int minPurchase;		// 起购数量
	private List<String> pics;
	private Date serviceTime;		// 服务时间
	private boolean goodsChecked = false;
	private List<OrderGoods> goods; //商品list
	private List<OrderGoodsTypeHouse> houses; //按居室商品列表


	private Double convertHours;		// 折算时长
	private int startPerNum;   		//起步人数（第一个4小时时长派人数量）
	private int cappingPerNum;		//封项人数
	
	public OrderGoods() {
		super();
	}

	public OrderGoods(String id){
		super(id);
	}

	@Length(min=1, max=32, message="订单ID长度必须介于 1 和 32 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	@Length(min=1, max=32, message="服务项目ID长度必须介于 1 和 32 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	@Length(min=0, max=32, message="项目名称长度必须介于 0 和 32 之间")
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	@Length(min=1, max=32, message="商品ID长度必须介于 1 和 32 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	@Length(min=0, max=32, message="商品名称长度必须介于 0 和 32 之间")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	@Length(min=0, max=11, message="订购商品数长度必须介于 0 和 11 之间")
	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
	
	public String getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(String originPrice) {
		this.originPrice = originPrice;
	}
	
	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Date serviceTime) {
		this.serviceTime = serviceTime;
	}

	public List<OrderGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<OrderGoods> goods) {
		this.goods = goods;
	}

	public boolean getGoodsChecked() {
		return goodsChecked;
	}

	public void setGoodsChecked(boolean goodsChecked) {
		this.goodsChecked = goodsChecked;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	public List<OrderGoodsTypeHouse> getHouses() {
		return houses;
	}

	public void setHouses(List<OrderGoodsTypeHouse> houses) {
		this.houses = houses;
	}

	public Double getConvertHours() {
		return convertHours;
	}

	public void setConvertHours(Double convertHours) {
		this.convertHours = convertHours;
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

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public List<String> getPics() {
		return pics;
	}

	public void setPics(List<String> pics) {
		this.pics = pics;
	}
}