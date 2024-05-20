package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     *
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品: {}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据条件分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("根据条件分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("根据条件分页查询菜品: {}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<String> ids){
        log.info("删除菜品: {}",ids);
        dishService.deleteDishByIds(ids);
        return Result.success();
    }

    /**
     * 启用或者停售菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或停售菜品")
    public Result startOrStop(@PathVariable Integer status,String id){
        log.info("启用或者停售菜品,{},{}",status,id);
        //封装实体类
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishService.startOrStop(dish);
        return Result.success();
    }

    /**
     * 根据id查询菜品详细信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品详细信息")
    public Result<DishVO> getDetailById(@PathVariable String id){
        log.info("根据id查询菜品详细信息: {}",id);
        DishVO dish = dishService.getDetailById(id);
        return Result.success(dish);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息: {}",dishDTO);
        //修改菜品信息，修改菜品味道信息
        dishService.updateDishForDetail(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类或者名字查询菜品
     * @param dishDTO
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类或者名字查询菜品")
    public Result<List<Dish>> list(DishDTO dishDTO){
        log.info("根据分类或者名字查询菜品: {}",dishDTO);
        List<Dish> dishList = dishService.getDishListByCondition(dishDTO);
        return Result.success(dishList);
    }


}
