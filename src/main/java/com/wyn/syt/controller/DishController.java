package com.wyn.syt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyn.syt.common.BaseContext;
import com.wyn.syt.common.R;
import com.wyn.syt.dto.DishDto;
import com.wyn.syt.entity.*;
import com.wyn.syt.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderService orderService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //分页查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        //根据id查找当前要修改菜品的基本信息并将结果返回
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 菜品状态修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> alterStatus(@PathVariable int status, String[] ids){
        //通过数组获取占位符中的id并将其对应的状态码进行修改
        for(String id: ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("菜品状态修改成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String[] ids){
        for (String id: ids){
            dishService.removeById(id);
        }

        return R.success("删除成功");
    }

    /**
     * 根据条件查询菜品信息
     * @param dish
     * @return
     */
    @Transactional
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        Long currentId = BaseContext.getCurrentId();

        String id = dish.getCategoryId().toString();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //推荐菜单
        if (id.equals("1638747381560307714")) {

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getUserId,currentId);
            List<OrderDetail> details = orderDetailService.list(wrapper);

            //查询所有菜品的排序值
            List<Dish> dishList = dishService.list();
            List<Integer> list_1 = dishList.stream().map((item) -> {
                Integer sort_dish = item.getSort();
                return sort_dish;
            }).collect(Collectors.toList());

            List<Long> list_id = new ArrayList<>();
            for (Dish dish1 : dishList){
                list_id.add(dish1.getId());
            }

            //获取用户关于菜品的排序值
            /*List<Integer> list_2 = details.stream().map((item) -> {
                Long dishId = item.getDishId();
                Dish dish2 = dishService.getById(list_id.get(n));
                Integer sort_1 = dish2.getSort();
                n = n + 1;
                boolean contains = Arrays.asList(list_id).contains(dishId);
                if (contains){
                    Dish dish3 = dishService.getById(dishId);
                    Integer sort_2 = dish3.getSort();
                    dish2.setSort(sort_2 + item.getSort());
                }
                return sort_1;
            }).collect(Collectors.toList());*/
            for (OrderDetail orderDetail : details){

                Long dishId = orderDetail.getDishId();
                Dish dish2 = dishService.getById(dishId);

                Long categoryId = dish2.getCategoryId();
                LambdaQueryWrapper<Dish> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(Dish::getCategoryId,categoryId);
                List<Dish> list = dishService.list(wrapper1);
                for (Dish dish3 : list){
                    Integer sort_3 = dish3.getSort();
                    dish3.setSort(sort_3 + 10);
                    dishService.updateById(dish3);
                }

                Integer sort = dish2.getSort();
                dish2.setSort(sort + orderDetail.getSort());
                dishService.updateById(dish2);
            }



            queryWrapper.gt(Dish::getSort,0);
            queryWrapper.eq(Dish::getStatus,1).ne(Dish::getCategoryId,"1413384954989060097").ne(Dish::getCategoryId,"1413341197421846529");

            List<Dish> dishes = dishService.list(queryWrapper);

            queryWrapper.orderByDesc(Dish::getSort);
            queryWrapper.last("limit 0,8");

            List<Dish> list = dishService.list(queryWrapper);

            //将查询的Dish菜品实体拷贝到DishDto中
            List<DishDto> dishDtoList = list.stream().map((item) -> {
                DishDto dishDto = new DishDto();

                BeanUtils.copyProperties(item, dishDto);

                Long categoryId = item.getCategoryId();
                //根据id查询分类对象
                Category category = categoryService.getById(categoryId);
                //并设置分类id
                if (category != null) {
                    String categoryName = category.getName();
                    dishDto.setCategoryName(categoryName);
                }

                //通过菜品的id来查询菜品的口味信息被将其返回
                Long dishId = item.getId();
                LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
                List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
                dishDto.setFlavors(dishFlavorList);
                return dishDto;
            }).collect(Collectors.toList());

            for (int n = 0; n < dishList.size(); n++){
                int m = list_1.get(n);
                Dish dish1 = dishList.get(n);
                dish1.setSort(m);
                dishService.updateById(dish1);
            }

            return R.success(dishDtoList);
        }else {
            //查询在售的菜品
            queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
            queryWrapper.eq(Dish::getStatus, 1);
            queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
            List<Dish> list = dishService.list(queryWrapper);

            //将查询的Dish菜品实体拷贝到DishDto中
            List<DishDto> dishDtoList = list.stream().map((item) -> {
                DishDto dishDto = new DishDto();

                BeanUtils.copyProperties(item, dishDto);

                Long categoryId = item.getCategoryId();
                //根据id查询分类对象
                Category category = categoryService.getById(categoryId);
                //并设置分类id
                if (category != null) {
                    String categoryName = category.getName();
                    dishDto.setCategoryName(categoryName);
                }

                //通过菜品的id来查询菜品的口味信息被将其返回
                Long dishId = item.getId();
                LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
                List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
                dishDto.setFlavors(dishFlavorList);
                return dishDto;
            }).collect(Collectors.toList());
            return R.success(dishDtoList);
        }


    }

}
