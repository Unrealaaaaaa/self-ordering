package com.wyn.syt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyn.syt.entity.ShoppingCart;
import com.wyn.syt.mapper.ShoppingCartMapper;
import com.wyn.syt.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
