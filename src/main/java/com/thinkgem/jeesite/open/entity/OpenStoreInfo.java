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
public class OpenStoreInfo extends DataEntity<OpenStoreInfo> {
	private static final long serialVersionUID = 1L;

	private String name;//门店名
	private String telephone;//门店电话
	private String remark;//门店备注
	private String addr;//门店地址
	private List<String> remark_pic;//门店备注图片
	private String remarkPic;//门店备注图片

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

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

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getRemarkPic() {
		return remarkPic;
	}

	public void setRemarkPic(String remarkPic) {
		this.remarkPic = remarkPic;
	}
}