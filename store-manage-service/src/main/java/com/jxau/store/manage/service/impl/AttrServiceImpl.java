package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.manage.mapper.PmsBaseAttrInfoMapper;
import com.jxau.store.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Override
    public List<PmsBaseAttrInfo> attrInfoList() {
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=pmsBaseAttrInfoMapper.selectAll();
        return pmsBaseAttrInfoList;
    }
}
