package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartService {

    /**
     * 添加商品进入购物车
     * add item to shopping cart
     */
    void add(ShoppingCartDTO shoppingCartDTO);
}
