package com.jxau.store.fjw.user.service.impl;

import com.jxau.store.beans.UmsMember;
import com.jxau.store.beans.UmsMemberReceiveAddress;
import com.jxau.store.fjw.user.mapper.UmsMemberReceiveAddressMapper;
import com.jxau.store.fjw.user.mapper.UserMapper;
import com.jxau.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;


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

        Example example=new Example(UmsMemberReceiveAddress.class);
            example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddressList;

    }
}