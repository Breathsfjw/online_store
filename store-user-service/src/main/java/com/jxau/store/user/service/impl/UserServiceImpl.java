package com.jxau.store.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.UmsMember;
import com.jxau.store.beans.UmsMemberReceiveAddress;
import com.jxau.store.service.UserService;
import com.jxau.store.user.mapper.UmsMemberReceiveAddressMapper;
import com.jxau.store.user.mapper.UserMapper;
import com.jxau.store.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    @Autowired
    RedisUtil redisUtil;


    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList = userMapper.selectAllUser();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberId) {
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
//        return umsMemberReceiveAddressList;

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddressList;

    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");
            if (StringUtils.isNotBlank(umsMemberStr)) {
                UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                return umsMemberFromCache;
            }
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            if (umsMemberFromDb != null) {
                jedis.setex("user:" + umsMember.getPassword() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
        } finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            jedis.setex("user" + memberId + "token", 60 * 60 * 24, token);
        } finally {
            jedis.close();
        }

    }

    @Override
    public void addOauthUser(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsMemberCheck) {
        UmsMember umsMemberCheck1 = userMapper.selectOne(umsMemberCheck);
        return umsMemberCheck1;

    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> umsMemberList = userMapper.select(umsMember);
        if (umsMemberList != null) {
            return umsMemberList.get(0);
        }
        return null;
    }
}