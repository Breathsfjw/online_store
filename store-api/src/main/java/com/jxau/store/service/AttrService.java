package com.jxau.store.service;

import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.beans.PmsBaseAttrValue;
import com.jxau.store.beans.PmsBaseSaleAttr;

import java.util.List;

public interface AttrService {
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
