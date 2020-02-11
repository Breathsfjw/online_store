package com.jxau.store.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.OmsCartItem;
import com.jxau.store.cart.mapper.OmsCartItemMapper;
import com.jxau.store.service.CartService;
import com.jxau.store.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    OmsCartItemMapper omsCartItemMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);
        return omsCartItem1;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        if (StringUtils.isNotBlank(omsCartItem.getMemberId())) {
            omsCartItemMapper.insertSelective(omsCartItem);//避免添加空值
        }
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id",omsCartItemFromDb.getId());
    omsCartItemMapper.updateByExample(omsCartItemFromDb,example);
    }

    @Override
    public void flushCartCache(String memberId) {
        Jedis jedis = null;

        try {
            jedis =redisUtil.getJedis();
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(memberId);
            List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);
            Map<String,String> map=new HashMap<>();
            for (OmsCartItem omsCartItem1:
                    omsCartItems) {
                omsCartItem1.setTotalPrice(omsCartItem1.getPrice().multiply(omsCartItem1.getQuantity()));
                String s = JSON.toJSONString(omsCartItem1);
                map.put(omsCartItem1.getProductSkuId(),s);
            }
            jedis.del("user:"+memberId+":cart");
            jedis.hmset("user:"+memberId+":cart",map);
        }catch (Exception e){
            e.getMessage();
        } finally
        {

            jedis.close();
        }

    }

    @Override
    public List<OmsCartItem> getCartList(String memberId) {
//        Jedis jedis = null;
//        List<OmsCartItem> omsCartItems = new ArrayList<>();
//        try {
//            jedis = redisUtil.getJedis();
//
//            List<String> hvals = jedis.hvals("user:" + userId + ":cart");
//
//            for (String hval : hvals) {
//                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
//                omsCartItems.add(omsCartItem);
//            }
//
//        }catch (Exception e){
//            // 处理异常，记录系统日志
//            e.printStackTrace();
//            //String message = e.getMessage();
//            //logService.addErrLog(message);
//            return null;
//        }finally {
//            jedis.close();
//        }
//
//        return omsCartItems;
        Jedis jedis=null;
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        try {
            jedis=redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
            for (String hval:
                 hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItemList.add(omsCartItem);
            }
        }catch (Exception e){
            e.getMessage();

            return  null;
        }finally {
            jedis.close();
        }
        return omsCartItemList;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
//        Example e = new Example(OmsCartItem.class);
//
//        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
//
//        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);
//
        // 缓存同步
        flushCartCache(omsCartItem.getMemberId());
        Example e=new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("productSkuId",omsCartItem.getProductSkuId()).andEqualTo("memberId",omsCartItem.getMemberId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);
        flushCartCache(omsCartItem.getMemberId());
    }
}