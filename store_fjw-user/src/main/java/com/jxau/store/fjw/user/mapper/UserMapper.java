package com.jxau.store.fjw.user.mapper;


import com.jxau.store.beans.UmsMeber;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<UmsMeber> {

    List<UmsMeber> selectAllUser();
}
