package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsBaseCatalog1;
import com.jxau.store.beans.PmsBaseCatalog2;
import com.jxau.store.beans.PmsBaseCatalog3;
import com.jxau.store.manage.mapper.PmsBaseCatalog1Mapper;
import com.jxau.store.manage.mapper.PmsBaseCatalog2Mapper;
import com.jxau.store.manage.mapper.PmsBaseCatalog3Mapper;
import com.jxau.store.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class CatalogServiceImpl implements CatalogService {
    /**
     * @author fjw
     * 商品发布后台管理系统
     *平台属性三级分类的查询
     * 查询一级平台属性分类：getCatalog1()
     * 查询二级平台属性分类：getCatalog2()
     * 查询三级平台属性分类：getCatalog3()
     */
    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1List=pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1List;
    }
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {

        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);

        return pmsBaseCatalog2s;
    }
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {

        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);

        return pmsBaseCatalog3s;
    }
}
