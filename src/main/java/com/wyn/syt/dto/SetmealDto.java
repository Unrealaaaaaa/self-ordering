package com.wyn.syt.dto;

import com.wyn.syt.entity.Setmeal;
import com.wyn.syt.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    //分类名称
    private String categoryName;
}
