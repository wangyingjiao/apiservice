/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.item;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.item.SerGasqSort;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.item.SortTree;
import com.thinkgem.jeesite.modules.service.service.item.SerGasqSortService;
import com.thinkgem.jeesite.modules.service.service.item.SerItemInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "分类", description = "分类相关接口")
@RequestMapping(value = "${adminPath}/service/item/serGasqSort")
public class SerGasqSortController extends BaseController {

    @Autowired
    private SerGasqSortService serGasqSortService;


    @ResponseBody
    @RequestMapping(value = "getList", method = {RequestMethod.POST})
    @ApiOperation("分类展示")
    public Result getList(SerGasqSort serGasqSort) {
        if (StringUtils.isBlank(serGasqSort.getPid())){
            serGasqSort.setPid("0");
        }
        List<SortTree> sortList = serGasqSortService.getSortList();

        return new SuccResult(sortList);
    }

}