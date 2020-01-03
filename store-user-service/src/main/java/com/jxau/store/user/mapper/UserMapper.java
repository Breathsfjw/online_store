package com.jxau.store.user.mapper;


import com.jxau.store.beans.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface UserMapper extends Mapper<UmsMember> {
    List<UmsMember> selectAllUser();
}
