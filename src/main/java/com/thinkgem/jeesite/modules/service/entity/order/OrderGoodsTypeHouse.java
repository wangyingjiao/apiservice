package com.thinkgem.jeesite.modules.service.entity.order;

public class OrderGoodsTypeHouse {
    private String id;
    private String name;
    private String payPrice;

    private String goodsUnit;		// 商品单位
    private int minPurchase;		// 起购数量
    private int goodsNum;		// 订购商品数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public String getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    public int getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(int minPurchase) {
        this.minPurchase = minPurchase;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }
}
