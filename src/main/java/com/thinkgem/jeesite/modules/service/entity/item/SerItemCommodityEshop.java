package com.thinkgem.jeesite.modules.service.entity.item;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目商品E店Entity
 * @author a
 * @version 2018-03-14
 */
public class SerItemCommodityEshop extends DataEntity<SerItemCommodityEshop> {

    private static final long serialVersionUID = 1L;
    private String orgId;             //机构id
    private String eshopCode;         //对接方E店CODE
    private String status;            //对接E店状态(yes:是，no:否)
    private String itemId;              //服务项目ID
    private String goodsId;             //服务商品ID
    private String jointGoodsCode;      //对接方商品CODE
    private String jointStatus;         //对接状态（"butt_butt": "对接中", "butt_success": "对接成功", butt_fail:对接失败, "remove_fail": "解除失败"）

    private String selfCode;  //对接编码
    private String eshopName;  //E店名称
    private String itemName;    //服务项目名称
    private String name;        //拼接后的商品名称
    private String sortId;    //所属服务分类id
    private String sortName;    //所属服务分类名称
    private SerItemCommodity serItemCommodity; //服务项目商品信息

    public String getSelfCode() {
        return selfCode;
    }

    public void setSelfCode(String selfCode) {
        this.selfCode = selfCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public SerItemCommodity getSerItemCommodity() {
        return serItemCommodity;
    }

    public void setSerItemCommodity(SerItemCommodity serItemCommodity) {
        this.serItemCommodity = serItemCommodity;
    }

    public String getEshopName() {
        return eshopName;
    }

    public void setEshopName(String eshopName) {
        this.eshopName = eshopName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getEshopCode() {
        return eshopCode;
    }

    public void setEshopCode(String eshopCode) {
        this.eshopCode = eshopCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getJointGoodsCode() {
        return jointGoodsCode;
    }

    public void setJointGoodsCode(String jointGoodsCode) {
        this.jointGoodsCode = jointGoodsCode;
    }

    public String getJointStatus() {
        return jointStatus;
    }

    public void setJointStatus(String jointStatus) {
        this.jointStatus = jointStatus;
    }
}
