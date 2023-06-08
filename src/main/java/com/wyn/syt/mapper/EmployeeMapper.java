package com.wyn.syt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyn.syt.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}
