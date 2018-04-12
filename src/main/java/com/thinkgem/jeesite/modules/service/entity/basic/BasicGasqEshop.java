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
    private String businessModelId;//业务模式ID
    private String sellerId;  //E店店主id
    private String publish;   //yse 审核通过 no 未审核 fail  未通过
    private int    status;    //软删除状态标志位
    private String code;          //编号
    private String operationBaseStatus;//运营基本信息审核状态：no未审核；submit审核中；fail审核未通过；yes审核通过'
    private String thirdPart;    //XMGJ 小马管家，selfService 自营服务， 空为请选择

    private String eshopCode;     //编号
    private String orgId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessModelId() {
        return businessModelId;
    }

    public void setBusinessModelId(String businessModelId) {
        this.businessModelId = businessModelId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperationBaseStatus() {
        return operationBaseStatus;
    }

    public void setOperationBaseStatus(String operationBaseStatus) {
        this.operationBaseStatus = operationBaseStatus;
    }

    public String getThirdPart() {
        return thirdPart;
    }

    public void setThirdPart(String thirdPart) {
        this.thirdPart = thirdPart;
    }

    public String getEshopCode() {
        return eshopCode;
    }

    public void setEshopCode(String eshopCode) {
        this.eshopCode = eshopCode;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
