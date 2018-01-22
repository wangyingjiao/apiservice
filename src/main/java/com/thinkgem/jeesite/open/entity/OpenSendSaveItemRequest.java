package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.List;

/**
 * 服务项目保存RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveItemRequest extends DataEntity<OpenSendSaveItemRequest> {

    private static final long serialVersionUID = 1L;

    private HashMap<String,Object> product;
    private String eshop_code;// ESHOP_CODE
    private List<String> carousel;
    private List<String> info;

    public HashMap<String, Object> getProduct() {
        return product;
    }

    public void setProduct(HashMap<String, Object> product) {
        this.product = product;
    }

    public String getEshop_code() {
        return eshop_code;
    }

    public void setEshop_code(String eshop_code) {
        this.eshop_code = eshop_code;
    }

    public List<String> getCarousel() {
        return carousel;
    }

    public void setCarousel(List<String> carousel) {
        this.carousel = carousel;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }
}