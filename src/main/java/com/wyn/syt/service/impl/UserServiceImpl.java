package com.wyn.syt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyn.syt.entity.User;
import com.wyn.syt.mapper.UserMapper;
import com.wyn.syt.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
