package com.jxau.store.service;

import com.jxau.store.beans.PmsProductInfo;
import com.jxau.store.beans.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);
}
