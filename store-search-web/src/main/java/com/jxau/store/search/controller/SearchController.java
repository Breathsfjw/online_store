package com.jxau.store.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.beans.PmsSearchParam;
import com.jxau.store.beans.PmsSearchSkuInfo;
import com.jxau.store.beans.PmsSkuAttrValue;
import com.jxau.store.service.AttrService;
import com.jxau.store.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {// 三级分类id、关键字、

        // 调用搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
//        String pmsSearchSkuInfosstr= JSON.toJSONString(pmsSearchSkuInfos);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);
        Set<String> valueSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo :
                pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue :
                    skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueSet.add(valueId);
            }
        }
        List<PmsBaseAttrInfo> pmsBaseAttrInfos=attrService.getAttrValueListByValueId(valueSet);
        modelMap.put("attrList",pmsBaseAttrInfos);
        return "list";
    }
}
