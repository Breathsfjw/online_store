package com.jxau.store.service;

import com.jxau.store.beans.OmsCartItem;

import java.util.List;

public interface CartService {
     OmsCartItem ifCartExistByUser(String memberId, String skuId) ;

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> getCartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
