package com.jxau.store.service;


import com.jxau.store.beans.UmsMember;
import com.jxau.store.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddress(String memberId);
}
