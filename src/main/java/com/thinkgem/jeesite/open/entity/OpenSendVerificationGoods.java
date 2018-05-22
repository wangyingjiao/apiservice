package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 子商品验证Entity
 * @author a
 * @version 2018-05-22
 */
public class OpenSendVerificationGoods extends DataEntity<OpenSendVerificationGoods> {

    private static final long serialVersionUID = 1L;

    private String service_pro_name;    //父商品名称
    private List<String> self_codes;    //子商品对接code集合
}
