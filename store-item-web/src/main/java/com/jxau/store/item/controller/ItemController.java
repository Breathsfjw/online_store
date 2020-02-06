package com.jxau.store.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.PmsProductSaleAttr;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.beans.PmsSkuSaleAttrValue;
import com.jxau.store.service.SkuService;
import com.jxau.store.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class ItemController {
    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;


    //    public String item(@PathVariable String skuId ){
//        return "item";
//    }
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map) {
//        获取请求http地址
//        String remoteAddr = (HttpServletRequest)request.getRemoteAddr();

        // request.getHeader("");// nginx负载均衡
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        //skuInfo对象
        map.put("skuInfo", pmsSkuInfo);
        //销售属性列表
//        System.out.print(pmsSkuInfo.getId());
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), pmsSkuInfo.getId());
//        System.out.print(pmsSkuInfo.getId());
        map.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);
        //skuSaleAttrHash.put(k, v);//代码优化，通过map将销售属性Id封装为K，skuId封装为V，转化成json数据传递给界面
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        for (PmsSkuInfo pmsSkuInfo1 :
                skuInfos) {
            String K = "";
            String V = pmsSkuInfo1.getId();
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo1.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue :
                    pmsSkuSaleAttrValueList) {
                K += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";// "239|245"
            }
            skuSaleAttrHash.put(K, V);

        }
        Set<String> keys = skuSaleAttrHash.keySet();   //此行可省略，直接将map.keySet()写在for-each循环的条件中
        for(String key:keys){
            System.out.println("key值："+key+" value值："+skuSaleAttrHash.get(key));
        }


        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);
        return "item";


    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }
}
