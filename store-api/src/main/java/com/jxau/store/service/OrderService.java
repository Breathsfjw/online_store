package com.jxau.store.service;

import com.jxau.store.beans.OmsOrder;

import java.util.List;

public interface OrderService {
    String genTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOmsOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOrderSn(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);

    List<OmsOrder> getOrderByMemberId(String memberId);
}
