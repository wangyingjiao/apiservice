/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;

import java.util.Date;
import java.util.List;

/**
 * 组合订单Entity
 * @author a
 * @version 2017-12-26
 */
public class CombinationOrderInfo extends DataEntity<CombinationOrderInfo> {

	private static final long serialVersionUID = 1L;
	private String masterId;		// 主订单ID
    private String orderId;         //订单id
	private String orderType;		// 订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
	private String orgId;		// 所属服务机构ID
	private String orgName;         //机构
	private String stationId;		// 服务站id
	private String stationName;         //服务站
	private String majorSort;		// 分类(all:全部 clean:保洁 repair:家修)
	private String originPrice;		// 订单总价原价
	private String payPrice;		// 实际付款价格
	private String orderAddressId;		// 订单地址ID
	private String longitude;		// 经度
	private String latitude;		// 纬度
	private Date orderTime;		// 下单时间
    private Date startTime;		//开始时间   列表查询条件
    private Date endTime;		//结束时间  列表查询条件
	private String serviceFrequency;	// 服务频次 weekly:1周1次 many:1周多次 fortnightly:2周1次
	private Date serviceStart;		// 第一次服务日期
	private Double serviceHour; //服务时长（小时）
	private String techId; //固定技师ID
	private String orderStatus;		// 订单状态(dispatched:已下单;cancel:已取消;success:已成功;close:已关闭)
	private String orderSource;		// 订单来源(own:本机构 gasq:国安社区)
	private String jointOrderId;//对接订单ID
	private String payStatus;		// 支付状态（waitpay:待支付  payed：已支付）
	private String customerId;		// 客户ID
	private String customerAddressId;		// 客户地址ID
	private String customerRemark;		// 客户备注
	private String customerRemarkPic;		// 客户备注图片
	private List<String> customerRemarkPics;		// 客户备注图片
	private String eshopCode;
	private String orderContent;		// 下单服务内容
	private String cancelReason;//取消原因
	private String cancelReasonRemark;//取消原因备注
	private OrderAddress addressInfo;  //服务地址
	private String customerName;	//用户姓名
	private String customerPhone;	//用户电话
	private OrderPayInfo payInfo;	//支付信息
	private ServiceTechnicianInfo tech; //固定技师
	private List<OrderCombinationFrequencyInfo> freList;	//服务时间
	private List<OrderInfo> order;

	public CombinationOrderInfo() {
		super();
	}

	public CombinationOrderInfo(String id){
		super(id);
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

	public String getOrderAddressId() {
		return orderAddressId;
	}

	public void setOrderAddressId(String orderAddressId) {
		this.orderAddressId = orderAddressId;
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
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public String getServiceFrequency() {
		return serviceFrequency;
	}

	public void setServiceFrequency(String serviceFrequency) {
		this.serviceFrequency = serviceFrequency;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getServiceStart() {
		return serviceStart;
	}

	public void setServiceStart(Date serviceStart) {
		this.serviceStart = serviceStart;
	}

	public Double getServiceHour() {
		return serviceHour;
	}

	public void setServiceHour(Double serviceHour) {
		this.serviceHour = serviceHour;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
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

	public String getJointOrderId() {
		return jointOrderId;
	}

	public void setJointOrderId(String jointOrderId) {
		this.jointOrderId = jointOrderId;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerAddressId() {
		return customerAddressId;
	}

	public void setCustomerAddressId(String customerAddressId) {
		this.customerAddressId = customerAddressId;
	}

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

	public List<String> getCustomerRemarkPics() {
		return customerRemarkPics;
	}

	public void setCustomerRemarkPics(List<String> customerRemarkPics) {
		this.customerRemarkPics = customerRemarkPics;
	}

	public String getEshopCode() {
		return eshopCode;
	}

	public void setEshopCode(String eshopCode) {
		this.eshopCode = eshopCode;
	}

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

	public OrderAddress getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(OrderAddress addressInfo) {
		this.addressInfo = addressInfo;
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

	public OrderPayInfo getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(OrderPayInfo payInfo) {
		this.payInfo = payInfo;
	}

	public ServiceTechnicianInfo getTech() {
		return tech;
	}

	public void setTech(ServiceTechnicianInfo tech) {
		this.tech = tech;
	}

	public List<OrderCombinationFrequencyInfo> getFreList() {
		return freList;
	}

	public void setFreList(List<OrderCombinationFrequencyInfo> freList) {
		this.freList = freList;
	}

	public List<OrderInfo> getOrder() {
		return order;
	}

	public void setOrder(List<OrderInfo> order) {
		this.order = order;
	}
}