/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 消息entity
 *
 * @author a
 * @version 2017-11-16
 */
public class VersionInfo extends DataEntity<VersionInfo> {

    private static final long serialVersionUID = 1L;
    private String versionNumber;   //versionName
    private BigDecimal build;            //versionCode
    private String upgradeContent;  //更新内容
    private String refreshAddress;  //更新地址
    private String forcedUpdate;    //是否强制更新
    private String receiveBuild;    //收到的参数 版本build

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getUpgradeContent() {
        return upgradeContent;
    }

    public void setUpgradeContent(String upgradeContent) {
        this.upgradeContent = upgradeContent;
    }

    public String getRefreshAddress() {
        return refreshAddress;
    }

    public void setRefreshAddress(String refreshAddress) {
        this.refreshAddress = refreshAddress;
    }

    public String getForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(String forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public BigDecimal getBuild() {
        return build;
    }

    public void setBuild(BigDecimal build) {
        this.build = build;
    }

    public String getReceiveBuild() {
        return receiveBuild;
    }

    public void setReceiveBuild(String receiveBuild) {
        this.receiveBuild = receiveBuild;
    }
}