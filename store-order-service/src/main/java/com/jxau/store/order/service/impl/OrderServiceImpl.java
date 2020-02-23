package com.jxau.store.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.jxau.store.beans.OmsOrder;
import com.jxau.store.beans.OmsOrderItem;
import com.jxau.store.order.mapper.OmsOrderItemMapper;
import com.jxau.store.order.mapper.OmsOrderMapper;
import com.jxau.store.service.CartService;
import com.jxau.store.service.OrderService;
import com.jxau.store.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

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
}
