package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.UUIDUtil;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


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
        dishFlavorMapper.insertDishFlavorByList(flavors);
    }

    /**
     * 根据条件菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectDishForPageByCondition(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据菜品id删除菜品
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDishByIds(List<String> ids) {
        //如果是启用状态,status为1，不能删除
        for (String id : ids) {
            Dish dish = dishMapper.getById(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //如果是被套餐关联了不能删除
        List<String> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //先删除菜品味道表中的数据
        dishFlavorMapper.deleteDishFlavorByDishIds(ids);
        //再删除菜品
        dishMapper.deleteDishByIds(ids);
    }

    /**
     * 启用或者禁用菜品
     * @param dish
     * @return
     */
    @Override
    public Integer startOrStop(Dish dish) {
        return dishMapper.updateDish(dish);
    }

    /**
     * 根据id查询菜品详细信息
     * @param id
     * @return
     */
    @Override
    public DishVO getDetailById(String id) {
        return dishMapper.selectDishForDetailById(id);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDishForDetail(DishDTO dishDTO) {
        //封装实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        System.out.println(dish);
        //修改菜品信息
        dishMapper.updateDish(dish);

        //修改菜品味道信息
        //先删除原来的味道信息
        List<String> dishIds = new ArrayList<>();
        dishIds.add(dish.getId());
        dishFlavorMapper.deleteDishFlavorByDishIds(dishIds);
        //插入现在的味道信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors == null || flavors.isEmpty()){
            return;
        }
        flavors.forEach(f -> {
            f.setId(UUIDUtil.getUUID());
            f.setDishId(dish.getId());
        });
        dishFlavorMapper.insertDishFlavorByList(flavors);
    }

    @Override
    public List<Dish> getDishListByCondition(DishDTO dishDTO) {
        //封装dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        return dishMapper.selectDishForListByCondition(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        //封装dish
        List<Dish> dishList = dishMapper.selectDishForListByCondition(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectDishFlavorByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
