package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * 服务项目保存RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveItemRequest extends DataEntity<OpenSendSaveItemRequest> {

    private static final long serialVersionUID = 1L;

    private List<OpenSendSaveItemProduct> product;
    private List<String> carousel;
    private List<String> info;

    public List<OpenSendSaveItemProduct> getProduct() {
        return product;
    }

    public void setProduct(List<OpenSendSaveItemProduct> product) {
        this.product = product;
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