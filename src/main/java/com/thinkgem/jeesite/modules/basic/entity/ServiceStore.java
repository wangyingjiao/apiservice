package com.thinkgem.jeesite.modules.basic.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务站-门店  关联表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("basic_service_store")
public class ServiceStore extends BaseEntity<ServiceStore> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务站id
     */
    @TableId("station_id")
	private String stationId;
    /**
     * 门店id
     */
	@TableField("store_id")
	private String storeId;


	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	@Override
	protected Serializable pkVal() {
		return this.stationId;
	}

	@Override
	public String toString() {
		return "ServiceStore{" +
			", stationId=" + stationId +
			", storeId=" + storeId +
			"}";
	}
}
