package com.jxau.store.service;

import com.jxau.store.beans.PmsSearchParam;
import com.jxau.store.beans.PmsSearchSkuInfo;

import java.util.List;

 public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
