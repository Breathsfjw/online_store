package com.jxau.store.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.UmsMember;
import com.jxau.store.beans.UmsMemberReceiveAddress;
import com.jxau.store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Reference
    UserService userService;

    @RequestMapping("getUmsMemberReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberID) {
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getUmsMemberReceiveAddress(memberID);
        return umsMemberReceiveAddresses;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return "hello fjw";
    }
}
