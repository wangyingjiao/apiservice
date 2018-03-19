package com.thinkgem.jeesite.modules.service.web.appVersion;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.appVersion.AppVersion;
import com.thinkgem.jeesite.modules.service.service.appVersion.AppVersionService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.thinkgem.jeesite.modules.service.service.appVersion.AppVersionService.CACHE_NEWEST_VERSION;

/**
 * APP发版管理Controller
 *
 * @author a
 * @version 2018-01-29
 */

@Controller
@Api(tags = "APP发版管理", description = "APP发版管理相关接口")
@RequestMapping(value = "${adminPath}/service/appVersion/appVersion")
public class AppVersionController extends BaseController {

    @Autowired
    private AppVersionService appVersionService;

    @ResponseBody
    //@RequiresPermissions("skill_view")
    @RequestMapping(value = "listData", method = { RequestMethod.POST, RequestMethod.GET })
    @ApiOperation("获取技能APP发版列表")
    public Result listData(@RequestBody(required = false) AppVersion appVersion, HttpServletRequest request, HttpServletResponse response) {
        if (appVersion == null) {
            appVersion = new AppVersion();
        }
        Page<AppVersion> serSkillPage = new Page<>(request, response);
        Page<AppVersion> page = appVersionService.findPage(serSkillPage, appVersion);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "saveData", method = { RequestMethod.POST })
    //@RequiresPermissions("skill_insert")
    @ApiOperation("保存技能APP发版")
    public Result saveData(@RequestBody AppVersion appVersion) {
        List<String> errList = errors(appVersion);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }

        /*if (!StringUtils.isNotBlank(appVersion.getId())) {// 新增时验证重复
            //User user = UserUtils.getUser();
            //appVersion.setOrgId(user.getOrganization().getId());// 机构ID
            if (0 != appVersionService.checkDataName(serSkillInfo)) {
                return new FailResult("当前机构已经包含技能名称" + serSkillInfo.getName() + "");
            }
        }*/
        AppVersion newest = appVersionService.getNewest();
        if (appVersion.getBuild().compareTo(newest.getBuild()) != 1){
            return new FailResult("build号不得小于等于当前版本");
        }
        int count = appVersionService.getVersionNumber(appVersion);
        if (count>0){
            return new FailResult("版本号不可与历史版本重复");
        }
        appVersion.setCreateBy(UserUtils.getUser());
        appVersion.setCreateDate(new Date());
        appVersionService.save(appVersion);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    @RequestMapping(value = "formData", method = { RequestMethod.POST })
    @ApiOperation("根据ID查找APP发版信息")
    public Result formData(@RequestBody AppVersion appVersion) {
        AppVersion entity = null;
        if (StringUtils.isNotBlank(appVersion.getId())) {
            entity = appVersionService.getData(appVersion.getId());
        }
        if (entity == null) {
            return new FailResult("未找到此id对应的发版信息。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    @RequestMapping(value = "upData", method = { RequestMethod.POST })
    //@RequiresPermissions("skill_update")
    @ApiOperation("修改保存APP发版信息")
    public Result upData(@RequestBody AppVersion appVersion) {
        List<String> errList = errors(appVersion);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }
        appVersionService.save(appVersion);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    //@RequiresPermissions("skill_delete")
    @RequestMapping(value = "deleteAppVersion", method = { RequestMethod.POST, RequestMethod.GET })
    @ApiOperation("删除APP发版信息")
    public Result deleteSortInfo(@RequestBody AppVersion appVersion) {
        AppVersion newest = appVersionService.getNewest();
        if(!appVersion.getId().equals(newest.getId())){
            return new FailResult("非最新版本，不能删除");
        }
        appVersionService.delete(appVersion);
        return new SuccResult("删除成功");
    }


    @ResponseBody
    @RequestMapping(value = "getNewest", method = { RequestMethod.POST })
    @ApiOperation("获取最新APP版本")
    public Result getNewest() {
        Object cache = CacheUtils.get(CACHE_NEWEST_VERSION);
        if (cache==null){
            cache = appVersionService.getNewest();
            CacheUtils.put(CACHE_NEWEST_VERSION,cache);
        }
        return new SuccResult(cache);
    }
}
