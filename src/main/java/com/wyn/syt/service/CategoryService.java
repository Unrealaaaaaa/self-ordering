package com.wyn.syt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyn.syt.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
