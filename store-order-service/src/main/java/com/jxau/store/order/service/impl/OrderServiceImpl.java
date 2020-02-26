package com.jxau.store.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.OmsOrder;
import com.jxau.store.beans.OmsOrderItem;
import com.jxau.store.mq.ActiveMQUtil;
import com.jxau.store.order.mapper.OmsOrderItemMapper;
import com.jxau.store.order.mapper.OmsOrderMapper;
import com.jxau.store.service.CartService;
import com.jxau.store.service.OrderService;
import com.jxau.store.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper orderItemIMapper;
    @Reference
    CartService cartService;
    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public String genTradeCode(String memberId) {
        Jedis jedis = null;
        String str = UUID.randomUUID().toString();
        try {
            jedis = redisUtil.getJedis();
            String key = "user" + memberId + "tradeCode";
            jedis.setex(key, 60 * 15, str);
        } finally {
            jedis.close();
        }
        return str;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        String success = "";
        Jedis jedis = null;
        String str = UUID.randomUUID().toString();
        try {
            jedis = redisUtil.getJedis();
            String key = "user" + memberId + "tradeCode";

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(key), Collections.singletonList(tradeCode));
            if (eval != null & eval != 0) {
                success = "success";
            } else {
                success = "fail";
            }

        } finally {
            jedis.close();
        }
        return success;
    }

    //保存订单信息与订单详情信息(保存完需删除购物车中的信息)
    @Override
    public void saveOmsOrder(OmsOrder omsOrder) {
        omsOrderMapper.insertSelective(omsOrder);
        String id = omsOrder.getId();
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem :
                omsOrderItems) {
            omsOrderItem.setOrderId(id);
            String productSkuId = omsOrderItem.getProductSkuId();
            orderItemIMapper.insertSelective(omsOrderItem);
            // 删除购物车数据
            // cartService.delCart();
            cartService.delCart(productSkuId);
        }
    }

    @Override
    public OmsOrder getOrderByOrderSn(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        return omsOrderMapper.selectOne(omsOrder);
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());
        OmsOrder omsOrderUpdate = new OmsOrder();
        omsOrderUpdate.setStatus("1");
//        Example e = new Example(OmsOrder.class);
//        e.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());
//
//        OmsOrder omsOrderUpdate = new OmsOrder();
//
//        omsOrderUpdate.setStatus("1");
        // 发送一个订单已支付的队列，提供给库存消费
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            omsOrderMapper.updateByExampleSelective(omsOrderUpdate, example);
            Queue order_pay_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(order_pay_queue);
            TextMessage textMessage = new ActiveMQTextMessage();//字符串文本
            //MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            // 查询订单的对象，转化成json字符串，存入ORDER_PAY_QUEUE的消息队列
            OmsOrder omsOrderParam = new OmsOrder();
            omsOrderParam.setOrderSn(omsOrder.getOrderSn());
            OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrderParam);
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setOrderSn(omsOrder1.getOrderSn());
            List<OmsOrderItem> select = orderItemIMapper.select(omsOrderItem);
            omsOrder1.setOmsOrderItems(select);
            textMessage.setText(JSON.toJSONString(omsOrder1));
            producer.send(textMessage);
            session.commit();
        } catch (JMSException e) {
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public List<OmsOrder> getOrderByMemberId(String memberId) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setMemberId(memberId);
        List<OmsOrder> omsOrderList = omsOrderMapper.select(omsOrder);
        return omsOrderList;
    }
}
