package com.thinkgem.jeesite.modules.service.entity.basic;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 机构-E店关联Entity
 * @author a
 * @version 2018-03-12
 */
public class BasicOrganizationEshop extends DataEntity<BasicOrganizationEshop> {
    private static final long serialVersionUID = 1L;
    private String orgId;       //机构ID
    private String dockType;    //平台
    private String eshopCode;   //E店code
    private String name;       //E店名称

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

    public String getDockType() {
        return dockType;
    }

    public void setDockType(String dockType) {
        this.dockType = dockType;
    }

    public String getEshopCode() {
        return eshopCode;
    }

    public void setEshopCode(String eshopCode) {
        this.eshopCode = eshopCode;
    }
}
