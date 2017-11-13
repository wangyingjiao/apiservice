/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.TreeEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 机构Entity
 *
 * @author ThinkGem
 * @version 2013-05-15
 */
public class Office extends TreeEntity<Office> {


    private Office parent;		// 父级编号

    private String masterName;		// 负责人
    private String masterPhone;		// 负责人电话


    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

    private String type;    // 机构类型（1：公司；2：部门；3：小组）
    private String grade;    // 机构等级（1：一级；2：二级；3：三级；4：四级）

    @ApiModelProperty(hidden = true)
    private List<String> childDeptList;//快速添加子部门

    private static final long serialVersionUID = 1L;


    private Area area;        // 归属区域
    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    private String code;        // 区域编码

    private String address;        // 联系地址
    private String serviceAreaType;        // 服务范围类型

    public List<String> getCityIds() {
        return cityIds;
    }

    public void setCityIds(List<String> cityIds) {
        this.cityIds = cityIds;
    }

    private List<String> cityIds;

    public String getServiceCityId() {
        if (null != cityIds) {
            return cityIds.toString();
        }
        return serviceCityId;
    }

    public void setServiceCityId(String serviceCityId) {
        this.serviceCityId = serviceCityId;
    }

    private String serviceCityId;        // 服务城市
    private String officeUrl;        // 机构网址
    private String office400;        // 机构400电话
    private String zipCode;        // 邮政编码
    private String master;        // 负责人
    private String phone;        // 电话
    private String fax;        // 传真
    private String email;        // 邮箱
    private String useable;        // 是否启用
    @ApiModelProperty(hidden = true)
    private User primaryPerson;        // 主负责人

    private String primaryPersonName;
    private String primaryPersonPhone;

    public String getPrimaryPersonName() {
        return primaryPersonName;
    }

    public void setPrimaryPersonName(String primaryPersonName) {
        this.primaryPersonName = primaryPersonName;
    }

    public String getPrimaryPersonPhone() {
        return primaryPersonPhone;
    }

    public void setPrimaryPersonPhone(String primaryPersonPhone) {
        this.primaryPersonPhone = primaryPersonPhone;
    }


    @ApiModelProperty(hidden = true)
    private User deputyPerson;        // 副负责人


    public String getServiceAreaType() {
        return serviceAreaType;
    }

    public void setServiceAreaType(String serviceAreaType) {
        this.serviceAreaType = serviceAreaType;
    }


    public String getOfficeUrl() {
        return officeUrl;
    }

    public void setOfficeUrl(String officeUrl) {
        this.officeUrl = officeUrl;
    }

    public String getOffice400() {
        return office400;
    }

    public void setOffice400(String office400) {
        this.office400 = office400;
    }

    public Office() {
        super();
//		this.sort = 30;
        this.type = "2";
    }

    public Office(String id) {
        super(id);
    }

    public List<String> getChildDeptList() {
        return childDeptList;
    }

    public void setChildDeptList(List<String> childDeptList) {
        this.childDeptList = childDeptList;
    }

    public String getUseable() {
        return useable;
    }

    public void setUseable(String useable) {
        this.useable = useable;
    }

    public User getPrimaryPerson() {
        return primaryPerson;
    }

    public void setPrimaryPerson(User primaryPerson) {
        this.primaryPerson = primaryPerson;
    }

    public User getDeputyPerson() {
        return deputyPerson;
    }

    public void setDeputyPerson(User deputyPerson) {
        this.deputyPerson = deputyPerson;
    }

    //	@JsonBackReference
//	@NotNull
    @Override
    public Office getParent() {
        return parent;
    }

    @Override
    public void setParent(Office parent) {
        this.parent = parent;
    }
//
//	@Length(min=1, max=2000)
//	public String getParentIds() {
//		return parentIds;
//	}
//
//	public void setParentIds(String parentIds) {
//		this.parentIds = parentIds;
//	}

    @NotNull
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
//
//	@Length(min=1, max=100)
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Integer getSort() {
//		return sort;
//	}
//
//	public void setSort(Integer sort) {
//		this.sort = sort;
//	}

    @Length(min = 1, max = 1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 1, max = 1)
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Length(min = 0, max = 255)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Length(min = 0, max = 100)
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Length(min = 0, max = 100)
    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Length(min = 0, max = 200)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(min = 0, max = 200)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Length(min = 0, max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(min = 0, max = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//	public String getParentId() {
//		return parent != null && parent.getId() != null ? parent.getId() : "0";
//	}

    @Override
    public String toString() {
        return name;
    }
}