package com.jxau.store.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.PaymentInfo;
import com.jxau.store.payment.mapper.PaymentInfoMapper;
import com.jxau.store.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        String orderSn = paymentInfo.getOrderSn();
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        paymentInfoMapper.updateByExample(paymentInfo,example);
    }
}
