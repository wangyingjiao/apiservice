/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单信息Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderInfo extends DataEntity<OrderInfo> {
	
	private static final long serialVersionUID = 1L;
	private String orderNumber;		// 订单流水号
	private String customId;		// 客户ID
	private Date orderTime;		// 下单时间
	private Date serTime;		// 服务时间
	private String orderStatus;		// 订单状态(1:待派单;2:已派单;3:已取消;4:已上门;5:已完成;6:已关闭;)
	private String orderSource;		// 订单来源
	private String customRemark;		// 备注
	private String servicerName;		// 业务人员姓名
	private String servicerPhone;		// 业务人员电话
	private String shopName;		// 门店名称
	private String shopPhone;		// 门店电话
	private String shopAddr;		// 门店地址
	private String officeId;		// 所属服务机构ID
	private String officeName;		// 所属服务机构名称
	private String stationId;		// 所属服务站ID
	private String stationName;		// 所属服务站名称
	
	private OrderCustomInfo customInfo; // 客户信息
	private OrderPayInfo payInfo; //支付信息
	private OrderReturn returnInfo; // 退款信息
	private List<OrderTech> orderTechs; //技师列表
	private List<OrderItemCommodity> OrderItems; //项目商品列表
	
//	private String customName;		// 客户姓名
//	private String customPhone;		// 客户电话
//	
//	private String payMode;		    // 支付方式(1:微信;2:支付宝;3:现金;4:银行卡;5:钱包;)
//	private String payTime;		    // 支付时间
//	private String payAccount;		// 支付金额
//	private String payStatus;		// 支付状态(1:待支付;2:已支付;)
	
	public OrderInfo() {
		super();
	}

	public OrderInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="订单流水号长度必须介于 0 和 64 之间")
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Length(min=0, max=64, message="客户ID长度必须介于 0 和 64 之间")
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSerTime() {
		return serTime;
	}

	public void setSerTime(Date serTime) {
		this.serTime = serTime;
	}
	
	@Length(min=0, max=1, message="订单状态长度必须介于 0 和 1 之间")
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	@Length(min=0, max=255, message="订单来源长度必须介于 0 和 255 之间")
	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getCustomRemark() {
		return customRemark;
	}

	public void setCustomRemark(String customRemark) {
		this.customRemark = customRemark;
	}
	
	@Length(min=0, max=64, message="业务人员姓名长度必须介于 0 和 64 之间")
	public String getServicerName() {
		return servicerName;
	}

	public void setServicerName(String servicerName) {
		this.servicerName = servicerName;
	}
	
	@Length(min=0, max=32, message="业务人员电话长度必须介于 0 和 32 之间")
	public String getServicerPhone() {
		return servicerPhone;
	}

	public void setServicerPhone(String servicerPhone) {
		this.servicerPhone = servicerPhone;
	}
	
	@Length(min=0, max=255, message="门店名称长度必须介于 0 和 255 之间")
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	@Length(min=0, max=32, message="门店电话长度必须介于 0 和 32 之间")
	public String getShopPhone() {
		return shopPhone;
	}

	public void setShopPhone(String shopPhone) {
		this.shopPhone = shopPhone;
	}
	
	@Length(min=0, max=255, message="门店地址长度必须介于 0 和 255 之间")
	public String getShopAddr() {
		return shopAddr;
	}

	public void setShopAddr(String shopAddr) {
		this.shopAddr = shopAddr;
	}

	@Length(min=0, max=64, message="所属服务机构名称长度必须介于 0 和64 之间")
	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	@Length(min=0, max=255, message="所属服务机构名称长度必须介于 0 和 255 之间")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	@Length(min=0, max=64, message="所属服务站ID长度必须介于 0 和 64 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	@Length(min=0, max=255, message="所属服务站名称长度必须介于 0 和 255 之间")
	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<OrderTech> getOrderTechs() {
		return orderTechs;
	}

	public void setOrderTechs(List<OrderTech> orderTechs) {
		this.orderTechs = orderTechs;
	}

	public List<OrderItemCommodity> getOrderItems() {
		return OrderItems;
	}

	public void setOrderItems(List<OrderItemCommodity> orderItems) {
		OrderItems = orderItems;
	}

	public OrderCustomInfo getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(OrderCustomInfo customInfo) {
		this.customInfo = customInfo;
	}

	public OrderPayInfo getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(OrderPayInfo payInfo) {
		this.payInfo = payInfo;
	}

	public OrderReturn getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(OrderReturn returnInfo) {
		this.returnInfo = returnInfo;
	}
	
}