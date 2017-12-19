/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 服务技师APP返回信息Entity
 *
 * @author a
 * @version 2017-11-16
 */
public class AppServiceTechnicianInfo extends DataEntity<AppServiceTechnicianInfo> {

    private static final long serialVersionUID = 1L;
    private String token;
    private String stationName;//服务站名称
    private String imgUrlHead;//头像
    private String techPhone;//登陆账号
    private String techName;//姓名
    private String techSex;//性别
    private Date techBirthDate;//出生日期
    private String addrDetailInfo;//现住地址
    private String techIdCard;//身份证号
    private String imgUrlCard;//身份证正面照
    private String techEmail;//邮箱
    private String techHeight;//身高
    private String techWeight;//体重
    private String techNativePlace;//籍贯
    private String techNation;//民族
    private String experDesc;//经验描述
    private String imgUrlLife;//生活照

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getImgUrlHead() {
        return imgUrlHead;
    }

    public void setImgUrlHead(String imgUrlHead) {
        this.imgUrlHead = imgUrlHead;
    }

    public String getTechPhone() {
        return techPhone;
    }

    public void setTechPhone(String techPhone) {
        this.techPhone = techPhone;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getTechSex() {
        return techSex;
    }

    public void setTechSex(String techSex) {
        this.techSex = techSex;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getTechBirthDate() {
        return techBirthDate;
    }

    public void setTechBirthDate(Date techBirthDate) {
        this.techBirthDate = techBirthDate;
    }

    public String getAddrDetailInfo() {
        return addrDetailInfo;
    }

    public void setAddrDetailInfo(String addrDetailInfo) {
        this.addrDetailInfo = addrDetailInfo;
    }

    public String getTechIdCard() {
        return techIdCard;
    }

    public void setTechIdCard(String techIdCard) {
        this.techIdCard = techIdCard;
    }

    public String getImgUrlCard() {
        return imgUrlCard;
    }

    public void setImgUrlCard(String imgUrlCard) {
        this.imgUrlCard = imgUrlCard;
    }

    public String getTechEmail() {
        return techEmail;
    }

    public void setTechEmail(String techEmail) {
        this.techEmail = techEmail;
    }

    public String getTechHeight() {
        return techHeight;
    }

    public void setTechHeight(String techHeight) {
        this.techHeight = techHeight;
    }

    public String getTechWeight() {
        return techWeight;
    }

    public void setTechWeight(String techWeight) {
        this.techWeight = techWeight;
    }

    public String getTechNativePlace() {
        return techNativePlace;
    }

    public void setTechNativePlace(String techNativePlace) {
        this.techNativePlace = techNativePlace;
    }

    public String getTechNation() {
        return techNation;
    }

    public void setTechNation(String techNation) {
        this.techNation = techNation;
    }

    public String getExperDesc() {
        return experDesc;
    }

    public void setExperDesc(String experDesc) {
        this.experDesc = experDesc;
    }

    public String getImgUrlLife() {
        return imgUrlLife;
    }

    public void setImgUrlLife(String imgUrlLife) {
        this.imgUrlLife = imgUrlLife;
    }
}