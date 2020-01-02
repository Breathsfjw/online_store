package com.jxau.store.fjw.user.service;


import com.jxau.store.fjw.user.bean.UmsMeber;
import com.jxau.store.fjw.user.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMeber> getAllUser();

    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberId);
}
