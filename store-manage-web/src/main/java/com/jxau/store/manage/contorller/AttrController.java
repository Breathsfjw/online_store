package com.jxau.store.manage.contorller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.beans.PmsBaseAttrValue;
import com.jxau.store.beans.PmsBaseCatalog1;
import com.jxau.store.beans.PmsBaseSaleAttr;
import com.jxau.store.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {
    /**
     * @author fjw
     * 商品发布后台管理系统
     *平台属性三级分类的属性信息，管理平台属性值
     */
    @Reference
    AttrService attrService;
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        List<PmsBaseAttrInfo>  pmsBaseAttrInfos= attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody  PmsBaseAttrInfo pmsBaseAttrInfo){

        String success= attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List getAttrValueList(String attrId){

        List<PmsBaseAttrValue> pmsBaseAttrValueList= attrService.getAttrValueList(attrId);
        return pmsBaseAttrValueList;
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){

        List<PmsBaseSaleAttr> pmsBaseAttrValueList= attrService.baseSaleAttrList();
        return pmsBaseAttrValueList;
    }
}
