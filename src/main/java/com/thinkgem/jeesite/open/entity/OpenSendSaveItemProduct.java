package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 服务项目保存RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveItemProduct extends DataEntity<OpenSendSaveItemProduct> {

    private static final long serialVersionUID = 1L;

    private String content_name;// 商品名称格式：项目名称（商品名）
    private String tags_system;//系统标签格式：系统标签1,系统标签2,系统标签3,
    private String tags_custom;// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
    private String content_price;// 商品价格
    private String content_unit;// 商品单位格式：次/个/间
    private String self_code;   //自营平台商品code  ID
    private String min_number;// 最小购买数量，起购数量
    private String content_shelf;//上架 下架 on off
    private String content_img;//图片 一张

    private String warn_number;// 0
    private String max_number;// 最大购买数量，默认999999
    private String is_combo_split;//no
    private String content_type;//service 类型：商品 product / 服务 service
    private String is_combo;//no 单一商品 no / 组合商品 yes
    private String content_number;//999999

    public String getContent_name() {
        return content_name;
    }

    public void setContent_name(String content_name) {
        this.content_name = content_name;
    }

    public String getTags_system() {
        return tags_system;
    }

    public void setTags_system(String tags_system) {
        this.tags_system = tags_system;
    }

    public String getTags_custom() {
        return tags_custom;
    }

    public void setTags_custom(String tags_custom) {
        this.tags_custom = tags_custom;
    }

    public String getContent_price() {
        return content_price;
    }

    public void setContent_price(String content_price) {
        this.content_price = content_price;
    }

    public String getContent_unit() {
        return content_unit;
    }

    public void setContent_unit(String content_unit) {
        this.content_unit = content_unit;
    }

    public String getSelf_code() {
        return self_code;
    }

    public void setSelf_code(String self_code) {
        this.self_code = self_code;
    }

    public String getMin_number() {
        return min_number;
    }

    public void setMin_number(String min_number) {
        this.min_number = min_number;
    }

    public String getContent_shelf() {
        return content_shelf;
    }

    public void setContent_shelf(String content_shelf) {
        this.content_shelf = content_shelf;
    }

    public String getContent_img() {
        return content_img;
    }

    public void setContent_img(String content_img) {
        this.content_img = content_img;
    }

    public String getWarn_number() {
        return warn_number;
    }

    public void setWarn_number(String warn_number) {
        this.warn_number = warn_number;
    }

    public String getMax_number() {
        return max_number;
    }

    public void setMax_number(String max_number) {
        this.max_number = max_number;
    }

    public String getIs_combo_split() {
        return is_combo_split;
    }

    public void setIs_combo_split(String is_combo_split) {
        this.is_combo_split = is_combo_split;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getIs_combo() {
        return is_combo;
    }

    public void setIs_combo(String is_combo) {
        this.is_combo = is_combo;
    }

    public String getContent_number() {
        return content_number;
    }

    public void setContent_number(String content_number) {
        this.content_number = content_number;
    }
}