package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(String categoryId) {
        log.info("根据分类id查询菜品: {}",categoryId);
        // 在高并发的情况下有很多用户同时进行查询操作，同一时间会有上百条设置上千条的sql查询，对于数据库来说，压力非常大，
        // 所以这个时候就应该将菜品数据放入缓存中，每次查询先从缓存中查询，如果没有就查询数据库，添加到缓存中，如有有直接从缓存中查

        //构造redis中的key, 规则: dish_分类id
        String key = "dish_" + categoryId;

        //查询redis中是否存在菜品数据
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        List<DishVO> list = (List<DishVO>)valueOperations.get(key);
        if(list != null && !list.isEmpty()){
            //如果存在，直接返回
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        //如果不存在，查询数据库，添加到缓存
        list = dishService.listWithFlavor(dish);
        valueOperations.set(key,list);

        return Result.success(list);
    }

}
