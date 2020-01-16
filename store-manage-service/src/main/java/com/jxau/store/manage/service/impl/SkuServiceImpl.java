package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsSkuAttrValue;
import com.jxau.store.beans.PmsSkuImage;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.beans.PmsSkuSaleAttrValue;
import com.jxau.store.manage.mapper.PmsSkuAttrValueMapper;
import com.jxau.store.manage.mapper.PmsSkuImageMapper;
import com.jxau.store.manage.mapper.PmsSkuInfoMapper;
import com.jxau.store.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.jxau.store.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    /**
     * @author fjw
     * 商品发布后台管理系统
     * sku：商品库存单元，商品库存单元管理
     * 保存商品库存单元信息：saveSkuInfo
     */

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }
}
