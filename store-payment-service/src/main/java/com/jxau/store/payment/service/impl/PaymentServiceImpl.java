package com.jxau.store.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jxau.store.beans.PaymentInfo;
import com.jxau.store.mq.ActiveMQUtil;
import com.jxau.store.payment.mapper.PaymentInfoMapper;
import com.jxau.store.service.PaymentService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;
    @Autowired
    AlipayClient alipayClient;

    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    //支付完成更新支付信息，并发布队列让订单服务更改订单信息
    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        PaymentInfo paymentInfo1 = new PaymentInfo();
        paymentInfo1.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfo2 = paymentInfoMapper.selectOne(paymentInfo1);
        if (paymentInfo2 != null && StringUtils.isNotBlank(paymentInfo2.getPaymentStatus()) && paymentInfo2.getPaymentStatus().equals("已支付")) {
            return;
        } else {
            String orderSn = paymentInfo.getOrderSn();
            Example example = new Example(PaymentInfo.class);
            example.createCriteria().andEqualTo("orderSn", orderSn);
            ConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            try {
                connectionFactory = activeMQUtil.getConnectionFactory();
                connection = connectionFactory.createConnection();
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
                // 支付成功后，引起的系统服务-》订单服务的更新-》库存服务-》物流服务
                // 调用mq发送支付成功的消息
                Queue payhment_success_queue = session.createQueue("PAYHMENT_SUCCESS_QUEUE");
                MessageProducer producer = session.createProducer(payhment_success_queue);
                //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本

                MapMessage mapMessage = new ActiveMQMapMessage();// hash结构
                mapMessage.setString("out_trade_no", paymentInfo.getOrderSn());
                producer.send(mapMessage);
                session.commit();

            } catch (JMSException e) {
                e.printStackTrace();
                // 消息回滚
                try {
                    session.rollback();
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            } finally {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //通过activeMq 的延迟队列发布定时任务对支付状态进行检查
    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo, int i) {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        try {
            connectionFactory = activeMQUtil.getConnectionFactory();
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Queue payhment_check_queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(payhment_check_queue);
            //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本

            MapMessage mapMessage = new ActiveMQMapMessage();// hash结构
            mapMessage.setString("out_trade_no", outTradeNo);
            mapMessage.setInt("count", i);
            // 为消息加入延迟时间
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 60);
            producer.send(mapMessage);
            session.commit();

        } catch (JMSException e) {
            e.printStackTrace();
            // 消息回滚
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }


    //调用支付宝的支付查询接口查询支付状态
    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {
        Map<String, Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no", out_trade_no);
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
            resultMap.put("out_trade_no", response.getOutTradeNo());
            //支付宝交易订单号
            resultMap.put("trade_no", response.getTradeNo());
            resultMap.put("trade_status", response.getTradeStatus());
            resultMap.put("call_back_content", response.getMsg());
        } else {
            System.out.println("调用失败");
        }
        return resultMap;
    }
}
