package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id，查询套餐id
     * @param dishIds
     * @return
     */
    List<String> getSetmealIdsByDishIds(List<String> dishIds);

    /**
     * 批量插入菜品和套餐的关系
     * @param setmealDishList
     * @return
     */
    Integer insertByList(List<SetmealDish> setmealDishList);
}
