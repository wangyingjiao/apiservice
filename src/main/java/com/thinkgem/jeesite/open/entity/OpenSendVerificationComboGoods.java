package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;
import java.util.Map;

public class OpenSendVerificationComboGoods extends DataEntity<OpenSendVerificationComboGoods> {

    private static final long serialVersionUID = 1L;

    private List<OpenSendVerificationGoods> goodsList;
    private Map<String,Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<OpenSendVerificationGoods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<OpenSendVerificationGoods> goodsList) {
        this.goodsList = goodsList;
    }
}
