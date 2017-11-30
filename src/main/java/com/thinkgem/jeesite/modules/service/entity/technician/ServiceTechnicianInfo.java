/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 服务技师基础信息Entity
 *
 * @author a
 * @version 2017-11-16
 */
public class ServiceTechnicianInfo extends DataEntity<ServiceTechnicianInfo> {

    private static final long serialVersionUID = 1L;
    private String id;
    private String techOfficeId;        // 技师所属机构id
    private String techOfficeName;        // 技师所属机构名称
    private String techStationId;        // 技师所属服务站
    private String techStationName;        // 技师所属服务站名称
    private String techName;        // 技师姓名
    private String techIdCard;        // 身份证号
    private String techPhone;        // 手机号
    private String status;           //是否可用
    private String appLoginPassword;        // app登录密码
    private String bankType;        // 银行卡类型
    private String bankCardNo;        // 银行卡号
    private String addrProvinceId;        // 现住地址_省_id
    private String addrCityId;        // 现住地址_市_id
    private String addrDistrictId;        // 现地地址_区_id
    private String addrProvinceName;        // 现住地址_省_名称
    private String addrCityName;        // 现住地址_市_名称
    private String addrDistrictName;        // 现住地址_区_名称
    private String addrDetailInfo;        // 现住地址_详细信息
    private String techSex;        // 性别
    private String techNation;        // 民族
    private Date techBirthDate;        // 出生日期
    private String techEmail;        // 邮箱
    private String techEducation;        // 学历
    private String techHeight;        // 身高
    private String techWeight;        // 体重
    private String techMatrimony;        // 婚姻状况
    private String techNativePlace;        // 籍贯
    private String sort;        // 排序
    private String imgUrl; //头像
    private String jobNatrue;//岗位性质
    private String age; //年龄
    private List<String> skillIds;//技能List

    //技师服务信息
    private ServiceTechnicianServiceInfo serviceInfo;
    //技师图片信息
    private List<ServiceTechnicianImages> images;

    //技师工作时间
    private List<ServiceTechnicianWorkTime> workTime;

    //技师家庭成员信息
    private List<ServiceTechnicianFamilyMembers> familyMembers;

    @Override
    @NotBlank(message = "技师id不可为空",groups = SaveMoreGroup.class)
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public ServiceTechnicianInfo() {
        super();
    }

    public ServiceTechnicianInfo(String id) {
        super(id);
    }

    @Length(min = 0, max = 64, message = "技师所属机构id长度必须介于 0 和 64 之间")
    public String getTechOfficeId() {
        return techOfficeId;
    }

    public void setTechOfficeId(String techOfficeId) {
        this.techOfficeId = techOfficeId;
    }

    @Length(min = 0, max = 64, message = "技师所属机构名称长度必须介于 0 和 64 之间")
    public String getTechOfficeName() {
        return techOfficeName;
    }

    public void setTechOfficeName(String techOfficeName) {
        this.techOfficeName = techOfficeName;
    }

    @Length(min = 0, max = 64, message = "技师所属服务站长度必须介于 0 和 64 之间")
    public String getTechStationId() {
        return techStationId;
    }

    public void setTechStationId(String techStationId) {
        this.techStationId = techStationId;
    }

    @Length(min = 0, max = 64, message = "技师所属服务站名称长度必须介于 0 和 64 之间")
    public String getTechStationName() {
        return techStationName;
    }

    public void setTechStationName(String techStationName) {
        this.techStationName = techStationName;
    }

    @NotBlank(message = "技师名称不可为空",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 255, message = "技师姓名长度必须介于 0 和 255 之间")
    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    @NotBlank(message = "技师身份证号不可为空。",groups = SavePersonalGroup.class)
    @Length(min = 15, max = 18, message = "身份证号长度必须介于 15 和 18 之间")
    public String getTechIdCard() {
        return techIdCard;
    }

    public void setTechIdCard(String techIdCard) {
        this.techIdCard = techIdCard;
    }

    @NotBlank(message = "技师手机号不可为空！",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 32, message = "手机号长度必须介于 0 和 32 之间")
    public String getTechPhone() {
        return techPhone;
    }

    public void setTechPhone(String techPhone) {
        this.techPhone = techPhone;
    }

    @Length(min = 0, max = 255, message = "app登录密码长度必须介于 0 和 255 之间")
    public String getAppLoginPassword() {
        return appLoginPassword;
    }

    public void setAppLoginPassword(String appLoginPassword) {
        this.appLoginPassword = appLoginPassword;
    }

    @Length(min = 0, max = 2, message = "银行卡类型长度必须介于 0 和 2 之间")
    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    @Length(min = 0, max = 64, message = "银行卡号长度必须介于 0 和 64 之间")
    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    @NotBlank(message = "技师省id为空",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 64, message = "现住地址_省_id长度必须介于 0 和 64 之间")
    public String getAddrProvinceId() {
        return addrProvinceId;
    }

    public void setAddrProvinceId(String addrProvinceId) {
        this.addrProvinceId = addrProvinceId;
    }

    @NotBlank(message = "技师市id为空",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 64, message = "现住地址_市_id长度必须介于 0 和 64 之间")
    public String getAddrCityId() {
        return addrCityId;
    }

    public void setAddrCityId(String addrCityId) {
        this.addrCityId = addrCityId;
    }

    @NotBlank(message = "技师区id为空",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 64, message = "现地地址_区_id长度必须介于 0 和 64 之间")
    public String getAddrDistrictId() {
        return addrDistrictId;
    }

    public void setAddrDistrictId(String addrDistrictId) {
        this.addrDistrictId = addrDistrictId;
    }

    @Length(min = 0, max = 255, message = "现住地址_省_名称长度必须介于 0 和 255 之间")
    public String getAddrProvinceName() {
        return addrProvinceName;
    }

    public void setAddrProvinceName(String addrProvinceName) {
        this.addrProvinceName = addrProvinceName;
    }

    @Length(min = 0, max = 255, message = "现住地址_市_名称长度必须介于 0 和 255 之间")
    public String getAddrCityName() {
        return addrCityName;
    }

    public void setAddrCityName(String addrCityName) {
        this.addrCityName = addrCityName;
    }

    @Length(min = 0, max = 255, message = "现住地址_区_名称长度必须介于 0 和 255 之间")
    public String getAddrDistrictName() {
        return addrDistrictName;
    }

    public void setAddrDistrictName(String addrDistrictName) {
        this.addrDistrictName = addrDistrictName;
    }

    @Length(min = 0, max = 255, message = "现住地址_详细信息长度必须介于 0 和 255 之间")
    public String getAddrDetailInfo() {
        return addrDetailInfo;
    }

    public void setAddrDetailInfo(String addrDetailInfo) {
        this.addrDetailInfo = addrDetailInfo;
    }

    @NotBlank(message = "性别不可为空",groups = SavePersonalGroup.class)
    @Length(min = 0, max = 2, message = "性别长度必须介于 0 和 2 之间")
    public String getTechSex() {
        return techSex;
    }

    public void setTechSex(String techSex) {
        this.techSex = techSex;
    }

    @Length(min = 0, max = 2, message = "民族长度必须介于 0 和 2 之间")
    public String getTechNation() {
        return techNation;
    }

    public void setTechNation(String techNation) {
        this.techNation = techNation;
    }

    //@NotEmpty(message = "技师出生日期不可为空",groups = SavePersonalGroup.class)
    //@NotEmpty(groups = SavePersonalGroup.class,message = "技师出生日期不可为空")
    //@Pattern(regexp="[0-9]{4}-[0-9]{2}-[0-9]{2}",message = "技师出生日期不可为空",groups = SavePersonalGroup.class)
    @NotNull(message = "技师出生日期不可为空",groups = SavePersonalGroup.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getTechBirthDate() {
        return techBirthDate;
    }

    public void setTechBirthDate(Date techBirthDate) {
        this.techBirthDate = techBirthDate;
    }

    @Length(min = 0, max = 63, message = "邮箱长度必须介于 0 和 63 之间")
    public String getTechEmail() {
        return techEmail;
    }

    public void setTechEmail(String techEmail) {
        this.techEmail = techEmail;
    }

    @Length(min = 0, max = 2, message = "学历长度必须介于 0 和 2 之间")
    public String getTechEducation() {
        return techEducation;
    }

    public void setTechEducation(String techEducation) {
        this.techEducation = techEducation;
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

    @Length(min = 0, max = 2, message = "婚姻状况长度必须介于 0 和 2 之间")
    public String getTechMatrimony() {
        return techMatrimony;
    }

    public void setTechMatrimony(String techMatrimony) {
        this.techMatrimony = techMatrimony;
    }

    @Length(min = 0, max = 2, message = "籍贯长度必须介于 0 和 2 之间")
    public String getTechNativePlace() {
        return techNativePlace;
    }

    public void setTechNativePlace(String techNativePlace) {
        this.techNativePlace = techNativePlace;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public ServiceTechnicianServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceTechnicianServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public List<ServiceTechnicianImages> getImages() {
        return images;
    }

    public void setImages(List<ServiceTechnicianImages> images) {
        this.images = images;
    }

    public List<ServiceTechnicianWorkTime> getWorkTime() {
        return workTime;
    }

    public void setWorkTime(List<ServiceTechnicianWorkTime> workTime) {
        this.workTime = workTime;
    }

    public List<ServiceTechnicianFamilyMembers> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<ServiceTechnicianFamilyMembers> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getJobNatrue() {
        return jobNatrue;
    }

    public void setJobNatrue(String jobNatrue) {
        this.jobNatrue = jobNatrue;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<String> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<String> skillIds) {
        this.skillIds = skillIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}