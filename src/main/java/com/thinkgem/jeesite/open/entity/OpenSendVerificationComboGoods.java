package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

public class OpenSendVerificationComboGoods extends DataEntity<OpenSendVerificationComboGoods> {

    private static final long serialVersionUID = 1L;

    private List<OpenSendVerificationGoods> goodsList;

    public List<OpenSendVerificationGoods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<OpenSendVerificationGoods> goodsList) {
        this.goodsList = goodsList;
    }
}
