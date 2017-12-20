/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service基类
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class BaseService {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static Logger log = LoggerFactory.getLogger(BaseService.class);

    /**
     * @param user       用户信息
     * @param organAlias 关联表别名
     * @param stationAlias  用户别名
     * @return
     */
    public static String dataScopeFilter(User user, String organAlias, String stationAlias) {

        StringBuilder sql = new StringBuilder();

        User u = UserUtils.get(user.getId());
        BasicOrganization organization = u.getOrganization();
        BasicServiceStation station = u.getStation();
        if (null != organization &&
                StringUtils.isNotBlank(organization.getId()) &&
                organization.getId().trim().equals("0")) {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 全平台 ");
        } else if (null != organization
                && StringUtils.isNotBlank(organization.getId())
                && !organization.getId().trim().equals("0")
                && null != station
                && StringUtils.isNotBlank(station.getId())
                && station.getId().trim().equals("0")) {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 全机构权限 ");
            sql.append("AND " + organAlias + ".id = " + "'" + organization.getId() + "'");
        } else if (null != organization
                && null != station
                && StringUtils.isNotBlank(organization.getId())
                && StringUtils.isNotBlank(station.getId())
                && !organization.getId().trim().equals("0")
                && !station.getId().trim().equals("0")) {
            sql.append(" AND " + organAlias + ".id = '" + organization.getId() + "' ");
            sql.append(" AND " + stationAlias + ".id = '" + station.getId() + "' ");
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 本服务站权限 ");
        } else {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 权限不明 ");
            sql.append(" AND a.id is null");
            log.info("附加sql：" + sql.toString() + " 不返回任何数据");
        }
        log.info("SQL:" + sql.toString());
        return sql.toString();
    }


    public static String dataOrganFilter(User user, String alias) {
        BasicOrganization organization = user.getOrganization();
        if (null != organization && organization.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为全机构用户 " + user.getId() + ":" + user.getName());
            return "";
        } else {
            return " AND " + alias + ".id = '" + organization.getId() + "'";
        }
    }

    public static String dataStationFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        if (null != org && org.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if (null != sts && sts.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全机构| 用户 " + user.getId() + ":" + user.getName());

            return " AND " + alias + ".org_id = '" + org.getId() + "'";
        } else {
            log.info("机构权限过滤：当前用户为 |本服务站| 用户 " + user.getId() + ":" + user.getName());
            return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND a.id = '" + sts.getId() + "'";
        }
    }


    public static String dataStatioRoleFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        if (null != org && org.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if (null != sts && sts.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全机构| 用户 " + user.getId() + ":" + user.getName());

            return " AND " + alias + ".org_id = '" + org.getId() + "'";
        } else {
            log.info("机构权限过滤：当前用户为 |本服务站| 用户 " + user.getId() + ":" + user.getName());
            return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND " + alias + ".station_id = '" + sts.getId() + "'";
        }
    }


    /**
     * @param user
     * @param tableAlias
     * @return
     */
    public static String dataRoleFilter(User user, String tableAlias) {
        StringBuilder sqlString = new StringBuilder();
        String officeId = user.getOrganization().getId();//机构ID
        String stationId = user.getStation().getId();//服务站ID

        String dataRole = "";
        if ("0".equals(officeId)) {
            dataRole = Role.DATA_ROLE_ALL;//机构ID为0 代表全平台
        } else {
            dataRole = Role.DATA_ROLE_OFFICE;//服务站ID为0 代表全机构
        }
        // 超级管理员，跳过权限过滤
        if (!user.isAdmin()) {
            boolean isDataScopeAll = false;
            if (Role.DATA_ROLE_ALL.equals(dataRole)) {
                sqlString = new StringBuilder();
            } else if (Role.DATA_ROLE_OFFICE.equals(dataRole)) {
                sqlString.append(" AND " + tableAlias + ".org_id = '" + officeId + "'");
            }
        }
        if (StringUtils.isNotBlank(sqlString.toString())) {
            return sqlString.toString();
        }
        return "";
    }

    /**
     * @param user
     * @return
     */
    public static String dataRoleFilter(User user) {
        StringBuilder sqlString = new StringBuilder();
        String officeId = user.getOrganization().getId();//机构ID
        String stationId = user.getStation().getId();//服务站ID

        String dataRole = "";
        if ("0".equals(officeId)) {
            dataRole = Role.DATA_ROLE_ALL;//机构ID为0 代表全平台
        } else {
            // if ("0".equals(stationId)) {
            dataRole = Role.DATA_ROLE_OFFICE;//服务站ID为0 代表全机构
//            } else {
//                dataRole = Role.DATA_ROLE_STATION;//本服务站
//            }
        }
        // 超级管理员，跳过权限过滤
        if (!user.isAdmin()) {
            boolean isDataScopeAll = false;
            if (Role.DATA_ROLE_ALL.equals(dataRole)) {
                sqlString = new StringBuilder();
            } else if (Role.DATA_ROLE_OFFICE.equals(dataRole)) {
                sqlString.append(" AND " + "a" + ".office_id = '" + officeId + "'");
            }
//            else if (Role.DATA_ROLE_STATION.equals(dataRole)) {
//                sqlString.append(" AND " + tableAlias + ".station_id = '" + stationId + "'");
//            }

        }
        if (StringUtils.isNotBlank(sqlString.toString())) {
            return sqlString.toString();
        }
        return "";
    }

}
