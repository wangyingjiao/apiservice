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


}