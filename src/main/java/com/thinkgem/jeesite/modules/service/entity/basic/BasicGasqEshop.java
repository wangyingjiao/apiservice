package com.thinkgem.jeesite.modules.service.entity.basic;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * E店Entity
 * @author a
 * @version 2018-03-12
 */
public class BasicGasqEshop extends DataEntity<BasicGasqEshop> {

    private static final long serialVersionUID = 1L;
    private String name;   //E店名
    private String sellerId;  //E店店主id
    private String publish;   //yse 审核通过 no 未审核 fail  未通过
    private int    status;    //软删除状态标志位
    private String freezeStatus; //yes 冻结 no 不冻结
    private String businessStatus;//open' COMMENT '营业状态
    private int    version;      //操作版本
    private String createUserId; //创建人ID
    private String updateUserId;  //更新人ID
    private String code;          //编号
    private String thirdPart;    //XMGJ 小马管家，selfService 自营服务， 空为请选择
    private String isSupportOpenPlatform;  //no' COMMENT '是否开通对接开放平台
    private String openPlatformKey;   //对接开放平台的秘钥

    private String businessModelId;//业务模式ID
    private String operationBaseStatus;//运营基本信息审核状态：no未审核；submit审核中；fail审核未通过；yes审核通过'

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFreezeStatus() {
        return freezeStatus;
    }

    public void setFreezeStatus(String freezeStatus) {
        this.freezeStatus = freezeStatus;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getThirdPart() {
        return thirdPart;
    }

    public void setThirdPart(String thirdPart) {
        this.thirdPart = thirdPart;
    }

    public String getIsSupportOpenPlatform() {
        return isSupportOpenPlatform;
    }

    public void setIsSupportOpenPlatform(String isSupportOpenPlatform) {
        this.isSupportOpenPlatform = isSupportOpenPlatform;
    }

    public String getOpenPlatformKey() {
        return openPlatformKey;
    }

    public void setOpenPlatformKey(String openPlatformKey) {
        this.openPlatformKey = openPlatformKey;
    }

    public String getBusinessModelId() {
        return businessModelId;
    }

    public void setBusinessModelId(String businessModelId) {
        this.businessModelId = businessModelId;
    }

    public String getOperationBaseStatus() {
        return operationBaseStatus;
    }

    public void setOperationBaseStatus(String operationBaseStatus) {
        this.operationBaseStatus = operationBaseStatus;
    }
}
