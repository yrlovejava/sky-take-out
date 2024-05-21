package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品口味
     * @param dishFlavorList
     * @return
     */
    Integer insertDishFlavorByList(List<DishFlavor>  dishFlavorList);

    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> selectDishFlavorByDishId(String dishId);

    Integer deleteDishFlavorByDishIds(List<String> dishIds);

}
