package com.wyn.syt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyn.syt.entity.DishFlavor;
import com.wyn.syt.mapper.DishFlavorMapper;
import com.wyn.syt.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
