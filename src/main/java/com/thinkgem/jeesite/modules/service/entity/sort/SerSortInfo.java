/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

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

    @Length(min = 0, max = 1, message = "分类：保洁、家修长度必须介于 0 和 1 之间")
    public String getMajorSort() {
        return majorSort;
    }

    public void setMajorSort(String majorSort) {
        this.majorSort = majorSort;
    }

    @Length(min = 0, max = 64, message = "服务分类名称长度必须介于 0 和 64 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


/*    private String city;//城市*/
    private String cityId;        // 城市编号
    private String cityName;        // 城市名称
    private List<String> cityNames;

    public List<String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(List<String> cityNames) {
        this.cityNames = cityNames;
    }
/*    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }*/

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    private List<SerSortCity> citys;
    /*private List<OfficeSeviceAreaList> officeCitys;

    public List<OfficeSeviceAreaList> getOfficeCitys() {
        return officeCitys;
    }

    public void setOfficeCitys(List<OfficeSeviceAreaList> officeCitys) {
        this.officeCitys = officeCitys;
    }*/

    public List<SerSortCity> getCitys() {
        return citys;
    }

    public void setCitys(List<SerSortCity> citys) {
        this.citys = citys;
    }
}