package com.wyn.syt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyn.syt.common.BaseContext;
import com.wyn.syt.common.R;
import com.wyn.syt.dto.DishDto;
import com.wyn.syt.dto.SetmealDto;
import com.wyn.syt.entity.ShoppingCart;
import com.wyn.syt.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        Long id_1 = 1413385247889891330L;
        Long id_2 = 1640254838459699202L;

        //指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //判断当前加入购物车的是菜品还是套餐
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //判断当前菜品或套餐是否在购物车中
        ShoppingCart cartServiceAdd= shoppingCartService.getOne(queryWrapper);


        if(cartServiceAdd != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceAdd.getNumber();
            cartServiceAdd.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceAdd);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceAdd = shoppingCart;
        }

        return R.success(cartServiceAdd);
    }

    /**
     * 减少购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //指定当前用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //判断当前减少的是菜品还是套餐
        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceSub = shoppingCartService.getOne(queryWrapper);

        //判断当前菜品或套餐数量是否为1
        Integer number = cartServiceSub.getNumber();
        if (number == 1){
            cartServiceSub.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceSub);
            shoppingCartService.remove(queryWrapper);
        }else {
            cartServiceSub.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceSub);
        }
        return R.success(cartServiceSub);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}