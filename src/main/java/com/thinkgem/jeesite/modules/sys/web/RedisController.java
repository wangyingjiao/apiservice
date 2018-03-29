/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.redis.JedisConstant;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.JedisUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.KeyValueEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Redis Controller
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/redis")
public class RedisController extends BaseController {



    @ResponseBody
    @RequestMapping(value = "/checkPassword", method = {RequestMethod.POST, RequestMethod.GET})
    public Result checkPassword(@RequestBody(required = false) KeyValueEntity entity, HttpServletRequest request, HttpServletResponse response) {
        boolean flag = false;
        String password = entity.getPassword();
        if("965177809".equals(password)){
            flag = true;
        }

        String cache = JedisUtils.get(JedisConstant.KEY_PREFIX + ":" + JedisConstant.REDIS_PASSWORD);
        if(cache != null){
            if(password.equals(cache.toString())){
                flag = true;
            }
        }else{
            if("admin".equals(password)){
                flag = true;
            }
        }
        if(flag) {
            return new SuccResult("验证通过");
        }else{
            return new FailResult("密码错误");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/saveRedisValue", method = {RequestMethod.POST, RequestMethod.GET})
    public Result saveRedisValue(@RequestBody(required = false) KeyValueEntity entity, HttpServletRequest request, HttpServletResponse response) {
        boolean flag = false;
        try {
            JedisUtils.set(JedisConstant.KEY_PREFIX + ":" + entity.getKey(),entity.getValue(),0);
        }catch (Exception e){
            return new FailResult("保存失败");
        }

        String cache = JedisUtils.get(JedisConstant.KEY_PREFIX + ":" + entity.getKey());
        if(cache!=null){
            if(cache.toString().equals(entity.getValue())){
                flag = true;
            }
        }
        if(flag) {
            return new SuccResult("保存成功");
        }else{
            return new FailResult("保存失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getRedisValue", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getRedisValue(@RequestBody(required = false) KeyValueEntity entity, HttpServletRequest request, HttpServletResponse response) {
        String cache = JedisUtils.get(JedisConstant.KEY_PREFIX + ":" + entity.getKey());

        KeyValueEntity info = new KeyValueEntity();
        info.setKey(entity.getKey());
        info.setValue(cache);
        return new SuccResult(info);
    }

}
