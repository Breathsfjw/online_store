package com.jxau.store.manage.mapper;

import com.jxau.store.beans.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
     List<PmsBaseAttrInfo> selectAttrValueListByValueId(@Param("valueSetStr") String valueSetStr);
}
