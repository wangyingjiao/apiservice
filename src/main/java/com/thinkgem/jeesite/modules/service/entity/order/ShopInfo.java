/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 门店Entity
 * @author a
 * @version 2017-12-26
 */
public class ShopInfo extends DataEntity<ShopInfo> {
	private static final long serialVersionUID = 1L;

	private String shopName;
	private String shopPhone;
	private String shopAddress;
	private String shopRemark;
	private List<String> shopRemarkPic;

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopPhone() {
		return shopPhone;
	}

	public void setShopPhone(String shopPhone) {
		this.shopPhone = shopPhone;
	}

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public String getShopRemark() {
		return shopRemark;
	}

	public void setShopRemark(String shopRemark) {
		this.shopRemark = shopRemark;
	}

	public List<String> getShopRemarkPic() {
		return shopRemarkPic;
	}

	public void setShopRemarkPic(List<String> shopRemarkPic) {
		this.shopRemarkPic = shopRemarkPic;
	}
}