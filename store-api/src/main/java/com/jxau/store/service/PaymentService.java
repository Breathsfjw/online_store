package com.jxau.store.service;

import com.jxau.store.beans.PaymentInfo;

public interface PaymentService {
    void save(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);
}
