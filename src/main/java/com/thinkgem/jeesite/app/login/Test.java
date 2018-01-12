package com.thinkgem.jeesite.app.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Administrator on 2018/1/12.
 */
@Controller
public class Test {

    @RequestMapping(value = "${appPath}/test",  method = {RequestMethod.POST, RequestMethod.GET})
    public void  test(){
        System.out.println("aaaaaaaaaaaaa");
    }
}
