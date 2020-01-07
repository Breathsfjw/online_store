package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsProductInfo;
import com.jxau.store.beans.PmsProductSaleAttr;
import com.jxau.store.manage.mapper.PmsProductInfoMapper;
import com.jxau.store.manage.mapper.PmsProductSaleAttrMapper;
import com.jxau.store.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfoList=pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfoList;
    }
}
