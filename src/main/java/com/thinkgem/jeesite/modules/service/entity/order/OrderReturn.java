/**
 * 
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * apiservice com.thinkgem.jeesite.modules.service.entity.order OrderReturn.java
 *
 * @author hsl
 *
 * 2017年12月1日 上午10:29:31
 *
 * desc:
 */
public class OrderReturn extends DataEntity<OrderReturn> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1194713373206650318L;
	private String orderId;		    // 订单ID
	private String applyTime;	    // 申请时间
	private String returnAccount;	// 退款金额
	private String returnStatus;    // 退款状态(1:申请退款中;2:已取消;3:退款成功;4:拒绝退款;)
	private String returnCause;		// 退款原因
	private String returnRefuse;    // 拒绝原因
	
	public OrderReturn() {
		super();
	}
	
	public OrderReturn(String id) {
		super(id);
	}

	@Length(min=0, max=64, message="订单ID长度必须介于 0 和 64 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	@Length(min=0, max=1, message="退款状态必须为【1-4】之间的1位数字")
    @Pattern(regexp = "^[1-4]{1}$", message = "退款状态必须为【1-4】之间的1位数字") 
	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

    @Pattern(regexp = "^(-?\\\\d+)(\\\\.\\\\d+)?$", message = "退款金额请填写数字") 
	public String getReturnAccount() {
		return returnAccount;
	}

	public void setReturnAccount(String returnAccount) {
		this.returnAccount = returnAccount;
	}

	@Length(min=0, max=64, message="退款原因长度必须介于 0 和 255之间")
	public String getReturnCause() {
		return returnCause;
	}

	public void setReturnCause(String returnCause) {
		this.returnCause = returnCause;
	}

	@Length(min=0, max=64, message="拒绝原因长度必须介于 0 和255之间")
	public String getReturnRefuse() {
		return returnRefuse;
	}

	public void setReturnRefuse(String returnRefuse) {
		this.returnRefuse = returnRefuse;
	}
	

}
