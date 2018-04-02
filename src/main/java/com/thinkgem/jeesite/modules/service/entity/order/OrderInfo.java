/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 子订单Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderInfo extends DataEntity<OrderInfo> {
	
	private static final long serialVersionUID = 1L;
	private String masterId;		// 主订单ID
	private String orderType;		// 订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
	private String orderNumber;		// 订单编号
	private String orgId;		// 所属服务机构ID
	private String stationId;		// 服务站id
	private String majorSort;		// 分类(all:全部 clean:保洁 repair:家修)
	private String originPrice;		// 订单总价原价
	private String majorSortName;	//app订单类型名称
	private String payPrice;		// 实际付款价格
	private String orderAddressId;		// 订单地址ID
	private String longitude;		// 经度
	private String latitude;		// 纬度
	private Date orderTime;		// 下单时间
	private Date serviceTime;		// 服务时间 上门时间
	private Date finishTime;		// 完成时间 实际完成时间（用来计算库存）
	private Date suggestFinishTime;		// 建议完成时间
	private Date realServiceTime;		//操作‘上门服务’动作的时间
	private Double serviceHour; //建议服务时长（小时）
	private String serviceStatus;//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
	private String orderStatus;		// 订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)
	private String orderSource;		// 订单来源(own:本机构 gasq:国安社区)
	private String orderSourceName; // 订单来源 app中文
	private String payStatus;		// 支付状态（waitpay:待支付  payed：已支付）
	private String customerId;		// 客户ID
	private String customerAddressId;		// 客户地址ID
	private String customerRemark;		// 客户备注
	private String customerRemarkPic;		// 客户备注图片
	private List<String> customerRemarkPics;		// 客户备注图片
	private String businessName;		// 业务人员姓名
	private String businessPhone;		// 业务人员电话
	private String businessRemark;		// 业务人员备注
	private String businessRemarkPic;		// 业务人员备注图片
	private List<String> businessRemarkPics;		// 业务人员备注图片
	private String shopName;		// 门店名称
	private String shopPhone;		// 门店电话
	private String shopAddr;		// 门店地址
	private String shopRemark;		// 门店备注
	private String shopRemarkPic;		// 门店备注图片
	private List<String> shopRemarkPics;		// 门店备注图片

	private String shopId;
	private String eshopCode;

	private String orderRemark;		// 订单备注（技师添加的）
	private String orderRemarkPic;   //订单备注图片
	private List<String> orderRemarkPics;   //订单备注图片
	private String orderContent;		// 下单服务内容
	private String cancelReason;//取消原因
	private String cancelReasonName;//取消原因中文
	private String cancelReasonRemark;//取消原因备注
	private String jointOrderId;//对接订单ID

	private String customerName;         //客户姓名
	private String customerPhone;         //客户电话
	private OrderCustomInfo customerInfo;
	private String orgName;         //机构
	private String stationName;         //服务站
	private Timestamp orderTimeStart;		// 下单起始时
	private Timestamp orderTimeEnd;		// 下单结束时
	private Timestamp serviceTimeStart;		// 服务时间 上门时间 起始时
	private Timestamp serviceTimeEnd;		// 服务时间 上门时间 结束时

	private OrderPayInfo payInfo;       // 支付信息
	//private OrderRefund refundInfo;    //退款信息
	private OrderAddress addressInfo;  //服务地址信息
	private OrderGoods goodsInfo;     //服务信息
	private List<OrderGoods> goodsInfoList;
	private List<OrderDispatch> techList; //技师List
	private List<String> techIdList; //技师List
	private String dispatchTechId;//改派前技师ID
	private String techName;
	private String techPhone;
	private BusinessInfo businessInfo; //业务人员
	private ShopInfo shopInfo;	//门店信息
	private BigDecimal openPrice;
	private String serviceStatusName;//app
	private String orderStatusName;//app
	private String payStatusName;//app
	private String nowId;  //app 当前技师id
	private List<AppServiceTechnicianInfo> appTechList;//app技师列表
	private String techId; //app改派接收的技师id
	private String isTech="no"; //app技师修改服务状态时做展示功能标识符
	private String pageSize;	//app 订单列表
	private String pageNo;	//app 订单列表
	private OrderPayInfo orderPayInfo; //app 订单的对应支付信息表

	public String getCancelReasonName() {
		return cancelReasonName;
	}

	public void setCancelReasonName(String cancelReasonName) {
		this.cancelReasonName = cancelReasonName;
	}

	public String getOrderSourceName() {
		return orderSourceName;
	}

	public void setOrderSourceName(String orderSourceName) {
		this.orderSourceName = orderSourceName;
	}

	public OrderPayInfo getOrderPayInfo() {
		return orderPayInfo;
	}

	public void setOrderPayInfo(OrderPayInfo orderPayInfo) {
		this.orderPayInfo = orderPayInfo;
	}

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public Date getRealServiceTime() {
		return realServiceTime;
	}

	public void setRealServiceTime(Date realServiceTime) {
		this.realServiceTime = realServiceTime;
	}

	public String getIsTech() {
		return isTech;
	}

	public void setIsTech(String isTech) {
		this.isTech = isTech;
	}

	private List<OrderTimeList> orderTimeList;

	public List<OrderTimeList> getOrderTimeList() {
		return orderTimeList;
	}

	public void setOrderTimeList(List<OrderTimeList> orderTimeList) {
		this.orderTimeList = orderTimeList;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getPayStatusName() {
		return payStatusName;
	}

	public void setPayStatusName(String payStatusName) {
		this.payStatusName = payStatusName;
	}

	public String getOrderStatusName() {
		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}

	public String getServiceStatusName() {
		return serviceStatusName;
	}

	public void setServiceStatusName(String serviceStatusName) {
		this.serviceStatusName = serviceStatusName;
	}

	public OrderInfo() {
		super();
	}

	public OrderInfo(String id){
		super(id);
	}

	public List<AppServiceTechnicianInfo> getAppTechList() {
		return appTechList;
	}

	public void setAppTechList(List<AppServiceTechnicianInfo> appTechList) {
		this.appTechList = appTechList;
	}

	@Length(min=1, max=32, message="主订单ID长度必须介于 1 和 32 之间")
	public String getMasterId() {
		return masterId;
	}

	public String getNowId() {
		return nowId;
	}

	public void setNowId(String nowId) {
		this.nowId = nowId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	@Length(min=0, max=32, message="订单编号长度必须介于 0 和 32 之间")
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Length(min=0, max=32, message="所属服务机构ID长度必须介于 0 和 32 之间")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	@Length(min=0, max=32, message="服务站id长度必须介于 0 和 32 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	public String getMajorSort() {
		return majorSort;
	}

	public void setMajorSort(String majorSort) {
		this.majorSort = majorSort;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSuggestFinishTime() {
		return suggestFinishTime;
	}

	public void setSuggestFinishTime(Date suggestFinishTime) {
		this.suggestFinishTime = suggestFinishTime;
	}

	public Double getServiceHour() {
		return serviceHour;
	}

	public void setServiceHour(Double serviceHour) {
		this.serviceHour = serviceHour;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
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
	
	@Length(min=0, max=32, message="订单地址ID长度必须介于 0 和 32 之间")
	public String getOrderAddressId() {
		return orderAddressId;
	}

	public void setOrderAddressId(String orderAddressId) {
		this.orderAddressId = orderAddressId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Date serviceTime) {
		this.serviceTime = serviceTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	
	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	
	@Length(min=0, max=32, message="客户ID长度必须介于 0 和 32 之间")
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@Length(min=0, max=255, message="客户备注长度必须介于 0 和 255 之间")
	public String getCustomerRemark() {
		return customerRemark;
	}

	public void setCustomerRemark(String customerRemark) {
		this.customerRemark = customerRemark;
	}
	
	public String getCustomerRemarkPic() {
		return customerRemarkPic;
	}

	public void setCustomerRemarkPic(String customerRemarkPic) {
		this.customerRemarkPic = customerRemarkPic;
	}
	
	@Length(min=0, max=32, message="业务人员姓名长度必须介于 0 和 32 之间")
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	@Length(min=0, max=16, message="业务人员电话长度必须介于 0 和 16 之间")
	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}
	
	@Length(min=0, max=255, message="业务人员备注长度必须介于 0 和 255 之间")
	public String getBusinessRemark() {
		return businessRemark;
	}

	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark;
	}
	
	public String getBusinessRemarkPic() {
		return businessRemarkPic;
	}

	public void setBusinessRemarkPic(String businessRemarkPic) {
		this.businessRemarkPic = businessRemarkPic;
	}
	
	@Length(min=0, max=32, message="门店名称长度必须介于 0 和 32 之间")
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	@Length(min=0, max=16, message="门店电话长度必须介于 0 和 16 之间")
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
	
	@Length(min=0, max=255, message="门店备注长度必须介于 0 和 255 之间")
	public String getShopRemark() {
		return shopRemark;
	}

	public void setShopRemark(String shopRemark) {
		this.shopRemark = shopRemark;
	}
	
	public String getShopRemarkPic() {
		return shopRemarkPic;
	}

	public void setShopRemarkPic(String shopRemarkPic) {
		this.shopRemarkPic = shopRemarkPic;
	}
	
	@Length(min=0, max=255, message="订单备注（技师添加的）长度必须介于 0 和 255 之间")
	public String getOrderRemark() {
		return orderRemark;
	}

	public void setOrderRemark(String orderRemark) {
		this.orderRemark = orderRemark;
	}

	public String getOrderRemarkPic() {
		return orderRemarkPic;
	}

	public void setOrderRemarkPic(String orderRemarkPic) {
		this.orderRemarkPic = orderRemarkPic;
	}

	@Length(min=0, max=255, message="下单服务内容长度必须介于 0 和 255 之间")
	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelReasonRemark() {
		return cancelReasonRemark;
	}

	public void setCancelReasonRemark(String cancelReasonRemark) {
		this.cancelReasonRemark = cancelReasonRemark;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Timestamp getOrderTimeStart() {
		return orderTimeStart;
	}

	public void setOrderTimeStart(Timestamp orderTimeStart) {
		this.orderTimeStart = orderTimeStart;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Timestamp getOrderTimeEnd() {
		return orderTimeEnd;
	}

	public void setOrderTimeEnd(Timestamp orderTimeEnd) {
		this.orderTimeEnd = orderTimeEnd;
	}

	public OrderPayInfo getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(OrderPayInfo payInfo) {
		this.payInfo = payInfo;
	}

/*	public OrderRefund getRefundInfo() {
		return refundInfo;
	}

	public void setRefundInfo(OrderRefund refundInfo) {
		this.refundInfo = refundInfo;
	}
	*/

	public OrderAddress getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(OrderAddress addressInfo) {
		this.addressInfo = addressInfo;
	}

	public OrderGoods getGoodsInfo() {
		return goodsInfo;
	}

	public void setGoodsInfo(OrderGoods goodsInfo) {
		this.goodsInfo = goodsInfo;
	}

	public List<OrderGoods> getGoodsInfoList() {
		return goodsInfoList;
	}

	public void setGoodsInfoList(List<OrderGoods> goodsInfoList) {
		this.goodsInfoList = goodsInfoList;
	}

	public List<OrderDispatch> getTechList() {
		return techList;
	}

	public void setTechList(List<OrderDispatch> techList) {
		this.techList = techList;
	}

	public List<String> getTechIdList() {
		return techIdList;
	}

	public void setTechIdList(List<String> techIdList) {
		this.techIdList = techIdList;
	}

	public String getDispatchTechId() {
		return dispatchTechId;
	}

	public void setDispatchTechId(String dispatchTechId) {
		this.dispatchTechId = dispatchTechId;
	}

	public List<String> getCustomerRemarkPics() {
		return customerRemarkPics;
	}

	public void setCustomerRemarkPics(List<String> customerRemarkPics) {
		this.customerRemarkPics = customerRemarkPics;
	}

	public List<String> getBusinessRemarkPics() {
		return businessRemarkPics;
	}

	public void setBusinessRemarkPics(List<String> businessRemarkPics) {
		this.businessRemarkPics = businessRemarkPics;
	}

	public List<String> getShopRemarkPics() {
		return shopRemarkPics;
	}

	public void setShopRemarkPics(List<String> shopRemarkPics) {
		this.shopRemarkPics = shopRemarkPics;
	}

	public List<String> getOrderRemarkPics() {
		return orderRemarkPics;
	}

	public void setOrderRemarkPics(List<String> orderRemarkPics) {
		this.orderRemarkPics = orderRemarkPics;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getTechPhone() {
		return techPhone;
	}

	public void setTechPhone(String techPhone) {
		this.techPhone = techPhone;
	}

	public BusinessInfo getBusinessInfo() {
		return businessInfo;
	}

	public void setBusinessInfo(BusinessInfo businessInfo) {
		this.businessInfo = businessInfo;
	}

	public ShopInfo getShopInfo() {
		return shopInfo;
	}

	public void setShopInfo(ShopInfo shopInfo) {
		this.shopInfo = shopInfo;
	}

	public OrderCustomInfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(OrderCustomInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	public String getMajorSortName() {
		return majorSortName;
	}

	public void setMajorSortName(String majorSortName) {
		this.majorSortName = majorSortName;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public String getJointOrderId() {
		return jointOrderId;
	}

	public void setJointOrderId(String jointOrderId) {
		this.jointOrderId = jointOrderId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getEshopCode() {
		return eshopCode;
	}

	public void setEshopCode(String eshopCode) {
		this.eshopCode = eshopCode;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Timestamp getServiceTimeStart() {
		return serviceTimeStart;
	}

	public void setServiceTimeStart(Timestamp serviceTimeStart) {
		this.serviceTimeStart = serviceTimeStart;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Timestamp getServiceTimeEnd() {
		return serviceTimeEnd;
	}

	public void setServiceTimeEnd(Timestamp serviceTimeEnd) {
		this.serviceTimeEnd = serviceTimeEnd;
	}

	public String getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(String customerAddressId) {
		this.customerAddressId = customerAddressId;
	}
}