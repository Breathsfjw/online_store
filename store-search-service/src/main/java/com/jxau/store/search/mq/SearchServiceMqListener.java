package com.jxau.store.search.mq;

import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@SuppressWarnings("ALL")
@Component
public class SearchServiceMqListener {
    @Autowired
    SearchService searchService;

    @JmsListener(destination = "PMSSKUINFO_UPPDATE_QUEUE", containerFactory = "jmsQueueListener")
    public void consumeElasticUpdateResult(TextMessage textMessage) throws JMSException {
        String pmsSkuInfoTaskJson = textMessage.getText();

        /***
         * 转化并保存订单对象
         */
        PmsSkuInfo pmsSkuInfo = JSON.parseObject(pmsSkuInfoTaskJson, PmsSkuInfo.class);
        searchService.updateElastic(pmsSkuInfo);
    }
}
