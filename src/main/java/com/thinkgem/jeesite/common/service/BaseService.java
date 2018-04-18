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

    /** 员工列表使用 （暂时测通）
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
        String type = u.getType();
        if (null != organization
                && StringUtils.isNotBlank(organization.getId())
                && organization.getId().trim().equals("sys")
                && null != station
                && StringUtils.isNotBlank(station.getId())
                && station.getId().trim().equals("sys")
                && "sys".equals(type)) {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 全系统权限 ");
            // sql.append("AND " + organAlias + ".id = " + "'" + organization.getId() + "'");
        } else if (null != organization &&
                StringUtils.isNotBlank(organization.getId()) &&
                organization.getId().trim().equals("0")) {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 全平台 ");
            sql.append(" AND a.type !='sys'");
            // sql.append(" AND " + stationAlias + ".id != 'sys' ");
        } else if (null != organization
                && StringUtils.isNotBlank(organization.getId())
                && !organization.getId().trim().equals("0")
                && null != station
                && StringUtils.isNotBlank(station.getId())
                && station.getId().trim().equals("0")) {
            log.info("当前用户：" + user.getId() + ":" + user.getName() + "==> 数据权限 全机构权限 ");
            sql.append(" AND " + organAlias + ".id = " + "'" + organization.getId() + "'");
        } else if (null != organization
                && null != station
                && StringUtils.isNotBlank(organization.getId())
                && StringUtils.isNotBlank(station.getId())
                && (!organization.getId().trim().equals("0") && !organization.getId().trim().equals("sys"))
                && (!station.getId().trim().equals("0") && !station.getId().trim().equals("sys"))) {
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

    // 服务机构的下拉列表（搜索栏下拉列表）
    public static String dataOrganFilter(User user, String alias) {
        BasicOrganization organization = user.getOrganization();
        if ("sys".equals(user.getType())) {
            log.info("机构权限过滤：当前用户为全系统用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if ("platform".equals(user.getType())) {
            log.info("机构权限过滤：当前用户为全平台用户 " + user.getId() + ":" + user.getName());
            return " AND a.id != 'sys'";
        } else{
            return " AND " + alias + ".id = '" + organization.getId() + "'";
        }
    }

    //员工新增时调用 角色机构下拉列表 根据type 获取对应机构 下拉列表
    public static String dataOrganFilterAddUser(BasicOrganization basicOrganization, String alias) {
        User user = UserUtils.getUser();
        BasicOrganization organization = user.getOrganization();
        String type = basicOrganization.getType();
        //如果选择机构员工 先去看用户权限
        if ("org".equals(type)){
            //如果是系统或者平台用户则机构展示所有机构列表
            if ("sys".equals(user.getType()) || "platform".equals(user.getType())){
                return " AND a.id != 'sys' AND a.id !='0'";
            }else if ("org".equals(user.getType())){
                return " AND " + alias + ".id = '" + organization.getId() + "'";
            }
        }else if ("station".equals(type)){ //如果是服务站员工
            //如果是系统或者平台用户则机构展示所有机构列表
            if ("sys".equals(user.getType()) || "platform".equals(user.getType())){
                return " AND a.id != 'sys' AND a.id !='0'";
            }else if ("org".equals(user.getType())){
                return " AND " + alias + ".id = '" + organization.getId() + "'";
            }else if ("station".equals(user.getType())){
                return " AND " + alias + ".id = '" + organization.getId() + "'";
            }
        }else {
            throw new ServiceException("数据不对");
        }
        return "";
    }

    //员工新增时调用 角色机构服务站下拉列表 根据type 机构id 获取对应机构 下拉列表
    public static String dataStationFilterAddUser(BasicServiceStation serviceStation, String alias) {
        User user = UserUtils.getUser();
        String type = serviceStation.getType();
        //如果选择机构员工 先去看用户权限
        if ("station".equals(type)){ //如果是服务站员工
            //如果是系统或者平台用户则机构展示所有机构列表
            if ("sys".equals(user.getType()) || "platform".equals(user.getType())){
                return " AND " + alias + ".org_id = '" + serviceStation.getOrgId() + "'";
            }else if ("org".equals(user.getType())){
                return " AND " + alias + ".org_id = '" + serviceStation.getOrgId() + "'";
            }else if ("station".equals(user.getType())){
                return " AND " + alias + ".id = '" + user.getStation().getId() + "'";
            }
        }else {
            throw new ServiceException("数据不对");
        }
        return "";
    }

    //服务站列表使用（暂时测通）
    public static String dataStationFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        String type = user.getType();
        if (org !=null &&  org.getId().trim().equals("sys") && sts != null && sts.getId().trim().equals("sys") && "sys".equals(type) ){
            log.info("机构权限过滤：当前用户为 |全系统| 用户 " + user.getId() + ":" + user.getName());
            //return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND a.id = '" + sts.getId() + "'";
            return  "";
        } else if (null != org && org.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if (null != sts && sts.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全机构| 用户 " + user.getId() + ":" + user.getName());

            return " AND " + alias + ".org_id = '" + org.getId() + "'";
        } else {
            log.info("机构权限过滤：当前用户为 |本服务站| 用户 " + user.getId() + ":" + user.getName());
            //return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND a.id = '" + sts.getId() + "'";
            return  "AND a.id = '" + sts.getId() + "'";
        }
    }

    public static String dataStatioRoleFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        String type = user.getType();
        if (org !=null &&  org.getId().trim().equals("sys") && sts != null && sts.getId().trim().equals("sys") && "sys".equals(type) ){
            log.info("机构权限过滤：当前用户为 |全系统| 用户 " + user.getId() + ":" + user.getName());
            //return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND a.id = '" + sts.getId() + "'";
            return  "";
        } else if (null != org && org.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if (null != sts && sts.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全机构| 用户 " + user.getId() + ":" + user.getName());

            return " AND " + alias + ".org_id = '" + org.getId() + "'";
        } else {
            log.info("机构权限过滤：当前用户为 |本服务站| 用户 " + user.getId() + ":" + user.getName());
            return " AND " + alias + ".org_id = '" + org.getId() + "'" + " AND " + alias + ".station_id = '" + sts.getId() + "'";
        }
    }
    //无机构id权限
    public static String dataStationIdRoleFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        String type = user.getType();
        if (org !=null &&  org.getId().trim().equals("sys") && sts != null && sts.getId().trim().equals("sys") && "sys".equals(type) ){
            log.info("机构权限过滤：当前用户为 |全系统| 用户 " + user.getId() + ":" + user.getName());
            //return " AND " + alias + ".org_id = '" + org.getId() + "'" + "AND a.id = '" + sts.getId() + "'";
            return  "";
        } else if (null != org && org.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return "";
        } else if (null != sts && sts.getId().trim().equals("0")) {
            log.info("机构权限过滤：当前用户为 |全机构| 用户 " + user.getId() + ":" + user.getName());

            return "";
        } else {
            log.info("机构权限过滤：当前用户为 |本服务站| 用户 " + user.getId() + ":" + user.getName());
            return "" + " AND " + alias + ".station_id = '" + sts.getId() + "'";
        }
    }
    public static String dataOrderRoleFilter(User user, String alias) {
        BasicOrganization org = user.getOrganization();
        BasicServiceStation sts = user.getStation();
        if (null != org && org.getId().trim().equals("0")) {
            log.info("订单权限过滤：当前用户为 |全平台| 用户 " + user.getId() + ":" + user.getName());
            return " AND " + alias + ".order_source = 'gasq'";
        } else{
            log.info("订单权限过滤：当前用户为 |本机构/本服务站| 用户 " + user.getId() + ":" + user.getName());

            return " AND " + alias + ".org_id = '" + org.getId() + "'";
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


    /**
     * 列表数据权限，订单、工单用
     *
     * 1、全平台角色： 只展示订单来源为国安社区的订单列表
     * 2、本机构/本服务站角色 不区分来源
     *    本机构：看到的是机构下所有的订单
     *    服务站：看到的是本服务站的所有订单
     * 3、全系统角色 可查看全部订单
     *
     * @param user
     * @param alias
     * @return
     */
    public static String dataRoleFilterForOrder(User user, String alias){
        String userType = user.getType();
        if("sys".equals(userType)){// 全系统角色 可查看全部订单
            return "";
        }else if("platform".equals(userType)){// 全平台角色： 只展示订单来源为国安社区的订单列表
            return " AND " + alias + ".order_source = 'gasq'";
        }else if("org".equals(userType)){// 本机构：看到的是机构下所有的订单
            return " AND " + alias + ".org_id = '" + user.getOrganization().getId() + "'";
        }else if("station".equals(userType)){// 服务站：看到的是本服务站的所有订单
            return " AND " + alias + ".org_id = '" + user.getOrganization().getId() + "'" +
                    " AND " + alias + ".station_id = '" + user.getStation().getId() + "'";
        }
        return "";
    }
}
