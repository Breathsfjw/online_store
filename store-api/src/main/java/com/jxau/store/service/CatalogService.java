package com.jxau.store.service;

import com.jxau.store.beans.PmsBaseCatalog1;
import com.jxau.store.beans.PmsBaseCatalog2;
import com.jxau.store.beans.PmsBaseCatalog3;

import java.util.List;

public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();


    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);
}
