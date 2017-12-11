package com.thinkgem.jeesite.modules.order.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 订单信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("order_info")
public class OrderInfo extends BaseEntity<OrderInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
	@TableField("order_number")
	private String orderNumber;
    /**
     * 所属服务机构ID
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 分类(all:全部 clean:保洁 repair:家修)
     */
	@TableField("major_sort")
	private String majorSort;
    /**
     * 订单总价原价
     */
	@TableField("origin_price")
	private BigDecimal originPrice;
    /**
     * 实际付款价格
     */
	@TableField("pay_price")
	private BigDecimal payPrice;
    /**
     * 订单地址
     */
	@TableField("order_addr")
	private String orderAddr;
    /**
     * 下单时间
     */
	@TableField("order_time")
	private Date orderTime;
    /**
     * 服务时间
     */
	@TableField("service_time")
	private Date serviceTime;
    /**
     * 完成时间
     */
	@TableField("finish_time")
	private Date finishTime;
    /**
     * 订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)
     */
	@TableField("order_status")
	private String orderStatus;
    /**
     * 订单来源(app:app callcenter:400 store:门店 wechat:微信 score:积分商城 web:PC tv:电视)
     */
	@TableField("order_source")
	private String orderSource;
    /**
     * 客户ID
     */
	@TableField("customer_id")
	private String customerId;
    /**
     * 客户备注
     */
	@TableField("customer_remark")
	private String customerRemark;
    /**
     * 客户备注图片
     */
	@TableField("customer_remark_pic")
	private String customerRemarkPic;
    /**
     * 业务人员姓名
     */
	@TableField("business_name")
	private String businessName;
    /**
     * 业务人员电话
     */
	@TableField("business_phone")
	private String businessPhone;
    /**
     * 业务人员备注
     */
	@TableField("business_remark")
	private String businessRemark;
    /**
     * 业务人员备注图片
     */
	@TableField("business_remark_pic")
	private String businessRemarkPic;
    /**
     * 门店名称
     */
	@TableField("shop_name")
	private String shopName;
    /**
     * 门店电话
     */
	@TableField("shop_phone")
	private String shopPhone;
    /**
     * 门店地址
     */
	@TableField("shop_addr")
	private String shopAddr;
    /**
     * 门店备注
     */
	@TableField("shop_remark")
	private String shopRemark;
    /**
     * 门店备注图片
     */
	@TableField("shop_remark_pic")
	private String shopRemarkPic;


	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

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

	public BigDecimal getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(BigDecimal originPrice) {
		this.originPrice = originPrice;
	}

	public BigDecimal getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(BigDecimal payPrice) {
		this.payPrice = payPrice;
	}

	public String getOrderAddr() {
		return orderAddr;
	}

	public void setOrderAddr(String orderAddr) {
		this.orderAddr = orderAddr;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Date serviceTime) {
		this.serviceTime = serviceTime;
	}

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

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

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

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopPhone() {
		return shopPhone;
	}

	public void setShopPhone(String shopPhone) {
		this.shopPhone = shopPhone;
	}

	public String getShopAddr() {
		return shopAddr;
	}

	public void setShopAddr(String shopAddr) {
		this.shopAddr = shopAddr;
	}

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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "OrderInfo{" +
			", orderNumber=" + orderNumber +
			", orgId=" + orgId +
			", majorSort=" + majorSort +
			", originPrice=" + originPrice +
			", payPrice=" + payPrice +
			", orderAddr=" + orderAddr +
			", orderTime=" + orderTime +
			", serviceTime=" + serviceTime +
			", finishTime=" + finishTime +
			", orderStatus=" + orderStatus +
			", orderSource=" + orderSource +
			", customerId=" + customerId +
			", customerRemark=" + customerRemark +
			", customerRemarkPic=" + customerRemarkPic +
			", businessName=" + businessName +
			", businessPhone=" + businessPhone +
			", businessRemark=" + businessRemark +
			", businessRemarkPic=" + businessRemarkPic +
			", shopName=" + shopName +
			", shopPhone=" + shopPhone +
			", shopAddr=" + shopAddr +
			", shopRemark=" + shopRemark +
			", shopRemarkPic=" + shopRemarkPic +
			"}";
	}
}
