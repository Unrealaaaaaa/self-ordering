package com.wyn.syt.dto;

import com.wyn.syt.entity.OrderDetail;
import com.wyn.syt.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author Unreal
 * @date 2023/3/22 - 16:29
 */
@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

    //数量
    private int sumNum;
}
