package com.wyn.syt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyn.syt.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
