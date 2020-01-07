package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PmsBaseAttrInfo;
import com.jxau.store.beans.PmsBaseAttrValue;
import com.jxau.store.beans.PmsBaseSaleAttr;
import com.jxau.store.manage.mapper.PmsBaseAttrInfoMapper;
import com.jxau.store.manage.mapper.PmsBaseAttrValueMapper;
import com.jxau.store.manage.mapper.PmsBaseSaleAttrMappeer;
import com.jxau.store.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    PmsBaseSaleAttrMappeer pmsBaseSaleAttrMappeer;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        return pmsBaseAttrInfoList;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String Id = pmsBaseAttrInfo.getId();
        String attrName = pmsBaseAttrInfo.getAttrName();

        if (StringUtils.isBlank(Id)) {
            // id为空，保存
            // 保存属性
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            // 保存属性值
            List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        } else {
            if (StringUtils.isBlank(attrName)) {
                PmsBaseAttrValue pmsBaseAttrValuedel = new PmsBaseAttrValue();
                pmsBaseAttrValuedel.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.delete(pmsBaseAttrValuedel);
                pmsBaseAttrInfoMapper.delete(pmsBaseAttrInfo);
            } else {
                // id不空，修改

                // 属性修改
                Example example = new Example(PmsBaseAttrInfo.class);
                example.createCriteria().andEqualTo("id", pmsBaseAttrInfo.getId());
                pmsBaseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);


                // 属性值修改
                // 按照属性id删除所有属性值


                PmsBaseAttrValue pmsBaseAttrValuedel = new PmsBaseAttrValue();
                pmsBaseAttrValuedel.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.delete(pmsBaseAttrValuedel);
                // 删除后，将新的属性值插入
                List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValueList) {
                    pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
                }

            }
        }
        return "success";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValueList;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMappeer.selectAll();
    }


}
