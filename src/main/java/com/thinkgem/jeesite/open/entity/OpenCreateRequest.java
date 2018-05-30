/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 选择服务时间RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenCreateRequest extends DataEntity<OpenCreateRequest> {

	private static final long serialVersionUID = 1L;

	private String store_id;//门店ID
	private String eshop_code;//E店编码
	private String platform;// 对接平台代号 默认值 gasq
	private String phone;//用户电话
	private List<String>  remark_pic;//订单备注(用户备注)
	private String remark;//订单备注(用户备注)
	private String area_code;//区CODE
	private String province_code;//省CODE
	private String city_code;//市CODE
	private OpenServiceInfo serviceInfo;//购买商品ID及数量
	private List<OpenServiceInfo> service_info;//购买商品ID及数量
	private String service_time;//服务时间
	private String expiry_date;//有效期
	private String address;//服务地址：小区+详细地址
	private String placename;//服务地址：小区
	private String detail_address;//服务地址：门牌号
	private String latitude;//服务地址：纬度
	private String longitude;//服务地址：经度
	private String sum_price;//订单总支付价格
	private String order_type;//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单

	private List<String> gasq_order_sn;//国安社区订单SN
	private String name; // 服务信息：用户姓名
	private String store_name;//门店名称
	private String store_phone;//门店电话
	private String store_addr;//门店地址
	private String province_name;//省的名称
	private String city_name;//市的名称
	private String area_name;//区的名称
	private String login_mobile;//登录帐号手机号
	private String login_name;//登录帐号用户名
	private String group_id;//订单组ID

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getEshop_code() {
		return eshop_code;
	}

	public void setEshop_code(String eshop_code) {
		this.eshop_code = eshop_code;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getProvince_code() {
		return province_code;
	}

	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public OpenServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(OpenServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public List<OpenServiceInfo> getService_info() {
		return service_info;
	}

	public void setService_info(List<OpenServiceInfo> service_info) {
		this.service_info = service_info;
	}

	public String getService_time() {
		return service_time;
	}

	public void setService_time(String service_time) {
		this.service_time = service_time;
	}

	public String getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(String expiry_date) {
		this.expiry_date = expiry_date;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSum_price() {
		return sum_price;
	}

	public void setSum_price(String sum_price) {
		this.sum_price = sum_price;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public List<String> getRemark_pic() {
		return remark_pic;
	}

	public void setRemark_pic(List<String> remark_pic) {
		this.remark_pic = remark_pic;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public String getDetail_address() {
		return detail_address;
	}

	public void setDetail_address(String detail_address) {
		this.detail_address = detail_address;
	}

	public List<String> getGasq_order_sn() {
		return gasq_order_sn;
	}

	public void setGasq_order_sn(List<String> gasq_order_sn) {
		this.gasq_order_sn = gasq_order_sn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_phone() {
		return store_phone;
	}

	public void setStore_phone(String store_phone) {
		this.store_phone = store_phone;
	}

	public String getStore_addr() {
		return store_addr;
	}

	public void setStore_addr(String store_addr) {
		this.store_addr = store_addr;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public String getLogin_mobile() {
		return login_mobile;
	}

	public void setLogin_mobile(String login_mobile) {
		this.login_mobile = login_mobile;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}