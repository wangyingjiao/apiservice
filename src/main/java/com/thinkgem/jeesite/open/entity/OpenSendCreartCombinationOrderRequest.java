package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 更新订单信息RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendCreartCombinationOrderRequest extends DataEntity<OpenSendCreartCombinationOrderRequest> {

    private static final long serialVersionUID = 1L;

    private String group_id;
    private String master_id;
    private List<OpenSendCreartCombinationOrderInfo> order_list;// 订单信息

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public List<OpenSendCreartCombinationOrderInfo> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OpenSendCreartCombinationOrderInfo> order_list) {
        this.order_list = order_list;
    }
}