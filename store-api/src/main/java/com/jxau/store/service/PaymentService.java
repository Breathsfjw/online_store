package com.jxau.store.service;

import com.jxau.store.beans.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void save(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendDelayPaymentResultCheckQueue(String outTradeNo, int i);

    Map<String, Object> checkAlipayPayment(String out_trade_no);
}
