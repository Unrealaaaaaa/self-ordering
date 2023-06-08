package com.wyn.syt.dto;

import com.wyn.syt.entity.User;
import lombok.Data;

/**
 * @author Unreal
 * @date 2023/3/20 - 16:46
 */
@Data
public class UserDto extends User {

    //验证码
    private String code;
}
