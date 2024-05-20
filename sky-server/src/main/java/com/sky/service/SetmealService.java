package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 根据条件分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);
}
