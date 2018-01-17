package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目保存RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveOrderServiceTechInfo extends DataEntity<OpenSendSaveOrderServiceTechInfo> {

    private static final long serialVersionUID = 1L;

    private String tech_name;// 技师姓名
    private String tech_phone;// 技师手机号

    public String getTech_name() {
        return tech_name;
    }

    public void setTech_name(String tech_name) {
        this.tech_name = tech_name;
    }

    public String getTech_phone() {
        return tech_phone;
    }

    public void setTech_phone(String tech_phone) {
        this.tech_phone = tech_phone;
    }
}