package com.sky.service.impl;

import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DIshFlavorServiceImpl implements DishFlavorService {

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品口味
     * @param dishFlavorList
     * @return
     */
    @Override
    public Integer saveByList(List<DishFlavor> dishFlavorList) {
        return dishFlavorMapper.insertDishFlavorByList(dishFlavorList);
    }
}
