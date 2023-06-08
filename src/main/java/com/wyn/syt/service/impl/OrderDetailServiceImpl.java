package com.wyn.syt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyn.syt.entity.OrderDetail;
import com.wyn.syt.mapper.OrderDetailMapper;
import com.wyn.syt.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}