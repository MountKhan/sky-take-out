package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "C端-购物车接口")
@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加商品进入购物车
     * add item to shopping cart
     */
    @PostMapping("/add")
    @ApiOperation("添加进购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加进购物车，商品信息为{}",shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * view shopping cart
     */
    @GetMapping("/list")
    @ApiOperation(value = "查看购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("查询当前用户的所有购物车数据");
        List<ShoppingCart> list = shoppingCartService.showShoppingCart();
        return Result.success(list);
    }

    /**
     * 清空购物车
     * Clear the shopping cart
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空购物车")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }

    /**
     * 删除购物车中指定的的一个商品
     * remove a specific item from shopping cart
     */
    @PostMapping("/sub")
    @ApiOperation(value = "删除购物车中指定的一个商品")
    public Result remove(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中指定的一个商品，{}",shoppingCartDTO);
        shoppingCartService.remove(shoppingCartDTO);
        return Result.success();
    }
}
