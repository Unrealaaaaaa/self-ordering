package com.wyn.syt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyn.syt.entity.Employee;
import com.wyn.syt.mapper.EmployeeMapper;
import com.wyn.syt.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
}
