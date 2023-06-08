package com.wyn.syt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyn.syt.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}