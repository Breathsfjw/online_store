package com.jxau.store.beans;

import java.io.Serializable;
import java.util.List;
/**@author
 * 平台搜索参数信息对应的实体对象
 */

public class PmsSearchParam implements Serializable{

    private String catalog3Id;

    private String keyword;

    String[] valueId;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }
}

