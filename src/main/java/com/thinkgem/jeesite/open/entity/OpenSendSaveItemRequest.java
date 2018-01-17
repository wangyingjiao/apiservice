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

    private String type;//service 类型：商品 product / 服务 service
    private String is_combo;//no 单一商品 no / 组合商品 yes
    private String tags_system;//系统标签格式：系统标签1,系统标签2,系统标签3,
    private String tags_custom;// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
    private String name;// 商品名称格式：项目名称（商品名）
    private String price;// 商品价格
    private String unit;// 商品单位格式：次/个/间
    private Integer stock_quantity;// 999999 每日库存不限，写默认值
    private Integer min_number;// 最小购买数量，起购数量
    private Integer max_number;// 最大购买数量，默认999999
    private String banner_pic;// 最少一张，最多四张 格式：["openservice/2017/12/25/微信图片_20171113093258.jpg","openservice/2017/12/25/头像.jpg"]
    private String describe_pic;// 可以为空；格式：["openservice/2017/12/25/微信图片_20171113093258.jpg","openservice/2017/12/25/头像.jpg"]
    private String joint_code;// 对接CODE；自定义编码
    private String eshop_code;// ESHOP_CODE

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_combo() {
        return is_combo;
    }

    public void setIs_combo(String is_combo) {
        this.is_combo = is_combo;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public Integer getMin_number() {
        return min_number;
    }

    public void setMin_number(Integer min_number) {
        this.min_number = min_number;
    }

    public Integer getMax_number() {
        return max_number;
    }

    public void setMax_number(Integer max_number) {
        this.max_number = max_number;
    }

    public String getBanner_pic() {
        return banner_pic;
    }

    public void setBanner_pic(String banner_pic) {
        this.banner_pic = banner_pic;
    }

    public String getDescribe_pic() {
        return describe_pic;
    }

    public void setDescribe_pic(String describe_pic) {
        this.describe_pic = describe_pic;
    }

    public String getJoint_code() {
        return joint_code;
    }

    public void setJoint_code(String joint_code) {
        this.joint_code = joint_code;
    }

    public String getEshop_code() {
        return eshop_code;
    }

    public void setEshop_code(String eshop_code) {
        this.eshop_code = eshop_code;
    }
}