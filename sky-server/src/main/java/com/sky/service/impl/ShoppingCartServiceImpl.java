package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishItemVO;
import org.apache.commons.collections4.BagUtils;
import org.apache.poi.hssf.record.DVALRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加商品进入购物车
     * add item to shopping cart
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //当前加入购物车的商品是否存在
        //Check if the current item added to the shopping cart already exists
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);


        //存在->数量+1
        //exist:quantity up
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);

            shoppingCartMapper.updateNumberById(cart);
        } else {
            //不存在->加入购物车
            //not exist:add to shopping cart

            //判断当前物品是菜品还是套餐
            //Determine whether the item is a dish or a setmeal
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();

            //菜品：在dish表格中获取详细信息
            //dish:get detail message in dish schema
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //套餐：在setmeal表格中获取详细信息
                //setmeal:get detail message in setmeal schema
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //加入购物车
            //add to shopping cart
            shoppingCartMapper.insert(shoppingCart);

        }
    }

    /**
     * 查看购物车
     * view shopping cart
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取当前用户id
        //get userId
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     * Clear the shopping cart
     */
    @Override
    public void clean() {
        //获取当前用户id
        //get userId
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //避免shoppingCart的所有属性都为空时删除数据库
        //To avoid deleting the database when all properties of the `shoppingCart` object are empty.
        if(shoppingCart.getDishId() == null &&
                shoppingCart.getSetmealId() == null &&
                shoppingCart.getDishFlavor() == null &&
                shoppingCart.getUserId() == null) {
            throw new DeletionNotAllowedException(MessageConstant.INVALID_SHOPPING_CART_DELETION);
        }

        shoppingCartMapper.delete(shoppingCart);
    }

    /**
     * 删除购物车中指定的的一个商品
     * remove a specific item from shopping cart
     */
    @Override
    public void remove(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());


        //查询购物车内这个商品的数量
        //Check the quantity of this item in the shopping cart.
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && !list.isEmpty()){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();

            //数量为1->删除
            //number == 1 --> delete
            if(number == 1){
                shoppingCartMapper.delete(shoppingCart);
            } else {
                //数量大于1->数量减一
                //number > 1 --> number - 1
                shoppingCart.setNumber(number-1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }


    }
}
