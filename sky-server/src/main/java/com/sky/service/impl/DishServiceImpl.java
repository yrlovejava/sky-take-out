package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.utils.UUIDUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和口味
     * @param dishDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
        //将dto转化为实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setId(UUIDUtil.getUUID());
        //默认是起售
        dish.setStatus(StatusConstant.ENABLE);

        //将菜品信息存入到菜品表中
        dishMapper.insertDish(dish);

        //将菜品口味表存在flavor表中
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors == null || flavors.isEmpty()){
            return;
        }

        flavors.forEach(d -> {
            d.setId(UUIDUtil.getUUID());
            d.setDishId(dish.getId());
        });
        dishFlavorMapper.insertDishFlavorByList(dishDTO.getFlavors());
    }
}
