/**
 * 
 */
package com.thinkgem.jeesite.modules.service.entity.order;

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
	private String returnStatus;    // 退款状态
	private String returnCause;		// 退款原因
	private String returnRefuse;    // 拒绝原因
	
	public OrderReturn() {
		super();
	}
	
	public OrderReturn(String id) {
		super(id);
	}

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

	@Length(min=0, max=1, message="退款状态长度必须介于 0 和 1 之间")
	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getReturnAccount() {
		return returnAccount;
	}

	public void setReturnAccount(String returnAccount) {
		this.returnAccount = returnAccount;
	}

	public String getReturnCause() {
		return returnCause;
	}

	public void setReturnCause(String returnCause) {
		this.returnCause = returnCause;
	}

	public String getReturnRefuse() {
		return returnRefuse;
	}

	public void setReturnRefuse(String returnRefuse) {
		this.returnRefuse = returnRefuse;
	}
	

}
