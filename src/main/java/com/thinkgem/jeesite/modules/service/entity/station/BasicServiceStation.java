/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.station;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 服务站Entity
 *
 * @author 服务站
 * @version 2017-12-11
 */
public class BasicServiceStation extends DataEntity<BasicServiceStation> {

    private static final long serialVersionUID = 1L;
    private String name;        // 服务站名称
    private String type;        // 服务站类型（self:直营，join:加盟）
    private String provinceCode;        // 省_区号
    private String cityCode;        // 市_区号
    private String cityName;
    private String areaCode;        // 区_区号
    private String address;        // 详细地址
    private User user;        // 站长id
    private String userId;// 站长id
    private String phone;        // 联系电话或联系手机号
    private int employees;        // 员工数量
    private int techNum;        // 技师数量
    private String servicePoint;        // 服务站座标点
    private String orgId;        // 机构ID
    private String isUseable;        // 是否可用(yes:可用，no:不可用)
    private BasicOrganization organ;
    private List<String> storeList;
    private String storeId;

    private String orgName;   //机构名称

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<String> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<String> storeList) {
        this.storeList = storeList;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public BasicOrganization getOrgan() {
        return organ;
    }

    public void setOrgan(BasicOrganization organ) {
        this.organ = organ;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public BasicServiceStation() {
        super();
    }

    public BasicServiceStation(String id) {
        super(id);
    }


    @Length(min = 0, max = 32, message = "服务站名称长度必须介于 0 和 32 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 20, message = "省_区号长度必须介于 0 和 20 之间")
    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Length(min = 0, max = 20, message = "市_区号长度必须介于 0 和 20 之间")
    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Length(min = 0, max = 20, message = "区_区号长度必须介于 0 和 20 之间")
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Length(min = 0, max = 100, message = "详细地址长度必须介于 0 和 100 之间")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Length(min = 0, max = 16, message = "联系电话或联系手机号长度必须介于 0 和 16 之间")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(min = 0, max = 11, message = "员工数量长度必须介于 0 和 11 之间")
    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

    @Length(min = 0, max = 11, message = "技师数量长度必须介于 0 和 11 之间")
    public int getTechNum() {
        return techNum;
    }

    public void setTechNum(int techNum) {
        this.techNum = techNum;
    }

    public String getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(String servicePoint) {
        this.servicePoint = servicePoint;
    }

    @Length(min = 0, max = 32, message = "机构ID长度必须介于 0 和 32 之间")
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getIsUseable() {
        return isUseable;
    }

    public void setIsUseable(String isUseable) {
        this.isUseable = isUseable;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}