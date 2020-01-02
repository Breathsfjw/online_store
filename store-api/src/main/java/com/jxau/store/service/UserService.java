package com.jxau.store.service;


import com.jxau.store.beans.UmsMeber;
import com.jxau.store.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMeber> getAllUser();

    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberId);
}
