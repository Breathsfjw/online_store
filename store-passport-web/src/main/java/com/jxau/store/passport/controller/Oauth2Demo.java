package com.jxau.store.passport.controller;

import com.alibaba.fastjson.JSON;
import com.jxau.store.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

public class Oauth2Demo {
    public static void main(String[] args) {
        getUser_Info();
    }

    public static String getCode() {
        // 1 获得授权码
        // 3366674761
        // http://passport.gmall.com:8085/vlogin
        String s = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=3366674761&response_type=code&redirect_uri=http://passport.store.com:8085/vlogin");
        System.out.println(s);
        return null;
    }

    public static String getAccessToken() {
//        授权码请求公式
//https://api.weibo.com/oauth2/access_token?
// client_id=YOUR_CLIENT_ID&             3366674761
// client_secret=YOUR_CLIENT_SECRET&    452ff7bb0b5ed4e742c91a225c57376b
// grant_type=authorization_code&
// redirect_uri=YOUR_REGISTERED_REDIRECT_URI&
// code=CODE
        String s = "https://api.weibo.com/oauth2/access_token?";
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("client_id", "3366674761");
        parmMap.put("client_secret", "452ff7bb0b5ed4e742c91a225c57376b");
        parmMap.put("grant_type", "authorization_code");
        parmMap.put("redirect_uri", "http://passport.store.com:8085/vlogin");
        parmMap.put("code", "c417f88b44b32815d25ac0b30897b8d3");
        String s1 = HttpclientUtil.doPost(s, parmMap);
        Map<String,String> access_map = JSON.parseObject(s1, Map.class);

        System.out.println(s1);
        return access_map.get("access_token");
    }

    //    {"access_token":"2.00nYBdjGptNqfD4365dd1c03HIdiZB","remind_in":"157679999","expires_in":157679999,"uid":"6171032905","isRealName":"true"}
    public static Map<String,String> getUser_Info() {
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00nYBdjGptNqfD4365dd1c03HIdiZB&uid=6171032905";
        String user_json = HttpclientUtil.doGet(s4);
        Map userInfoMap = JSON.parseObject(user_json, Map.class);
        System.out.println(userInfoMap);
        return userInfoMap;
    }
    }
