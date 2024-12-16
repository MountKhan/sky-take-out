package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加商品进入购物车
     * add item to shopping cart
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * view shopping cart
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     * Clear the shopping cart
     */
    void clean();

    /**
     * 删除购物车中指定的的一个商品
     * remove a specific item from shopping cart
     */
    void remove(ShoppingCartDTO shoppingCartDTO);
}
