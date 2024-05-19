package com.sky.service;

import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    /**
     * 新增菜品口味
     * @param dishFlavorList
     * @return
     */
    Integer saveByList(List<DishFlavor> dishFlavorList);
}
