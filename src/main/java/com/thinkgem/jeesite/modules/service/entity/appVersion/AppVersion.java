package com.thinkgem.jeesite.modules.service.entity.appVersion;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * APP发版Entity
 * @author a
 * @version 2018-01-29
 */
public class AppVersion extends DataEntity<AppVersion> {

    private static final long serialVersionUID = 1L;
    private String versionNumber;  //版本号
    private BigDecimal build;       //build号
    private String forcedUpdate;   //强更状态：不强更no; 强更yes
    private String upgradeContent; //更新提示语
    private String refreshAddress; //更新地址
    private String startTime;
    private String endTime;

    private VersionInfo forcedAppVersion;

    public VersionInfo getForcedAppVersion() {
        return forcedAppVersion;
    }

    public void setForcedAppVersion(VersionInfo forcedAppVersion) {
        this.forcedAppVersion = forcedAppVersion;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    @NotBlank(message = "版本号不可为空")
    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
    @NotBlank(message = "build号不可为空")
    public BigDecimal getBuild() {
        return build;
    }

    public void setBuild(BigDecimal build) {
        this.build = build;
    }
    @NotBlank(message = "强更状态不可为空")
    public String getForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(String forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }
    @NotBlank(message = "更新提示语不可为空")
    public String getUpgradeContent() {
        return upgradeContent;
    }

    public void setUpgradeContent(String upgradeContent) {
        this.upgradeContent = upgradeContent;
    }
    @NotBlank(message = "更新地址不可为空")
    public String getRefreshAddress() {
        return refreshAddress;
    }

    public void setRefreshAddress(String refreshAddress) {
        this.refreshAddress = refreshAddress;
    }
}
