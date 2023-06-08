package com.wyn.syt.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyn.syt.common.R;
import com.wyn.syt.dto.DishDto;
import com.wyn.syt.dto.OrdersDto;
import com.wyn.syt.entity.Category;
import com.wyn.syt.entity.Dish;
import com.wyn.syt.entity.OrderDetail;
import com.wyn.syt.entity.Orders;
import com.wyn.syt.service.OrderDetailService;
import com.wyn.syt.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 订单信息分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Transactional
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage=new Page<>();

        //根据订单号进行模糊查询
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(number),Orders::getNumber,number);

        //根据时间进行范围查询
        if(beginTime!=null&&endTime!=null){
            queryWrapper.ge(Orders::getOrderTime,beginTime);
            queryWrapper.le(Orders::getOrderTime,endTime);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //进行分页查询
        orderService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders> records=pageInfo.getRecords();
        //写入新的属性并返回
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);
            String name="用户"+item.getUserId();
            ordersDto.setUserName(name);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> make(@RequestBody Orders orders){
        //获取要修改的订单id
        Long id = orders.getId();
        Integer status = orders.getStatus();
        //查询要修改的订单
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,id);
        //修改状态码
        Orders one = orderService.getOne(queryWrapper);
        one.setStatus(status);
        orderService.updateById(one);

        return R.success("订单状态修改成功");
    }

    /**
     * 用户订单
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //进行分页查询
        orderService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records=pageInfo.getRecords();

        List<OrdersDto> list = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);
            Long Id = item.getId();
            //根据id查分类对象
            Orders orders = orderService.getById(Id);
            String number = orders.getNumber();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,number);
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
            int num=0;

            for(OrderDetail l:orderDetailList){
                num+=l.getNumber().intValue();
            }

            ordersDto.setSumNum(num);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }
}