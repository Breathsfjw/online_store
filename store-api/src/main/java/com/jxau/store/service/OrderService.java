package com.jxau.store.service;

import com.jxau.store.beans.OmsOrder;

public interface OrderService {
    String genTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOmsOrder(OmsOrder omsOrder);
}
