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

    
}