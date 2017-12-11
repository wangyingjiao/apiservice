package com.thinkgem.jeesite.modules.tech.entity;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务技师基础信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("tech_info")
public class TechInfo extends BaseEntity<TechInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 技师所属机构id
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 技师所属服务站
     */
	@TableField("station_id")
	private String stationId;
    /**
     * 技师姓名
     */
	private String name;
    /**
     * 头像
     */
	@TableField("head_pic")
	private String headPic;
    /**
     * 身份证号
     */
	@TableField("id_card")
	private String idCard;
    /**
     * 身份证照片(正反)
     */
	@TableField("id_card_pic")
	private String idCardPic;
    /**
     * 生活照
     */
	@TableField("life_pic")
	private String lifePic;
    /**
     * 证件照
     */
	@TableField("job_pic")
	private String jobPic;
    /**
     * 手机号
     */
	private String phone;
    /**
     * 省_区号
     */
	@TableField("province_code")
	private String provinceCode;
    /**
     * 市_区号
     */
	@TableField("city_code")
	private String cityCode;
    /**
     * 区_区号
     */
	@TableField("area_code")
	private String areaCode;
    /**
     * 详细地址
     */
	private String address;
    /**
     * 性别（male：男 female：女）
     */
	private String sex;
    /**
     * 民族
     */
	private String nation;
    /**
     * 出生日期
     */
	@TableField("birth_date")
	private Date birthDate;
    /**
     * 状态(yes:上线，no:暂停)
     */
	private String status;
    /**
     * app登录密码
     */
	@TableField("app_login_password")
	private String appLoginPassword;
    /**
     * 岗位性质（full_time:全职，part_time:兼职）
     */
	@TableField("job_natrue")
	private Date jobNatrue;
    /**
     * 岗位状态(online:在职，leave:离职)
     */
	@TableField("job_status")
	private String jobStatus;
    /**
     * 工作年限
     */
	@TableField("work_time")
	private Integer workTime;
    /**
     * 邮箱
     */
	private String email;
    /**
     * 学历(primary：小学 middle：初中 high：高中 special：中专 junior：大专 university：本科及以上)
     */
	private String education;
    /**
     * 身高
     */
	private Integer height;
    /**
     * 体重
     */
	private Integer weight;
    /**
     * 婚姻状况(married：已婚 unmarried：未婚 widowed：丧偶 divorce：离婚)
     */
	@TableField("marry_status")
	private String marryStatus;
    /**
     * 籍贯
     */
	@TableField("native_province_code")
	private String nativeProvinceCode;
    /**
     * 入职日期
     */
	@TableField("in_job_time")
	private Date inJobTime;
    /**
     * 级别
     */
	@TableField("job_level")
	private Integer jobLevel;
    /**
     * 经验描述
     */
	private String description;
    /**
     * 备注信息
     */
	private String remark;


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAppLoginPassword() {
		return appLoginPassword;
	}

	public void setAppLoginPassword(String appLoginPassword) {
		this.appLoginPassword = appLoginPassword;
	}

	public Date getJobNatrue() {
		return jobNatrue;
	}

	public void setJobNatrue(Date jobNatrue) {
		this.jobNatrue = jobNatrue;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Integer getWorkTime() {
		return workTime;
	}

	public void setWorkTime(Integer workTime) {
		this.workTime = workTime;
	}

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

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getMarryStatus() {
		return marryStatus;
	}

	public void setMarryStatus(String marryStatus) {
		this.marryStatus = marryStatus;
	}

	public String getNativeProvinceCode() {
		return nativeProvinceCode;
	}

	public void setNativeProvinceCode(String nativeProvinceCode) {
		this.nativeProvinceCode = nativeProvinceCode;
	}

	public Date getInJobTime() {
		return inJobTime;
	}

	public void setInJobTime(Date inJobTime) {
		this.inJobTime = inJobTime;
	}

	public Integer getJobLevel() {
		return jobLevel;
	}

	public void setJobLevel(Integer jobLevel) {
		this.jobLevel = jobLevel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "TechInfo{" +
			", orgId=" + orgId +
			", stationId=" + stationId +
			", name=" + name +
			", headPic=" + headPic +
			", idCard=" + idCard +
			", idCardPic=" + idCardPic +
			", lifePic=" + lifePic +
			", jobPic=" + jobPic +
			", phone=" + phone +
			", provinceCode=" + provinceCode +
			", cityCode=" + cityCode +
			", areaCode=" + areaCode +
			", address=" + address +
			", sex=" + sex +
			", nation=" + nation +
			", birthDate=" + birthDate +
			", status=" + status +
			", appLoginPassword=" + appLoginPassword +
			", jobNatrue=" + jobNatrue +
			", jobStatus=" + jobStatus +
			", workTime=" + workTime +
			", email=" + email +
			", education=" + education +
			", height=" + height +
			", weight=" + weight +
			", marryStatus=" + marryStatus +
			", nativeProvinceCode=" + nativeProvinceCode +
			", inJobTime=" + inJobTime +
			", jobLevel=" + jobLevel +
			", description=" + description +
			", remark=" + remark +
			"}";
	}
}
