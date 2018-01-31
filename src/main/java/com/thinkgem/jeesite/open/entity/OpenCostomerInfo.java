/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 下单服务项目信息Entity
 * @author a
 * @version 2017-12-11
 */
public class OpenCostomerInfo extends DataEntity<OpenCostomerInfo> {
	private static final long serialVersionUID = 1L;

    private String remark;//用户备注
    private List<String> remark_pic;//用户备注图片
    private String remarkPic;//用户备注图片

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getRemark_pic() {
        return remark_pic;
    }

    public void setRemark_pic(List<String> remark_pic) {
        this.remark_pic = remark_pic;
    }

    public String getRemarkPic() {
        return remarkPic;
    }

    public void setRemarkPic(String remarkPic) {
        this.remarkPic = remarkPic;
    }
}