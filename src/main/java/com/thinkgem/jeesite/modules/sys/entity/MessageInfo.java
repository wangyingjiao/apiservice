/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

/**
 * 消息entity
 *
 * @author a
 * @version 2017-11-16
 */
public class MessageInfo extends DataEntity<MessageInfo> {

    private static final long serialVersionUID = 1L;
    private String title;		// 标题
    private String message;     //内容
    private String receivePhone;		// 收信人手机号
    private String type;		// 类型
    private String isRead;		// 是否已读
    private String targetType;  //订单类型 order
    private String targetId;    //订单id
    private Date pushTime;      //推送时间
    private String extParameters;    //额外带参
    private String isSuccess;
    private String techId;

    private String createTime;    //app展示的推送时间
    private String orderId; //订单id
    private String orderType;

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getExtParameters() {
        return extParameters;
    }

    public void setExtParameters(String extParameters) {
        this.extParameters = extParameters;
    }

    private String accessKeyId= Global.getConfig("message_access_key_id");//"raAz3o82A1eT3TSy";
    private String accessKeySecret= Global.getConfig("message_access_key_secret");//"mT4z5Umnel0i7voopzqXzhshV5Nnie";
    private Long appKey= Long.parseLong(Global.getConfig("message_app_key"));//(long)24779049;
    private String deviceIds;  //要发送的账号

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public Long getAppKey() {
        return appKey;
    }

    public void setAppKey(Long appKey) {
        this.appKey = appKey;
    }

    public String getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(String deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}