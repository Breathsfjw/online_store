package com.jxau.store.manage.mapper;

import com.jxau.store.beans.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> selectspuSaleAttrListCheckBySku(@Param("productId") String productId, @Param("skuId") String skuId);
}
