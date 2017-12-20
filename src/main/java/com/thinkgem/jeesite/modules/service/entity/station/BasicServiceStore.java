/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.station;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 门店与服务站Entity
 * @author a
 * @version 2017-12-20
 */
public class BasicServiceStore extends DataEntity<BasicServiceStore> {
	
	private static final long serialVersionUID = 1L;
	private String stationId;		// 服务站id
	private String storeId;		// 门店id
	
	public BasicServiceStore() {
		super();
	}

	public BasicServiceStore(String id){
		super(id);
	}

	@Length(min=1, max=32, message="服务站id长度必须介于 1 和 32 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	@Length(min=1, max=32, message="门店id长度必须介于 1 和 32 之间")
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
}