package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 更新订单信息RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveOrderRequest extends DataEntity<OpenSendSaveOrderRequest> {

    private static final long serialVersionUID = 1L;

    private String service_time;// 上门服务时间；上门服务时间和服务人员备注必传其一
    private List<OpenSendSaveOrderServiceTechInfo> service_tech_info;// 技师信息
    private String order_id;// 订单ID
    private String order_remark;// 服务人员备注 ；上门服务时间和服务人员备注必传其一

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

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_remark() {
        return order_remark;
    }

    public void setOrder_remark(String order_remark) {
        this.order_remark = order_remark;
    }
}