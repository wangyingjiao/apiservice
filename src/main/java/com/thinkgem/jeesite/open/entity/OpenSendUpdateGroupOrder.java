package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 设置预约时间订单RequestEntity
 * @author a
 * @version 2018-05-24
 */
public class OpenSendUpdateGroupOrder extends DataEntity<OpenSendUpdateGroupOrder> {

    private static final long serialVersionUID = 1L;

    private String group_id;
    private String master_id;
    private List<OpenSendUpdateGroupComboOrder> order_list;


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

    public List<OpenSendUpdateGroupComboOrder> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OpenSendUpdateGroupComboOrder> order_list) {
        this.order_list = order_list;
    }

}
