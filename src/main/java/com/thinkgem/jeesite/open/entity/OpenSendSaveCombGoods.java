package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 子商品Entity
 * @author a
 * @version 2018-05-22
 */
public class OpenSendSaveCombGoods extends DataEntity<OpenSendSaveCombGoods> {

    private static final long serialVersionUID = 1L;

    private String product_id;   //商品对接code
    private String product_num;   //组合商品的子商品数量
    private String settlement_price;   //组合商品的子商品价格

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_num() {
        return product_num;
    }

    public void setProduct_num(String product_num) {
        this.product_num = product_num;
    }

    public String getSettlement_price() {
        return settlement_price;
    }

    public void setSettlement_price(String settlement_price) {
        this.settlement_price = settlement_price;
    }
}
