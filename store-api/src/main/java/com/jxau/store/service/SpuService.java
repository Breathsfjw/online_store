package com.jxau.store.service;

import com.jxau.store.beans.PmsProductImage;
import com.jxau.store.beans.PmsProductInfo;
import com.jxau.store.beans.PmsProductSaleAttr;
import com.jxau.store.beans.PmsSkuInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
