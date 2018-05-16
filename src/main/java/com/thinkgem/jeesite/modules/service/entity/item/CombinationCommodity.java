package com.thinkgem.jeesite.modules.service.entity.item;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 服务项目组合商品-子商品信息Entity
 * @author a
 * @version 2018-05-15
 */
public class CombinationCommodity extends DataEntity<CombinationCommodity> {

    private static final long serialVersionUID = 1L;
    private String combinationGoodsId;   //组合商品ID
    private String itemId;                //项目ID
    private String itemName;             //项目名称
    private String goodsId;               //商品ID
    private String goodsName;            //商品名称
    private BigDecimal goodsPrice;        //商品价格
    private String goodsUnit;             //商品单位
    private int goodsMinPurchase;        //商品起购数量
    private BigDecimal combinationPrice;  //组合商品价格
    private int combinationNum;           //组合商品数量(多次服务)

    private String orgId;     //机构ID
    private String sortId;     //分类ID
    private boolean check = false;  //是否被选中
    private BigDecimal convertHours;  //折算时长

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public BigDecimal getConvertHours() {
        return convertHours;
    }

    public void setConvertHours(BigDecimal convertHours) {
        this.convertHours = convertHours;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getCombinationGoodsId() {
        return combinationGoodsId;
    }

    public void setCombinationGoodsId(String combinationGoodsId) {
        this.combinationGoodsId = combinationGoodsId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    public int getGoodsMinPurchase() {
        return goodsMinPurchase;
    }

    public void setGoodsMinPurchase(int goodsMinPurchase) {
        this.goodsMinPurchase = goodsMinPurchase;
    }

    public BigDecimal getCombinationPrice() {
        return combinationPrice;
    }

    public void setCombinationPrice(BigDecimal combinationPrice) {
        this.combinationPrice = combinationPrice;
    }

    public int getCombinationNum() {
        return combinationNum;
    }

    public void setCombinationNum(int combinationNum) {
        this.combinationNum = combinationNum;
    }
}
