/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.item;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import com.thinkgem.jeesite.modules.service.service.item.SerItemInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenSendDeleteItemResponse;
import com.thinkgem.jeesite.open.entity.OpenSendSaveItemResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 服务项目Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "服务项目", description = "服务项目相关接口")
@RequestMapping(value = "${adminPath}/service/item/serItemInfo")
public class SerItemInfoController extends BaseController {

    @Autowired
    private SerItemInfoService serItemInfoService;

    @ModelAttribute
    public SerItemInfo get(@RequestParam(required = false) String id) {
        SerItemInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serItemInfoService.get(id);
        }
        if (entity == null) {
            entity = new SerItemInfo();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    @RequiresPermissions("project_insert")
    @ApiOperation("新增保存服务项目")
    public Result saveData(@RequestBody SerItemInfo serItemInfo) {
        List<String> errList = errors(serItemInfo);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }

        List<String> haveTags = new ArrayList<>();
        List<String> sysTags = serItemInfo.getSysTags();
        //系统标签格式：系统标签1,系统标签2,系统标签3,
        if (null != sysTags){
            for(String sysTag : sysTags){
                if(haveTags.contains(sysTag)){
                    return new FailResult("标签重复");
                }else{
                    haveTags.add(sysTag);
                }
            }
            String sys = JsonMapper.toJsonString(sysTags);
            serItemInfo.setTags(sys);
        }

        List<String> customTags = serItemInfo.getCustomTags();
        // 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
        if (null != customTags){
            for(String cusTag : customTags){
                if(haveTags.contains(cusTag)){
                    return new FailResult("标签重复");
                }else{
                    haveTags.add(cusTag);
                }
            }
            String tags = JsonMapper.toJsonString(customTags);
            serItemInfo.setCusTags(tags);
        }

        HashMap<String,Object> map = serItemInfoService.saveItem(serItemInfo);
        return doSendItem(map);
    }

    @ResponseBody
    @RequestMapping(value = "upData", method = {RequestMethod.POST})
    @RequiresPermissions("project_update")
    @ApiOperation("编辑保存服务项目")
    public Result upData(@RequestBody SerItemInfo serItemInfo) {
        List<String> errList = errors(serItemInfo);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }

        List<String> haveTags = new ArrayList<>();
        List<String> sysTags = serItemInfo.getSysTags();
        //系统标签格式：系统标签1,系统标签2,系统标签3,
        if (null != sysTags){
            for(String sysTag : sysTags){
                if(haveTags.contains(sysTag)){
                    return new FailResult("标签重复");
                }else{
                    haveTags.add(sysTag);
                }
            }
            String sys = JsonMapper.toJsonString(sysTags);
            serItemInfo.setTags(sys);
        }

        List<String> customTags = serItemInfo.getCustomTags();
        // 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
        if (null != customTags){
            for(String cusTag : customTags){
                if(haveTags.contains(cusTag)){
                    return new FailResult("标签重复");
                }else{
                    haveTags.add(cusTag);
                }
            }
            String tags = JsonMapper.toJsonString(customTags);
            serItemInfo.setCusTags(tags);
        }

        HashMap<String,Object> map = serItemInfoService.saveItem(serItemInfo);
        return doSendItem(map);
    }

    @ResponseBody
    @RequestMapping(value = "upDataSortNum", method = {RequestMethod.POST})
    @RequiresPermissions("project_detail")
    @ApiOperation("保存服务项目图文详情")
    public Result upDataSortNum(@RequestBody SerItemInfo serItemInfo) {
        HashMap<String,Object> map =  serItemInfoService.updateSerItemPicNum(serItemInfo);
        return doSendItem(map);
    }

    @ResponseBody
    @RequestMapping(value = "sendData", method = {RequestMethod.POST})
    @ApiOperation("对接")
    public Result sendData(@RequestBody SerItemInfo serItemInfo) {
        HashMap<String,Object> map =  serItemInfoService.sendItemData(serItemInfo);
        return doSendItem(map);
    }
    public Result doSendItem(HashMap<String,Object> map){
        SerItemInfo serItemInfo = (SerItemInfo) map.get("item");
        try {
            // 机构有对接方E店CODE
            if(map.get("jointEshopCode") != null && StringUtils.isNotEmpty(map.get("jointEshopCode").toString())){
                OpenSendSaveItemResponse sendResponse = OpenSendUtil.openSendSaveItem((SerItemInfo)map.get("info"),map.get("jointEshopCode").toString());
                if(sendResponse != null && sendResponse.getCode() == 0){
                    Map<String, Object> responseData = (Map<String, Object>)JsonMapper.fromJsonString(sendResponse.getData().toString(), Map.class);
                    Iterator iter = responseData.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        SerItemCommodity goods = new SerItemCommodity();
                        String key = entry.getKey().toString();
                        String goodsId = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
                        goods.setId(goodsId);
                        goods.setJointGoodsCode(entry.getValue().toString());
                        User user = new User();
                        user.setId("gasq001");
                        goods.setUpdateBy(user);
                        goods.setUpdateDate(new Date());
                        serItemInfoService.updateCommodityJointCode(goods);
                    }
                    serItemInfo.setJointStatus("yes");
                    User user = new User();
                    user.setId("gasq001");
                    serItemInfo.setUpdateBy(user);
                    serItemInfo.setUpdateDate(new Date());
                    serItemInfoService.updateJointStatus(serItemInfo);
                }else{
                    serItemInfo.setJointStatus("no");
                    User user = new User();
                    user.setId("gasq001");
                    serItemInfo.setUpdateBy(user);
                    serItemInfo.setUpdateDate(new Date());
                    serItemInfoService.updateJointStatus(serItemInfo);
                    return new SuccResult("保存成功；对接商品失败，请点击列表中对接按钮，手动完成商品对接");
                }
            }
        }catch (Exception e){
            serItemInfo.setJointStatus("no");
            User user = new User();
            user.setId("gasq001");
            serItemInfo.setUpdateBy(user);
            serItemInfo.setUpdateDate(new Date());
            serItemInfoService.updateJointStatus(serItemInfo);
            return new SuccResult("保存成功；对接商品失败，请点击列表中对接按钮，手动完成商品对接");
        }

        return new SuccResult("保存成功");
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务项目列表")
    @RequiresPermissions("project_view")
    public Result listData(@RequestBody(required = false) SerItemInfo serItemInfo, HttpServletRequest request, HttpServletResponse response) {
        if(null == serItemInfo){
            serItemInfo = new SerItemInfo();
        }
        Page<SerItemInfo> serItemInfoPage = new Page<>(request, response);
        Page<SerItemInfo> page = serItemInfoService.findPage(serItemInfoPage, serItemInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "formData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("根据ID查找服务项目")
    public Result formData(@RequestBody SerItemInfo serItemInfo) {
        SerItemInfo entity = null;
        if (StringUtils.isNotBlank(serItemInfo.getId())) {
            entity = serItemInfoService.getData(serItemInfo.getId());

            String pictureDetail = entity.getPictureDetail();
            if(null != pictureDetail){
                List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(pictureDetail,ArrayList.class);
                entity.setPictureDetails(pictureDetails);
            }
            String tags = entity.getTags();
            if (null != tags){
                List<String> sysTags = (List<String>) JsonMapper.fromJsonString(tags, ArrayList.class);
                entity.setSysTags(sysTags);
            }
            String cusTags = entity.getCusTags();
            if (null != cusTags){
                List<String> customTags = (List<String>) JsonMapper.fromJsonString(cusTags, ArrayList.class);
                entity.setCustomTags(customTags);
            }
            String picture = entity.getPicture();
            if(null != picture){
                List<String> pictures = (List<String>) JsonMapper.fromJsonString(picture,ArrayList.class);
                entity.setPictures(pictures);
            }
        }
        if (entity == null) {
            return new FailResult("未找到此id对应的服务项目。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    @RequiresPermissions("project_delete")
    @RequestMapping(value = "deleteData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("删除服务项目")
    public Result deleteData(@RequestBody SerItemInfo info) {
        if(info == null){
            return new FailResult("未找到项目ID");
        }
        //前台传过来项目id 字段名:id
        String itemId = info.getId();
        if(StringUtils.isBlank(itemId)){
            return new FailResult("未找到项目ID");
        }

        HashMap<String,Object> map =  serItemInfoService.getDeleteGoodsSendList(info);
        try {
            // 机构有对接方E店CODE
            if(map.get("jointEshopCode") != null && StringUtils.isNotEmpty(map.get("jointEshopCode").toString())){
                SerItemInfo serItemInfo = (SerItemInfo)map.get("info");
                if(serItemInfo != null && serItemInfo.getCommoditys() != null && serItemInfo.getCommoditys().size() > 0){
                    List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();
                    //对接
                    OpenSendDeleteItemResponse sendResponse = OpenSendUtil.openSendDeleteItem(serItemInfo,map.get("jointEshopCode").toString());
                    if(sendResponse != null && sendResponse.getCode() == 0){
                        List<String> dataFail = new ArrayList<>();//属于专场商品或其相关的组合商品属于专场商品
                        List<String> datainventoryfail = new ArrayList<>();//还有可售库存该商品和其相关组合商品不可删除
                        if(sendResponse.getDatafail() != null){
                            Map<String, Object> responseData1 = (Map<String, Object>)JsonMapper.fromJsonString(sendResponse.getDatafail().toString(), Map.class);
                            Iterator iter1 = responseData1.entrySet().iterator();
                            while (iter1.hasNext()) {
                                Map.Entry entry1 = (Map.Entry) iter1.next();
                                String key = entry1.getKey().toString();
                                String value = entry1.getValue().toString();
                                dataFail.add(key);
                            }
                        }
                        if(sendResponse.getDatainventoryfail() != null) {
                            Map<String, Object> responseData2 = (Map<String, Object>) JsonMapper.fromJsonString(sendResponse.getDatainventoryfail().toString(), Map.class);
                            Iterator iter2 = responseData2.entrySet().iterator();
                            while (iter2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iter2.next();
                                String key = entry2.getKey().toString();
                                String value = entry2.getValue().toString();
                                datainventoryfail.add(key);
                            }
                        }

                        List<String> failCommodityIds = new ArrayList<>();
                        HashMap<String,String> commodityIds = new HashMap<>();// joinCode - id
                        HashMap<String,String> commodityNames = new HashMap<>();// joinCode - name
                        for(SerItemCommodity commodity : commoditys){
                            commodityIds.put(commodity.getJointGoodsCode(),commodity.getId());
                            commodityNames.put(commodity.getJointGoodsCode(),commodity.getName());
                        }
                        if(dataFail != null && dataFail.size() != 0){//专场商品
                            for (String fail : dataFail){
                                failCommodityIds.add(commodityIds.get(fail));
                            }
                        }else if (datainventoryfail != null && datainventoryfail.size() != 0){//有可售库存
                            for (String fail : dataFail){
                                failCommodityIds.add(commodityIds.get(fail));
                            }
                        }

                        if(failCommodityIds.size()==0){
                            serItemInfoService.delete(info);
                            return new SuccResult("删除成功");
                        }else{
                            for(SerItemCommodity commodity : commoditys) {
                                if(!failCommodityIds.contains(commodity.getId())){
                                    serItemInfoService.deleteGoodsInfo(commodity);
                                }
                            }

                            return new FailResult("部分商品删除成功");
                        }
                    }else{
                        return new FailResult("删除失败");
                    }
                }else{
                    return new FailResult("删除失败");
                }
            }
        }catch (Exception e){
            return new FailResult("系统异常");
        }

        return new SuccResult("删除成功");
    }

    @ResponseBody
    @RequiresPermissions("project_update")
    @RequestMapping(value = "deleteGoodsData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("单个删除服务项目商品")
    public Result deleteGoodsData(@RequestBody SerItemCommodity serItemCommodity) {
        if(serItemCommodity == null){
            return new FailResult("未找到商品ID");
        }
        //前台传过来商品id 字段名:id
        String goodsId = serItemCommodity.getId();
        if(StringUtils.isBlank(goodsId)){
            return new FailResult("未找到商品ID");
        }
        HashMap<String,Object> map =  serItemInfoService.getDeleteGoodsSendInfo(serItemCommodity);
        try {
            // 机构有对接方E店CODE
            if(map.get("jointEshopCode") != null && StringUtils.isNotEmpty(map.get("jointEshopCode").toString())){
                SerItemInfo serItemInfo = (SerItemInfo)map.get("info");
                if(serItemInfo != null && serItemInfo.getCommoditys() != null && serItemInfo.getCommoditys().size() > 0){
                    OpenSendDeleteItemResponse sendResponse = OpenSendUtil.openSendDeleteItem(serItemInfo,map.get("jointEshopCode").toString());
                    if(sendResponse != null && sendResponse.getCode() == 0){

                        List<String> dataFail = new ArrayList<>();//属于专场商品或其相关的组合商品属于专场商品
                        List<String> datainventoryfail = new ArrayList<>();//还有可售库存该商品和其相关组合商品不可删除
                        if(sendResponse.getDatafail() != null){
                            Map<String, Object> responseData1 = (Map<String, Object>)JsonMapper.fromJsonString(sendResponse.getDatafail().toString(), Map.class);
                            Iterator iter1 = responseData1.entrySet().iterator();
                            while (iter1.hasNext()) {
                                Map.Entry entry1 = (Map.Entry) iter1.next();
                                String key = entry1.getKey().toString();
                                String value = entry1.getValue().toString();
                                dataFail.add(key);
                            }
                        }
                        if(sendResponse.getDatainventoryfail() != null) {
                            Map<String, Object> responseData2 = (Map<String, Object>) JsonMapper.fromJsonString(sendResponse.getDatainventoryfail().toString(), Map.class);
                            Iterator iter2 = responseData2.entrySet().iterator();
                            while (iter2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iter2.next();
                                String key = entry2.getKey().toString();
                                String value = entry2.getValue().toString();
                                datainventoryfail.add(key);
                            }
                        }

                        if(dataFail != null && dataFail.size() != 0){
                            return new FailResult("删除失败，第三方不允许删除此商品");
                        }else if (datainventoryfail != null && datainventoryfail.size() != 0){
                            return new FailResult("删除失败，第三方不允许删除此商品");
                        }else {
                            serItemInfoService.deleteGoodsInfo(serItemCommodity);
                        }
                    }else{
                        return new FailResult("删除失败，第三方不允许删除此商品");
                    }
                }
            }
        }catch (Exception e){
            return new FailResult("删除失败，第三方不允许删除此商品");
        }

        return new SuccResult("删除成功");
    }

   /* @ResponseBody
    @RequestMapping(value = "getAllCityCodes", method = {RequestMethod.POST})
    @ApiOperation("机构或分类下定向城市")
    public Result getAllCityCodes(@RequestBody SerItemInfo serItemInfo) {
        if(null == serItemInfo){
            serItemInfo = new SerItemInfo();
        }
        User user = UserUtils.getUser();
        serItemInfo.setOrgId(user.getOrganization().getId());//机构ID
        List<SerCityScope> list = serItemInfoService.getAllCityCodes(serItemInfo);
        return new SuccResult(list);
    }*/

}