package com.jxau.store.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jxau.store.annotations.LoginRequired;
import com.jxau.store.beans.OmsCartItem;
import com.jxau.store.beans.OmsOrder;
import com.jxau.store.beans.OmsOrderItem;
import com.jxau.store.beans.UmsMemberReceiveAddress;
import com.jxau.store.service.CartService;
import com.jxau.store.service.OrderService;
import com.jxau.store.service.SkuService;
import com.jxau.store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserService userService;
    @Reference
    CartService cartService;
    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;

    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        if (memberId == null) {
            memberId = "1";
        }
        if (nickname == null) {
            nickname = "test";
        }
        List<UmsMemberReceiveAddress> umsMemberReceiveAddress = userService.getUmsMemberReceiveAddress(memberId);
        List<OmsCartItem> cartList = cartService.getCartList(memberId);
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for (OmsCartItem omsCartItem : cartList
        ) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItemList.add(omsOrderItem);
            }
        }
        modelMap.put("omsOrderItems", omsOrderItemList);
        modelMap.put("userAddressList", umsMemberReceiveAddress);
        modelMap.put("totalAmount", getTotalAmount(cartList));
        String tradeCode = orderService.genTradeCode(memberId);
        modelMap.put("tradeCode", tradeCode);
        return "trade";
    }

    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        if (memberId == null) {
            memberId = "1";
        }
        String success = orderService.checkTradeCode(memberId, tradeCode);
        if (success.equals("success")) {
            List<OmsCartItem> cartList = cartService.getCartList(memberId);
            List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
            //1、对订单信息进行价格验证
            //2、对订单信息进行库存验证
            //3、保存订单详情信息，保存订单信息（保存之后删除购物车的商品信息）
            //4、重定向至支付服务
            String outTradeNo = "storejxau";
            outTradeNo = outTradeNo + System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("YYYYMMDDhhss");
            String date = format.format(new Date());
            outTradeNo = outTradeNo + date;
            for (OmsCartItem omsCartItem : cartList
            ) {
                //1、对订单信息进行价格验证
                String productSkuId = omsCartItem.getProductSkuId();
                BigDecimal price = omsCartItem.getPrice();

                if (omsCartItem.getIsChecked().equals("1")) {
                    boolean b = skuService.checkPrice(productSkuId, price);
                    //2、对订单信息进行库存验证
                    //库存验证
                    if (b) {  //将购物车数据转换成订单详情对象
                        OmsOrderItem omsOrderItem = new OmsOrderItem();
                        omsOrderItem.setProductName(omsCartItem.getProductName());
                        omsOrderItem.setProductPic(omsCartItem.getProductPic());
                        omsOrderItem.setOrderSn(outTradeNo);// 外部订单号，用来和其他系统进行交互，防止重复
                        omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                        omsOrderItem.setProductPrice(price);
                        omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                        omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                        omsOrderItem.setProductSkuCode("111111111111");
                        omsOrderItem.setProductSkuId(productSkuId);
                        omsOrderItem.setProductId(omsCartItem.getProductId());
                        omsOrderItem.setProductSn("仓库对应的商品编号");// 在仓库中的skuId
                        omsOrderItemList.add(omsOrderItem);
                    } else {
                        ModelAndView modelAndView = new ModelAndView("tradeFail");
                        return modelAndView;
                    }
                }
            }
            //订单对象
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getUmsMemberReceiveAddressById(receiveAddressId);
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            //omsOrder.setFreightAmount(); 运费，支付后，在生成物流信息时
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("快点发货");
            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setPayAmount(totalAmount);
            omsOrder.setOrderType(1);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            Date date1 = calendar.getTime();
            omsOrder.setReceiveTime(date1);
            omsOrder.setSourceType("0");
            omsOrder.setStatus("0");
            omsOrder.setOrderType(0);
            omsOrder.setTotalAmount(totalAmount);
            omsOrder.setOmsOrderItems(omsOrderItemList);
            orderService.saveOmsOrder(omsOrder);
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.store.com:8087/index");
            modelAndView.addObject("outTradeNo", outTradeNo);
            modelAndView.addObject("totalAmount", totalAmount);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("tradeFail");
            return modelAndView;
        }

    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

    @RequestMapping("list")
    @LoginRequired(loginSuccess = true)
    public String list(String memberId, ModelMap modelMap) {
        List<OmsOrder> omsOrderList = orderService.getOrderByMemberId(memberId);
        modelMap.put("orderList", omsOrderList);
        return "list";
    }
}
