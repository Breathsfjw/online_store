package com.jxau.store.manage.contorller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.beans.PmsBaseCatalog1;
import com.jxau.store.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {
    @Reference
    AttrService attrService;
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        List<PmsBaseAttrInfo>  pmsBaseAttrInfos= attrService.attrInfoList();
        return pmsBaseAttrInfos;
    }
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public List<PmsBaseAttrInfo> saveAttrInfo(String catalog3Id){

        List<PmsBaseAttrInfo>  pmsBaseAttrInfos= attrService.attrInfoList();
        return pmsBaseAttrInfos;
    }
}
