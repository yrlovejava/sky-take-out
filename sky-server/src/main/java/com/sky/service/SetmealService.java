package com.sky.service;

import com.sky.dto.SetmealDTO;
import org.springframework.stereotype.Service;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    void save(SetmealDTO setmealDTO);
}
