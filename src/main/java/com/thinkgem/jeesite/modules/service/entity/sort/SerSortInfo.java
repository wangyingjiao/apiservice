/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String orgId;//机构ID
    private Boolean flag=false;  //是否已经选过
    private List<String> sortIds;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public SerSortInfo() {
        super();
    }

    public SerSortInfo(String id) {
        super(id);
    }

    @NotBlank(message = "服务分类：保洁、家修不可为空")
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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public List<String> getSortIds() {
        return sortIds;
    }

    public void setSortIds(List<String> sortIds) {
        this.sortIds = sortIds;
    }
}