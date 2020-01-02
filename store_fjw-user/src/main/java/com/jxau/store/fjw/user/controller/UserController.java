package com.jxau.store.fjw.user.controller;

import com.jxau.store.fjw.user.bean.UmsMeber;
import com.jxau.store.fjw.user.bean.UmsMemberReceiveAddress;
import com.jxau.store.fjw.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("getUmsMemberReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberID) {
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getUmsMemberReceiveAddress(memberID);
        return umsMemberReceiveAddresses;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMeber> getAllUser() {
        List<UmsMeber> umsMebers = userService.getAllUser();
        return umsMebers;
    }

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return "hello fjw";
    }
}
