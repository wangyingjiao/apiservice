package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 设置预约时间子订单RequestEntity
 * @author a
 * @version 2018-05-24
 */
public class OpenSendUpdateGroupComboOrder extends DataEntity<OpenSendUpdateGroupComboOrder> {

    private static final long serialVersionUID = 1L;

    private String order_sn;    //自营订单编号
    private String gasq_order_sn;  //国安订单编号
    private String service_time;   //服务时间

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getGasq_order_sn() {
        return gasq_order_sn;
    }

    public void setGasq_order_sn(String gasq_order_sn) {
        this.gasq_order_sn = gasq_order_sn;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }
}
