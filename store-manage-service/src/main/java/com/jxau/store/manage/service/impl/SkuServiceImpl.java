package com.jxau.store.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.PmsSkuAttrValue;
import com.jxau.store.beans.PmsSkuImage;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.beans.PmsSkuSaleAttrValue;
import com.jxau.store.manage.mapper.PmsSkuAttrValueMapper;
import com.jxau.store.manage.mapper.PmsSkuImageMapper;
import com.jxau.store.manage.mapper.PmsSkuInfoMapper;
import com.jxau.store.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.jxau.store.mq.ActiveMQUtil;
import com.jxau.store.service.SkuService;
import com.jxau.store.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {
    /**
     * @author fjw
     * 商品发布后台管理系统
     * sku：商品库存单元，商品库存单元管理
     * 保存商品库存单元信息：saveSkuInfo
     */

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ActiveMQUtil activeMQUtil;

    //保存商品存储单元的信息
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {



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
            // 插入skuInfo
            int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
            String skuId = pmsSkuInfo.getId();

            // 插入平台属性关联
            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                pmsSkuAttrValue.setSkuId(skuId);
                pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
            }

            // 插入销售属性关联
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                pmsSkuSaleAttrValue.setSkuId(skuId);
                pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
            }

            // 插入图片信息
            List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
            for (PmsSkuImage pmsSkuImage : skuImageList) {
                pmsSkuImage.setSkuId(skuId);
                pmsSkuImageMapper.insertSelective(pmsSkuImage);
            }
            // 修改完商品sku信息，将修改后的信息通过消息队列同步到elastic
            // 调用mq发送sku商品信息修改的消息
            Queue pmsskuinfo_uppdate_queue = session.createQueue("PMSSKUINFO_UPPDATE_QUEUE");
            MessageProducer producer = session.createProducer(pmsskuinfo_uppdate_queue);
            TextMessage textMessage = new ActiveMQTextMessage();//字符串文本
            //MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            // 将sku商品对象，转化成json字符串，存入ORDER_PAY_QUEUE的消息队列
            textMessage.setText(JSON.toJSONString(pmsSkuInfo));
            producer.send(textMessage);
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

    @Override
    public PmsSkuInfo getSkuById(String skuId) {

        /**
         * 代码优化，加入缓存技术
         * 通过redis将sku数据存入redis
         * 减少对db数据库的访问
         */

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        // 链接缓存
        Jedis jedis = redisUtil.getJedis();
        // 查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        if (StringUtils.isNotBlank(skuJson)) {//if(skuJson!=null&&!skuJson.equals(""))

            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        } else {
            // 如果缓存中没有，查询mysql

            // 设置分布式锁
            String token = UUID.randomUUID().toString();
            String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);// 拿到锁的线程有10秒的过期时间
            if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                // 设置成功，有权在10秒的过期时间内访问数据库

                pmsSkuInfo = getSkuByIdFromDb(skuId);

                if (pmsSkuInfo != null) {
                    // mysql查询结果存入redis
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                } else {
                    // 数据库中不存在该sku
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                }

                // 在访问mysql后，将mysql的分布锁释放
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)) {
                    //jedis.eval("lua");可与用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                    jedis.del("sku:" + skuId + ":lock");// 用token确认删除的是自己的sku的锁
                }
            } else {
                // 设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    /**
     * 引入Redis查询商品存储单元的信息
     * 如果缓存中没有，查询mysql
     */
    private PmsSkuInfo getSkuByIdFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo1.setSkuImageList(pmsSkuImages);
        return pmsSkuInfo1;
    }

    //根据标准商品单元ID查询商品存储单元集合信息
    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }


    //获取所有商品库存单元的信息
    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo :
                pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);

        }

        return pmsSkuInfos;
    }


    //订单提交时，为防止价格不一致对商品进行检验
    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        boolean b = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        BigDecimal price1 = pmsSkuInfo1.getPrice();
        if (price1.compareTo(price) == 0) {
            b = true;
        }
        return b;
    }
}
