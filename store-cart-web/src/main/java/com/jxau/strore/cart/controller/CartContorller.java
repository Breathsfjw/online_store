package com.jxau.strore.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jxau.store.beans.OmsCartItem;
import com.jxau.store.beans.PmsSkuInfo;
import com.jxau.store.service.CartService;
import com.jxau.store.service.SkuService;
import com.jxau.store.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartContorller {
    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;
    @RequestMapping("checkCart")
//    public String checkCart(String isChecked,String skuId,HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {

//        String memberId = "1";
//
//        // 调用服务，修改状态
//        OmsCartItem omsCartItem = new OmsCartItem();
//        omsCartItem.setMemberId(memberId);
//        omsCartItem.setProductSkuId(skuId);
//        omsCartItem.setIsChecked(isChecked);
//        cartService.checkCart(omsCartItem);
//
//        // 将最新的数据从缓存中查出，渲染给内嵌页
//        List<OmsCartItem> omsCartItems = cartService.getCartList(memberId);
//        modelMap.put("cartList",omsCartItems);
//        return "cartListInner";
//    }
    public String checkCart(String skuId,String isChecked, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){
        OmsCartItem omsCartItem = new OmsCartItem();
        String memberId = "1";
        // 调用服务，修改状态
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);
        List<OmsCartItem> omsCartItems = cartService.getCartList(memberId);
        modelMap.put("cartList",omsCartItems);
        return "cartListInner";
    }
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        String memberId = "1";
        if (StringUtils.isNotBlank(memberId)){
            omsCartItems=cartService.getCartList(memberId);
        }else {
            String artListCoolie = CookieUtil.getCookieValue(request, "artListCoolie", true);
if (artListCoolie!=null){
    omsCartItems = JSON.parseArray(artListCoolie, OmsCartItem.class);
}
        }
        for (OmsCartItem omsCartItem:
             omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
        modelMap.put("cartList",omsCartItems);
        return "cartList";
    }
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, int quantity) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // 调用商品服务查询商品信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        // 将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        String memberId = "1";//"1" 代表登录 "" 代表未登录;
        if (StringUtils.isBlank(memberId)) {
            // 用户没有登录

            // cookie里原有的购物车数据
//            CookieUtil.setCookie(request,response, "artListCoolie",JSON.toJSONString(omsCartItem),60*60*72,true);
            String artListCoolie = CookieUtil.getCookieValue(request, "artListCoolie", true);
            if (StringUtils.isBlank(artListCoolie)) {
                // cookie为空
                omsCartItems.add(omsCartItem);
            } else {
                // cookie不为空
                omsCartItems = JSON.parseArray(artListCoolie, OmsCartItem.class);
                // 判断添加的购物车数据在cookie中是否存在
                boolean if_exit = if_cart_exist(omsCartItems, omsCartItem);
                if (if_exit) {
                    for (OmsCartItem cartItem :
                            omsCartItems) {
                        // 之前添加过，更新购物车添加数量
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                } else {
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            // 更新cookie
            CookieUtil.setCookie(request, response, "artListCoolie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            // 用户已经登录
            // 从db中查出购物车数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);
            if (omsCartItemFromDb == null) {
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("test小明");
                cartService.addCart(omsCartItem);
            } else {
                omsCartItemFromDb.setQuantity(omsCartItem.getQuantity().add(omsCartItemFromDb.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);

            }
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean exit = false;
        for (OmsCartItem cartItem :
                omsCartItems) {
            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                exit = true;
            }

        }
        return exit;
    }

}
