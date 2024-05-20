package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和口味
     * @param dishDTO
     * @return
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 根据条件菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据菜品id删除菜品
     * @param ids
     * @return
     */
    void deleteDishByIds(List<String> ids);

    /**
     * 启用或者停售菜品
     * @param dish
     * @return
     */
    Integer startOrStop(Dish dish);

    /**
     * 根据id查询详细信息
     * @param id
     * @return
     */
    DishVO getDetailById(String id);

    /**
     * 更新菜品信息，包括口味
     * @param dishDTO
     */
    void updateDishForDetail(DishDTO dishDTO);

    List<Dish> getDishListByCondition(DishDTO dishDTO);
}
