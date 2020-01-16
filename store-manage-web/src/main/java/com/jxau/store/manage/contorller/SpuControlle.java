package com.jxau.store.manage.contorller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.beans.PmsProductImage;
import com.jxau.store.beans.PmsProductInfo;
import com.jxau.store.beans.PmsProductSaleAttr;
import com.jxau.store.manage.utils.PmsUploadUtil;
import com.jxau.store.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@Controller
public class SpuControlle {
    /***
     * @author fjw
     * 商品发布后台管理系统
     *spu：标准商品单元，标准商品管理单元
     * fastdfs图片存储服务器，存储ip：192.168.174.140
     */
    @Reference
    SpuService spuService;

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id) {

        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {

        spuService.saveSpuInfo(pmsProductInfo);


        return "success";
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {

        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        MultipartFile multipartFile1 = multipartFile;
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile1);
        //spuService.fileUpload(imgUrl);


        return imgUrl;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrList(spuId);


        return pmsProductSaleAttrList;
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId) {

        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }
}
