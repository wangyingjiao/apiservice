package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 更新订单信息RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendCreartCombinationOrderInfo extends DataEntity<OpenSendCreartCombinationOrderInfo> {

    private static final long serialVersionUID = 1L;

    private String order_sn;
    private String gasq_order_sn;

    private String service_time;// 上门服务时间；
    private List<OpenSendSaveOrderServiceTechInfo> service_tech_info;// 技师信息

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

    public List<OpenSendSaveOrderServiceTechInfo> getService_tech_info() {
        return service_tech_info;
    }

    public void setService_tech_info(List<OpenSendSaveOrderServiceTechInfo> service_tech_info) {
        this.service_tech_info = service_tech_info;
    }
}