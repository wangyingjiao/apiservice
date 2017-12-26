/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.RegexTool;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    private String orgId;		// 技师所属机构id
    private String orgName;
    private String stationId;		// 技师所属服务站
    private String stationName;		// 技师所属服务站
    private String stationCityCode;		// 技师所属服务站
    private String name;		// 技师姓名
    private String headPic;		// 头像
    private String idCard;		// 身份证号
    private String idCardPic;		// 身份证照片(正反)
    private String lifePic;		// 生活照
    private String jobPic;		// 证件照
    private String phone;		// 手机号
    private String provinceCode;		// 省_区号
    private String cityCode;		// 市_区号
    private String areaCode;		// 区_区号
    private String address;		// 详细地址
    private String sex;		// 性别（male：男 female：女）
    private String nation;		// 民族
    private Date birthDate;		// 出生日期
    private int age;            // 年龄
    private String status;		// 状态(yes:上线，no:暂停)
    private String appLoginPassword;		// app登录密码
    private String jobNature;		// 岗位性质（full_time:全职，part_time:兼职）
    private String jobStatus;		// 岗位状态(online:在职，leave:离职)
    private int workTime;		// 工作年限
    private String email;		// 邮箱
    private String education;		// 学历(primary：小学 middle：初中 high：高中 special：中专 junior：大专 university：本科及以上)
    private int height;		// 身高
    private int weight;		// 体重
    private String marryStatus;		// 婚姻状况(married：已婚 unmarried：未婚 widowed：丧偶 divorce：离婚)
    private String nativeProvinceCode;		// 籍贯
    private Date inJobTime;		// 入职日期
    private int jobLevel;		// 级别
    private String description;		// 经验描述
    private String remark;		// 备注信息


    public ServiceTechnicianInfo() {
        super();
    }

    public ServiceTechnicianInfo(String id) {
        super(id);
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCityCode() {
        return stationCityCode;
    }

    public void setStationCityCode(String stationCityCode) {
        this.stationCityCode = stationCityCode;
    }

    @NotBlank(message = "姓名不可为空")
    @Length(min=2, max=15, message="技师姓名长度必须介于 2 和 15 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank(message = "头像不可为空")
    @Length(min=0, max=255, message="头像长度必须介于 0 和 255 之间")
    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    @NotBlank(message = "身份证号不可为空")
    @Pattern(regexp = RegexTool.REGEX_ID_CARD,message = "身份证号格式不正确！")
    @Length(min=0, max=32, message="身份证号长度必须介于 0 和 32 之间")
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Length(min=0, max=1000, message="身份证照片(正反)长度必须介于 0 和 1000 之间")
    public String getIdCardPic() {
        return idCardPic;
    }

    public void setIdCardPic(String idCardPic) {
        this.idCardPic = idCardPic;
    }

    public String getLifePic() {
        return lifePic;
    }

    public void setLifePic(String lifePic) {
        this.lifePic = lifePic;
    }

    public String getJobPic() {
        return jobPic;
    }

    public void setJobPic(String jobPic) {
        this.jobPic = jobPic;
    }

    @NotBlank(message = "手机号不可为空")
    @Pattern(regexp = RegexTool.REGEX_MOBILE,message = "手机号格式不正确！")
    @Length(min=11, max=11, message="手机号长度必须是 11 位")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @NotBlank(message = "省不可为空")
    @Length(min=0, max=20, message="省_区号长度必须介于 0 和 20 之间")
    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @NotBlank(message = "市不可为空")
    @Length(min=0, max=20, message="市_区号长度必须介于 0 和 20 之间")
    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @NotBlank(message = "区不可为空")
    @Length(min=0, max=20, message="区_区号长度必须介于 0 和 20 之间")
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Length(min=0, max=100, message="详细地址长度必须介于 0 和 100 之间")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NotBlank(message = "性别不可为空")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Length(min=0, max=2, message="民族长度必须介于 0 和 2 之间")
    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Length(min=0, max=255, message="app登录密码长度必须介于 0 和 255 之间")
    public String getAppLoginPassword() {
        return appLoginPassword;
    }

    public void setAppLoginPassword(String appLoginPassword) {
        this.appLoginPassword = appLoginPassword;
    }

    public String getJobNature() {
        return jobNature;
    }

    public void setJobNature(String jobNature) {
        this.jobNature = jobNature;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    //@Pattern(regexp = RegexTool.REGEX_EMAIL,message = "邮箱格式不正确！")
    @Length(min=0, max=64, message="邮箱长度必须介于 0 和 64 之间")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getMarryStatus() {
        return marryStatus;
    }

    public void setMarryStatus(String marryStatus) {
        this.marryStatus = marryStatus;
    }

    @Length(min=0, max=20, message="籍贯长度必须介于 0 和 20 之间")
    public String getNativeProvinceCode() {
        return nativeProvinceCode;
    }

    public void setNativeProvinceCode(String nativeProvinceCode) {
        this.nativeProvinceCode = nativeProvinceCode;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getInJobTime() {
        return inJobTime;
    }

    public void setInJobTime(Date inJobTime) {
        this.inJobTime = inJobTime;
    }

    public int getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(int jobLevel) {
        this.jobLevel = jobLevel;
    }

    @Length(min=0, max=255, message="经验描述长度必须介于 0 和 255 之间")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Length(min=0, max=255, message="备注信息长度必须介于 0 和 255 之间")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    //技师工作时间
    private List<ServiceTechnicianWorkTime> workTimes;
    //技师家庭成员信息
    private List<ServiceTechnicianFamilyMembers> familyMembers;
    private List<String> skillIds;//技能List
    private List<BasicServiceStation> stations;

    public List<ServiceTechnicianWorkTime> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(List<ServiceTechnicianWorkTime> workTimes) {
        this.workTimes = workTimes;
    }

    public List<ServiceTechnicianFamilyMembers> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<ServiceTechnicianFamilyMembers> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public List<String> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<String> skillIds) {
        this.skillIds = skillIds;
    }

    public List<BasicServiceStation> getStations() {
        return stations;
    }

    public void setStations(List<BasicServiceStation> stations) {
        this.stations = stations;
    }
}