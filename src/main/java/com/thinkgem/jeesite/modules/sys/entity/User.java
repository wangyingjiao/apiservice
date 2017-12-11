package com.thinkgem.jeesite.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;
import com.thinkgem.jeesite.modules.basic.entity.Organization;
import com.thinkgem.jeesite.modules.basic.entity.ServiceStation;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_user")
public class User extends BaseEntity<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @TableField("org_id")
    private String orgId;
    /**
     * 组织机构
     */
    @TableField(exist = false)
    private Organization org;
    /**
     * 服务站id
     */
    @TableField("station_id")
    private String stationId;
    /**
     * 服务站属性
     */
    @TableField(exist = false)
    private ServiceStation station;
    /**
     * 角色（岗位）id
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 岗位信息
     */
    @TableField(exist = false)
    private Role role;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号（登录名）
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
    /**
     * 可用状态（yes：可用 no:不可用）
     */
    @TableField("is_useable")
    private String isUseable;
    /**
     * 最后登陆IP
     */
    @TableField("login_ip")
    private String loginIp;
    /**
     * 最后登陆时间
     */
    @TableField("login_date")
    private Date loginDate;


    public boolean isAdmin() {
        return this.id.equals("1");
    }


    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsUseable() {
        return isUseable;
    }

    public void setIsUseable(String isUseable) {
        this.isUseable = isUseable;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization org) {
        this.org = org;
    }

    public ServiceStation getStation() {
        return station;
    }

    public void setStation(ServiceStation station) {
        this.station = station;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                ", orgId=" + orgId +
                ", stationId=" + stationId +
                ", roleId=" + roleId +
                ", name=" + name +
                ", phone=" + phone +
                ", password=" + password +
                ", isUseable=" + isUseable +
                ", loginIp=" + loginIp +
                ", loginDate=" + loginDate +
                "}";
    }

}
