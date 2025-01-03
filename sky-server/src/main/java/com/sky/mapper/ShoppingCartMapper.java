package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询购物车
     * query in shopping cart
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改购物车中某个物品的数量
     * update item’s quantity in shopping cart by id
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 加入购物车
     * add item to shopping cart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

//    /**
//     * 清空购物车
//     * Clear the shopping cart
//     */
//    @Delete("delete from shopping_cart where user_id = #{userId}")
//    void deleteByUserId(Long userId);

    /**
     * 删除购物车中指定的商品
     * remove specific item from shopping cart
     */
    void delete(ShoppingCart shoppingCart);

    /**
     * 批量插入购物车
     * insert items into shopping cart in batches
     */
    void insertInBatches(List<ShoppingCart> shoppingCartList);
}
