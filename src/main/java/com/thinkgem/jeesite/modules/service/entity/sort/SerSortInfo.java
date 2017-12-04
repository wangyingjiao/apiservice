/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 服务分类Entity
 *
 * @author a
 * @version 2017-11-15
 */
public class SerSortInfo extends DataEntity<SerSortInfo> {

    private static final long serialVersionUID = 1L;
    private String majorSort;        // 分类：保洁、家修
    private String name;        // 服务分类名称
    private String allCity;   //是否是全部城市
    private String stationId;//服务站ID
    private String officeId;//机构ID
    private String stationName;//服务站名称
    private String officeName;//机构名称

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }


    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public SerSortInfo() {
        super();
    }

    public SerSortInfo(String id) {
        super(id);
    }

    @NotBlank(message = "服务分类：保洁、家修不可为空")
    @Length(min = 0, max = 1, message = "分类：保洁、家修长度必须介于 0 和 1 之间")
    public String getMajorSort() {
        return majorSort;
    }

    public void setMajorSort(String majorSort) {
        this.majorSort = majorSort;
    }

    @NotBlank(message = "服务分类名称不可为空")
    @Length(min = 2, max = 10, message = "服务分类名称长度必须介于 2 和 10 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllCity() {
        return allCity;
    }

    public void setAllCity(String allCity) {
        this.allCity = allCity;
    }

    private String cityId;        // 城市编号 查询用
    private List<String> cityNames; // 城市 列表显示用
    private List<SerSortCity> citys; // 城市 保存用

    public List<String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(List<String> cityNames) {
        this.cityNames = cityNames;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public List<SerSortCity> getCitys() {
        return citys;
    }

    public void setCitys(List<SerSortCity> citys) {
        this.citys = citys;
    }
}