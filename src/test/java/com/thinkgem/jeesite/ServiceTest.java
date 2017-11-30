package com.thinkgem.jeesite;

import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:*.xml")
public class ServiceTest {

    @Autowired
    SystemService systemService;

    @Test
    public void getUser(){
        List<User> userByRole = systemService.findUserByRole(new Role("02a25edb9ff64a36996f5fd2662174e3"));
        for (User user : userByRole) {
            System.out.println(user);
        }
    }
}
