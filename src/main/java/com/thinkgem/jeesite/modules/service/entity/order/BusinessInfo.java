/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 业务人员Entity
 * @author a
 * @version 2017-12-26
 */
public class BusinessInfo extends DataEntity<BusinessInfo> {
	private static final long serialVersionUID = 1L;

	private String businessName;
	private String businessPhone;
	private String businessRemark;
	private List<String> businessRemarkPic;

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	public String getBusinessRemark() {
		return businessRemark;
	}

	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark;
	}

	public List<String> getBusinessRemarkPic() {
		return businessRemarkPic;
	}

	public void setBusinessRemarkPic(List<String> businessRemarkPic) {
		this.businessRemarkPic = businessRemarkPic;
	}
}