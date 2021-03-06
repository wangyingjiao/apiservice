/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.service.dao.station.ServiceStationDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.sys.dao.*;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户工具类
 *
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils {

    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
    private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
    private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
    private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
    private static ServiceStationDao stationDao = SpringContextHolder.getBean(ServiceStationDao.class);

    public static final String USER_CACHE = "userCache";
    public static final String USER_CACHE_ID_ = "id_";
    public static final String USER_CACHE_LOGIN_NAME_ = "ln";
    public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

    public static final String CACHE_AUTH_INFO = "authInfo";
    public static final String CACHE_ROLE_LIST = "roleList";
    public static final String CACHE_MENU_LIST = "menuList";
    public static final String CACHE_AREA_LIST = "areaList";
    public static final String CACHE_OFFICE_LIST = "officeList";
    public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
    public static final String CACHE_STATION_INFO = "stationInfo";

    /**
     * 根据ID获取用户
     *
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id) {
        User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
        if (user == null) {
            user = userDao.get(id);
            if (user == null) {
                return null;
            }
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }
    /**
     * 根据ID获取用户
     *
     * @param id
     * @return 取不到返回null
     */
    public static User getUser(String id) {
           User user = userDao.get(id);
            if (user == null) {
                return null;
            }
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        return user;
    }
    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName) {
        User user = null; //(User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
        if (user == null) {
            user = userDao.getByLoginName(new User(null, loginName));
            if (user == null) {
                return null;
            }
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }

    /**
     * 清除当前用户缓存
     */
    public static void clearCache() {
        removeCache(CACHE_AUTH_INFO);
        removeCache(CACHE_ROLE_LIST);
        removeCache(CACHE_MENU_LIST);
        removeCache(CACHE_AREA_LIST);
        removeCache(CACHE_OFFICE_LIST);
        removeCache(CACHE_OFFICE_ALL_LIST);
        UserUtils.clearCache(getUser());
    }

    /**
     * 清除指定用户缓存
     *
     * @param user
     */
    public static void clearCache(User user) {
        //todo 清空时会报空异常
        try {

            CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
            CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
            CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
            if (user.getOrganization() != null && user.getOrganization().getId() != null) {
                CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOrganization().getId());
            }

        } catch (NullPointerException e) {

        }

    }

    /**
     * 获取当前用户
     *
     * @return 取不到返回 new User()
     */
    public static User getUser() {
        Principal principal = getPrincipal();
        if (principal != null) {
            User user = get(principal.getId());
            if (user != null) {
                return user;
            }
            return new User();
        }
        // 如果没有登录，则返回实例化空的User对象。
        return new User();
    }

    /**
     * 获取当前用户角色列表
     *
     * @return
     */
    public static List<Role> getRoleList() {
        @SuppressWarnings("unchecked")
        List<Role> roleList = null;
        //List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
        if (roleList == null) {
            User user = getUser();
            if (user.isAdmin()) {
                roleList = roleDao.findAllList(new Role());
            } else {
                Role role = new Role();
                //role.getSqlMap().put("dsf", BaseService.dataRoleFilter(user.getCurrentUser(), "a"));
                roleList = roleDao.findRoleListByUser(user);
            }
            putCache(CACHE_ROLE_LIST, roleList);
        }
        return roleList;
    }

    /**
     * 获取当前用户授权菜单
     *
     * @return
     */
    public static List<Menu> getMenuList() {
        @SuppressWarnings("unchecked")
        List<Menu> menuList = (List<Menu>) getCache(CACHE_MENU_LIST);
        if (menuList == null) {
            User user = getUser();
            if (user.isAdmin()) {
                Menu menu = new Menu();
                menu.setType(user.getType());
                menuList = menuDao.findAllList(menu);
            }else {
                if (null != user.getType() && user.getType().equals("sys")) {
                    Menu menu = new Menu();
                    menu.setUserId(user.getId());
                    menu.setType(user.getType());
                    menuList = menuDao.findByUserId(menu);
                } else {
                    BasicOrganization org = user.getOrganization();
                    if (null != org && org.getId().trim().equals("0")) { //add by wyr全平台用户显示左侧菜单栏
                        Menu m = new Menu();
                        m.setUserId(user.getId());
                        m.setType(user.getType());
                        //menuList = menuDao.findByUserId(m);
                        menuList = menuDao.findByUserIdFullPlatform(m);
                    } else {
                        Menu m = new Menu();
                        m.setUserId(user.getId());
                        m.setType(user.getType());
                        //m.getSqlMap().put("dsf","and a.permission not in('class_insert','class_update','class_delete','class','class_view')");
                        menuList = menuDao.findByUserId(m);
                    }
                }
            }
            putCache(CACHE_MENU_LIST, menuList);
       }
        return menuList;
    }

    /**
     * 获取当前用户授权的区域
     *
     * @return
     */
    public static List<Area> getAreaList() {
        @SuppressWarnings("unchecked")
        List<Area> areaList = (List<Area>) getCache(CACHE_AREA_LIST);
        if (areaList == null) {
            areaList = areaDao.findAllList(new Area());
            putCache(CACHE_AREA_LIST, areaList);
        }
        return areaList;
    }

    /**
     * 机构查询，分页和条件搜索
     *
     * @return
    //     */
//    public static Page<Office> getOffices(Page<Office> page, Office office) {
//        office.setPage(page);
//        User user = getUser();
//        if (user.isAdmin()) {
//            page.setList(officeDao.findAllList(office));
//        } else {
//            office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
//            page.setList(officeDao.findList(office));
//        }
//        return page;
//    }

    /**
     * 获取当前用户有权限访问的部门
     *
     * @return
     */
    public static List<Office> getOfficeList() {
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>) getCache(CACHE_OFFICE_LIST);
        if (officeList == null) {
            User user = getUser();
            if (user.isAdmin()) {
                officeList = officeDao.findAllList(new Office());
            } else {
                Office office = new Office();
                office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "org", "ss"));
                officeList = officeDao.findList(office);
            }
            putCache(CACHE_OFFICE_LIST, officeList);
        }
        return officeList;
    }

    /**
     * 获取当前用户有权限访问的部门
     *
     * @return
     */
    public static List<Office> getOfficeAllList() {
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>) getCache(CACHE_OFFICE_ALL_LIST);
        if (officeList == null) {
            officeList = officeDao.findAllList(new Office());
        }
        return officeList;
    }

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取当前登录者对象
     */
    public static Principal getPrincipal() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null) {
                return principal;
            }
//			subject.logout();
        } catch (UnavailableSecurityManagerException e) {

        } catch (InvalidSessionException e) {

        }
        return null;
    }

    public static Session getSession() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(false);
            if (session == null) {
                session = subject.getSession();
            }
            if (session != null) {
                return session;
            }
//			subject.logout();
        } catch (InvalidSessionException e) {

        }
        return null;
    }

    // ============== User Cache ==============

    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
        Object obj = getSession().getAttribute(key);
        return obj == null ? defaultValue : obj;
    }

    public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
        getSession().setAttribute(key, value);
    }

    public static void removeCache(String key) {
//		getCacheMap().remove(key);
        getSession().removeAttribute(key);
    }

//	public static Map<String, Object> getCacheMap(){
//		Principal principal = getPrincipal();
//		if(principal!=null){
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}

    /**
     * 获取当前用户的所属服务站信息
     */
    public static ServiceStation getStationInfo() {
        User user = getUser();
        return stationDao.findStationByUser(user);
    }

    public static List<Menu> getMenuListForPlatform() {
        User user = getUser();
        if (user.getOrganization().getId().equals("0")) {
        	Menu menu = new Menu();
        	menu.setType(user.getType());
            return menuDao.findAllList(menu);
        } else {
            return getMenuList();
        }
    }

    public static List<Menu> getAllMenuListForPlatform(Menu menu) {
            return menuDao.findAllMenuList(menu);
    }

    public static List<Menu> genTreeMenu(String id, List<Menu> menus) {
        ArrayList<Menu> list = new ArrayList<>();
        for (Menu menu : menus) {
            //如果对象的父id等于传进来的id，则进行递归，进入下一轮；
            if (menu.getParentId().equals(id)) {
                List<Menu> menus1 = genTreeMenu(menu.getId(), menus);
                menu.setSubMenus(menus1);
                list.add(menu);
            }
        }
        return list;
    }
    public static List<Menu> genTreeMenuOrder(String id, List<Menu> menus) {
        ArrayList<Menu> list = new ArrayList<>();
        /*for (Menu menu : menus) {
            //如果对象的父id等于传进来的id，则进行递归，进入下一轮；
            if (menu.getParentId().equals(id)) {
                List<Menu> menus1 = genTreeMenu(menu.getId(), menus);
                menu.setSubMenus(menus1);
                list.add(menu);
            }
        }*/
        for (int i = 0; i < menus.size(); i++) {
        	if (menus.get(i).getParentId().equals(id)) {
                List<Menu> menus1 = genTreeMenuOrder(menus.get(i).getId(), menus);
                menus.get(i).setSubMenus(menus1);
                list.add(menus.get(i));
                if (menus.get(i).getPermission().contains("view")) {
                	list.add(menus.size(), menus.get(i));
				}
            }
			
		}
        return list;
    }
    /**
     * 获取当前用户缓存
     *
     * @param id
     * @return 取不到返回null
     */
    public static User getUserCache(String id) {
        User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
        return user;
    }
}
