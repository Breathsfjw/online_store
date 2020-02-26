package com.jxau.store.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.UmsMember;
import com.jxau.store.service.UserService;
import com.jxau.store.util.CookieUtil;
import com.jxau.store.util.HttpclientUtil;
import com.jxau.store.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;

    @RequestMapping("vlogin")
    public String vlogin(String code, HttpServletRequest request, HttpServletResponse response) {
        String token="";
        Map<String, String> accessMap = getAccess_token(code);
        String uid = accessMap.get("uid");
        String access_token = accessMap.get("access_token");
        Map<String, String> user_info = getUser_info(access_token, uid);
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((String)user_info.get("idstr"));
        umsMember.setCity((String)user_info.get("location"));
        umsMember.setNickname((String)user_info.get("screen_name"));
        umsMember.setCreateTime(new Date());
        String g = "0";
        String gender = (String)user_info.get("gender");
        if(gender.equals("m")){
            g = "1";
        }
        umsMember.setGender(g);
        umsMember.setMemberLevelId("4");
        umsMember.setIcon(user_info.get("profile_image_url"));
        UmsMember umsMemberCheck = new UmsMember();
        umsMemberCheck.setSourceUid(user_info.get("idstr"));
        UmsMember umsMember1 = userService.checkOauthUser(umsMemberCheck);
        if (umsMember1==null){
            userService.addOauthUser(umsMember);
        }else{
            umsMember=umsMember1;
        }
        token = getString(request, umsMember);
        // 将token存入redis一份
        String memberId = umsMember.getId();
        request.setAttribute("memberId", umsMember.getId());
        request.setAttribute("nickname", umsMember.getNickname());
        if(StringUtils.isNotBlank(token)){
            CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
        }
        return "redirect:http://search.store.com:8083/index?token="+token;
    }

    private Map<String, String> getUser_info(String accessToken, String uid) {
        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=" + accessToken + "&uid=" + uid;
        String user_json = HttpclientUtil.doGet(s4);
        Map<String, String> user_map = JSON.parseObject(user_json, Map.class);
        return user_map;
    }

    private Map<String, String> getAccess_token(String code) {
        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("client_id", "3366674761");
        paramMap.put("client_secret", "452ff7bb0b5ed4e742c91a225c57376b");
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("redirect_uri", "http://passport.store.com:8085/vlogin");
        paramMap.put("code", code);// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String, String> access_map = JSON.parseObject(access_token_json, Map.class);
        return access_map;
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp, HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        // 通过jwt校验token真假
        Map<String, Object> objectMap = JwtUtil.decode(token, "2020store0605", currentIp);
        if (objectMap != null) {
            map.put("memberId", (String) objectMap.get("memberId"));
            map.put("nickname", (String) objectMap.get("nickname"));
            map.put("status", "success");
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request,HttpServletResponse response) {

        String token = "";
        // 调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = userService.login(umsMember);
        if (umsMemberLogin != null) {
            // 用jwt制作token
            token = getString(request, umsMemberLogin);


        }
        request.setAttribute("memberId", umsMemberLogin.getId());
        request.setAttribute("nickname", umsMemberLogin.getNickname());
        if(StringUtils.isNotBlank(token)){
            CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
        }
        return token;
    }

    private String getString(HttpServletRequest request, UmsMember umsMemberLogin) {
        String token;
        String memberId = umsMemberLogin.getId();
        String nickname = umsMemberLogin.getNickname();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);
        userMap.put("nickname", nickname);


        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();// 从request中获取ip
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
            // 按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("2020store0605", userMap, ip);

            // 按照设计的算法对参数进行加密后，生成token
            userService.addUserToken(token, memberId);
        } else {
            token = "fail";
        }
        return token;
    }

    @RequestMapping("index")

    public String index(String ReturnUrl, ModelMap map) {

        map.put("ReturnUrl", ReturnUrl);
        return "index";
    }
}
