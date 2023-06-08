package com.wyn.syt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyn.syt.dto.SetmealDto;
import com.wyn.syt.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐并保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐并删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询该套餐的信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
